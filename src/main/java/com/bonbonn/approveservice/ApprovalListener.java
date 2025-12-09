package com.bonbonn.approveservice;

import com.bonbonn.approveservice.data.AbstractApprovableEntity;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApprovalListener {

  @PreUpdate
  public void onPreUpdate(AbstractApprovableEntity entity) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUser =
        (auth != null && auth.isAuthenticated()) ? auth.getName() : "SYSTEM";

    if (entity.getApprovedBy() == null && entity.getApproveType() == null) {
      entity.setApprovedBy(currentUser);
    }

    if (entity.getCreatedBy() != null
        && entity.getApprovedBy() != null
        && entity.getCreatedBy().equalsIgnoreCase(entity.getApprovedBy())) {
      throw new IllegalStateException(
          "Approval not allowed: creator and approver must be different users.");
    }
  }

}
