package com.restapi.doppelganger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.seruco.encoding.base62.Base62;
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
  //doppelganger id
  private Long dgId;
  @Transient
  private Long reqId;
  @Column(unique = true, nullable = false)
  private String longLink;
  @Column(unique = true, nullable = false)
  private String shortLink;
  @Column(unique = true, nullable = false)
  private LocalDateTime addTime;

  private static final Base62 base62 = Base62.createInstance();

  public UserLink(Long reqId, Long dgId, String longLink, String shortLink,
                  LocalDateTime addTime) {
    this.reqId = reqId;
    this.dgId = dgId;
    this.longLink = longLink;
    this.shortLink = shortLink;
    this.addTime = addTime;
  }

  public static String getHash(String str) {
    final byte[] encoded = base62.encode(str.getBytes());
    return new String(encoded);
  }

  public static Long getUnHash(String str) {
    final byte[] encoded = base62.decode(str.getBytes());
    return Long.valueOf(new String(encoded));
  }
}