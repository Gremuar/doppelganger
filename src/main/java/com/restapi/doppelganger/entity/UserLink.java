package com.restapi.doppelganger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class UserLink {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;
  @Column(unique = true)
  @JsonIgnore
  private Long dgId;
  @Transient
  private Long reqId;
  @Column(unique = true, nullable = false)
  private String longLink;
  @Column(unique = true, nullable = false)
  private String shortLink;
  @Column(unique = true, nullable = false)
  private LocalDateTime addTime;

  public UserLink(Long reqId, Long dgId, String longLink, String shortLink,
                  LocalDateTime addTime) {
    this.reqId = reqId;
    this.dgId = dgId;
    this.longLink = longLink;
    this.shortLink = shortLink;
    this.addTime = addTime;
  }
}