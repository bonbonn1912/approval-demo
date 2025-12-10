package com.bonbonn.approveservice.service;

import com.bonbonn.approveservice.data.AbstractApprovableEntity;
import com.bonbonn.approveservice.data.ApproveType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractApprovableService<
    E extends AbstractApprovableEntity, R extends JpaRepository<E, Long>> {

  protected final R repository;
  protected final ObjectMapper objectMapper;

  protected AbstractApprovableService(R repository, ObjectMapper objectMapper) {
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public void approve(Long id) {
    E entity = repository.findById(id).orElseThrow();
    ApproveType approveType = entity.getApproveType();

    if (approveType == ApproveType.CREATE) {
      approveCreate(entity);
      return;
    }

    E draft = fromJson(entity.getDraft());
    if (draft == null || draft.getApproveType() == null) {
      throw new IllegalStateException("Draft or draft approveType is null for id " + id);
    }

    switch (draft.getApproveType()) {
      case UPDATE -> approveUpdate(entity, draft);
      case DELETE -> approveDelete(entity);
      default ->
          throw new IllegalStateException(
              "Unsupported draft approveType: " + draft.getApproveType());
    }
  }

  @Transactional
  public void reject(Long id) {
    E entity = repository.findById(id).orElseThrow();

    if (entity.getApproveType() == ApproveType.CREATE) {
      repository.delete(entity);
      return;
    }

    if (entity.getDraft() != null) {
      E draft = fromJson(entity.getDraft());
      if (draft != null && draft.getApproveType() == ApproveType.DELETE) {
        entity.clearPendingDrafts();
        repository.save(entity);
        return;
      }
    }

    ensureNotAlreadyApproved(entity);
    repository.save(entity);
  }

  public List<E> getOpenDrafts(List<E> entities) {
    if (entities == null || entities.isEmpty()) {
      return List.of();
    }

    List<E> drafts =
        entities.stream()
            .map(E::getDraft)
            .filter(Objects::nonNull)
            .map(this::fromJson)
            .filter(Objects::nonNull)
            .toList();

    return Stream.concat(drafts.stream(), entities.stream()).toList();
  }

  protected abstract TypeReference<E> getTypeReference();

  private void approveCreate(E entity) {
    String currentUser = getCurrentUser();
    ensureDifferentUser(entity, currentUser);

    entity.setApprovedBy(currentUser);
    entity.clearPendingDrafts();
    repository.save(entity);
  }

  private void approveUpdate(E original, E draft) {
    String currentUser = getCurrentUser();
    ensureDifferentUser(draft, currentUser);

    draft.setApprovedBy(currentUser);
    draft.clearPendingDrafts();

    repository.delete(original);
    repository.save(draft);
  }

  private void approveDelete(E entity) {
    String currentUser = getCurrentUser();
    ensureDifferentUser(entity, currentUser);
    repository.delete(entity);
  }

  protected void addAuditDetails(E entity) {
    entity.setCreatedBy(getCurrentUser());
    entity.setCreatedAt(LocalDateTime.now());
  }

  protected void setDraft(E entity, E draft) {
    addAuditDetails(draft);
    entity.setDraft(toJson(draft));
  }

  protected String getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getName();
  }

  private void ensureDifferentUser(E entity, String currentUser) {
    String createdBy = entity.getCreatedBy();
    if (createdBy == null) {
      return;
    }
    if (createdBy.equalsIgnoreCase(currentUser)) {
      throw new IllegalStateException(
          "Action not allowed: creator '"
              + currentUser
              + "' cannot approve or reject entity.");
    }
  }

  private void ensureNotAlreadyApproved(E entity) {
    if (entity.getApprovedBy() != null) {
      throw new IllegalStateException(
          entity.getClass().getSimpleName() + " already approved");
    }
  }

  private String toJson(E entity) {
    try {
      return objectMapper.writeValueAsString(entity);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Could not serialize entity to JSON", e);
    }
  }

  private E fromJson(String json) {
    if (json == null) {
      return null;
    }
    try {
      return objectMapper.readValue(json, getTypeReference());
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Could not deserialize JSON", e);
    }
  }
}