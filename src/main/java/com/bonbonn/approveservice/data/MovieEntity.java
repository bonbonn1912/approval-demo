package com.bonbonn.approveservice.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MOVIE")
@Getter
@Setter
public class MovieEntity extends AbstractApprovableEntity {

  @Column(name = "TITLE", nullable = false)
  private String title;

  @Column(name = "LENGTH_IN_MINUTES")
  private int lengthInMinutes;

  @Column(name = "PRICE")
  private double price;

}
