package com.restapi.doppelganger.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class FreeShortUrl {
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long id;

  @Id
  @Column(unique = true)
  private String shortUrl;
}
