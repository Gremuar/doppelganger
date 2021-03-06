package com.restapi.doppelganger.dto;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Slf4j
public class Link {
  private String url;

  public boolean isValid() {
    try {
      if (!url.matches("^.*://.*$")) {
        throw new URISyntaxException(url, "Переданный адрес не является URL");
      }
      new URL(url).toURI();
      return true;
    } catch (MalformedURLException | URISyntaxException e) {
      return false;
    }
  }

  public boolean isAvailable(int timeout) {
    String url = this.url.replaceFirst("^https:", "http:");

    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setConnectTimeout(timeout);
      connection.setReadTimeout(timeout);
      connection.setRequestMethod("HEAD");
      int responseCode = connection.getResponseCode();
      return (200 <= responseCode && responseCode <= 399 || responseCode == 418);
    } catch (IOException ex) {
      log.debug(ex.getMessage());
      return false;
    }
  }
}
