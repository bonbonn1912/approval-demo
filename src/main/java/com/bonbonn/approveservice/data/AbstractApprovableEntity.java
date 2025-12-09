package com.bonbonn.approveservice.data;

import com.bonbonn.approveservice.ApprovalListener;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
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

  @CreatedBy
  @Column(name = "CREATED_BY", updatable = false)
  private String createdBy;

  @Column(name = "APPROVED_BY")
  private String approvedBy;

  @Column(name = "APPROVE_TYPE")
  @Enumerated(EnumType.STRING)
  private ApproveType approveType;

  @Column(name = "PREDECESSOR_ID")
  private Long predecessor;
}
