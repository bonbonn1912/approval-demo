package com.bonbonn.approveservice.data.DTO;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AdditionalInformationDTO {

  private String createdBy;
  private ApproveType approveType;
  private LocalDateTime createdAt;
  private String approvedBy;
  private Long draftTarget;

  public enum ApproveType {
    UPDATE, DELETE, CREATE
  }

}
