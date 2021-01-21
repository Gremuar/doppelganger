package com.restapi.doppelganger.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AsJsonString {
  public static String jsonToString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T stringToJson(String json, Class<T> objClass) {
    try {
      return new ObjectMapper()
          .findAndRegisterModules()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(json, objClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}