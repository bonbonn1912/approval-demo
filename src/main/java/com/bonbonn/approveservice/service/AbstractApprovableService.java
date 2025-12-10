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

    if (entity.getApproveType() == ApproveType.CREATE) {
      approveCreate(id);
    } else if (entity.getDraft() != null) {
      approveUpdate(id);
    } else {
      throw new IllegalStateException("No approval action required for entity id=" + id);
    }
  }

  protected void addAuditDetails(E entity) {
    entity.setCreatedBy(getCurrentUser());
    entity.setCreatedAt(LocalDateTime.now());
  }

  private void approveCreate(Long id) {
    E entity = repository.findById(id).orElseThrow();
    String currentUser = getCurrentUser();

    ensureDifferentUser(entity, currentUser);
    entity.setApproveType(null);
    repository.save(entity);
  }

  protected abstract TypeReference<E> getTypeReference();

  private void approveUpdate(Long id) {
    E entity = repository.findById(id).orElseThrow();
    E newEntity = fromJson(entity.getDraft());
    String currentUser = getCurrentUser();

    ensureDifferentUser(newEntity, currentUser);
    newEntity.clearPendingDrafts();
    newEntity.setApprovedBy(currentUser);
    repository.delete(entity);
    repository.save(newEntity);
  }


  @Transactional
  public void reject(Long id) {
    E entity = repository.findById(id).orElseThrow();
    if (entity.getApprovedBy() != null) {
      throw new IllegalStateException(entity.getClass().getSimpleName() + " already approved");
    }
    entity.clearPendingDrafts();
    repository.save(entity);
  }

  protected String getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getName();
  }

  protected void setDraft(E entity, E draft) {
    addAuditDetails(draft);
    entity.setDraft(toJson(draft));
  }

  private String toJson(E entity) {
    try{
      return this.objectMapper.writeValueAsString(entity);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public List<E> getOpenDrafts(List<E> entities) {
    if (entities == null || entities.isEmpty()) {
      return List.of();
    }
    List<E> drafts = entities.stream()
        .map(E::getDraft)
        .filter(Objects::nonNull)
        .map(this::fromJson)
        .toList();
    return Stream.of(drafts, entities)
        .flatMap(List::stream)
        .toList();
  }

  private E fromJson(String json) {
    if (json == null) {
      return null;
    }
    try {
      return this.objectMapper.readValue(json, getTypeReference());
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(
          "Could not deserialize JSON", e);
    }
  }


  private void ensureDifferentUser(E entity, String currentUser) {
    if (entity.getCreatedBy() == null) {
      return;
    }
    if (entity.getCreatedBy().equalsIgnoreCase(currentUser)) {
      throw new IllegalStateException(
          "Action not allowed: creator '" + currentUser + "' cannot approve or reject entity.");
    }
  }
}
