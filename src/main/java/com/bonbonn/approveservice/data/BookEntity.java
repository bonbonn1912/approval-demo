package com.bonbonn.approveservice.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "BOOK")
@Getter
@Setter
public class BookEntity extends AbstractApprovableEntity {

  @Column(name = "TITLE", nullable = false, unique = true)
  private String title;

  @Column(name = "PAGES")
  private int pages;

  @Column(name = "PRICE")
  private double price;
}
