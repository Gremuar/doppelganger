package com.restapi.doppelganger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServerError {
  public Long reqId;
  public Integer errCode;
  public String errMessage;
}
