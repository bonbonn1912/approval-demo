package com.bonbonn.approveservice.data.DTO;

import lombok.Data;

@Data
public class BookDTO {

  private String title;
  private Long id;
  private int pages;
  private double price;
  private AdditionalInformationDTO additionalInformation;

}
