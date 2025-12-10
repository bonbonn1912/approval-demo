package com.bonbonn.approveservice.data;

import com.bonbonn.approveservice.ApprovalListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@EntityListeners({AuditingEntityListener.class, ApprovalListener.class})
public abstract class AbstractApprovableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name = "CREATED_AT", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "CREATED_BY", updatable = false)
  private String createdBy;

  @Column(name = "APPROVED_BY")
  private String approvedBy;

  @Column(name = "APPROVE_TYPE")
  @Enumerated(EnumType.STRING)
  private ApproveType approveType;

  @Column(name = "DRAFT")
  @Lob
  private String draft;

  @Column(name = "DRAFT_TARGET")
  private Long draftTarget;

  public void clearPendingDrafts() {
    this.draft = null;
    this.draftTarget = null;
    this.approveType = null;
  }
}
