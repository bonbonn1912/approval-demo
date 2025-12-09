package com.bonbonn.approveservice.service;

import com.bonbonn.approveservice.base.Constants;
import com.bonbonn.approveservice.data.AbstractApprovableEntity;
import com.bonbonn.approveservice.data.ApproveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractApprovableService<
    E extends AbstractApprovableEntity, R extends JpaRepository<E, Long>> {

  protected final R repository;

  protected AbstractApprovableService(R repository) {
    this.repository = repository;
  }

  @Transactional
  public void approve(Long id) {
    E entity = repository.findById(id).orElseThrow();
    String currentUser = getCurrentUser();

    ensureDifferentUser(entity, currentUser);

    if (entity.getApproveType() == ApproveType.CREATE) {
      approveCreate(id);
    } else if (entity.getApproveType() == ApproveType.UPDATE) {
      approveUpdate(id);
    } else {
      throw new IllegalStateException("No approval action required for entity id=" + id);
    }
  }

  private void approveCreate(Long id) {
    E entity = repository.findById(id).orElseThrow();
    entity.setApproveType(null);
    repository.save(entity);
  }

  private void approveUpdate(Long id) {
    E entity = repository.findById(id).orElseThrow();
    E predecessor = repository.findById(entity.getPredecessor()).orElseThrow();
    repository.delete(predecessor);
    repository.save(entity);
  }


  @Transactional
  public void reject(Long id) {
    E entity = repository.findById(id).orElseThrow();
    if (entity.getApprovedBy() != null) {
      throw new IllegalStateException(entity.getClass().getSimpleName() + " already approved");
    }
    repository.delete(entity);
  }

  protected String getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return "SYSTEM";
    }
    return auth.getName();
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
