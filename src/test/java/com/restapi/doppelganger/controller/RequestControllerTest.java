package com.restapi.doppelganger.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.restapi.doppelganger.dto.Link;
import com.restapi.doppelganger.entity.UserLink;
import com.restapi.doppelganger.repository.FreeShortUrlRepository;
import com.restapi.doppelganger.repository.UserLinkRepository;
import com.restapi.doppelganger.util.AsJsonString;
import java.util.Objects;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class RequestControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void normalResponse() throws Exception {
    this.mockMvc
        .perform(post("http://localhost:" + port).contentType(MediaType.APPLICATION_JSON)
            .content(AsJsonString.jsonToString(new Link("http://google.com"))))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.shortLink").isNotEmpty());
  }

  @Test
  public void urlIsNotValidCase1() throws Exception {
    this.mockMvc.perform(post("http://localhost:" + port).contentType(MediaType.APPLICATION_JSON)
        .content(AsJsonString.jsonToString(new Link("http:/google.com"))))
        .andDo(print())
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.errMessage").value("Переданный параметр не является URL"));
  }

  @Test
  public void urlIsNotValidCase2() throws Exception {
    this.mockMvc.perform(post("http://localhost:" + port).contentType(MediaType.APPLICATION_JSON)
        .content(AsJsonString.jsonToString(new Link("p:\\abracadabra"))))
        .andDo(print())
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.errMessage").value("Переданный параметр не является URL"));
  }

  @Test
  public void urlIsNotAvailable() throws Exception {
    this.mockMvc.perform(post("http://localhost:" + port).contentType(MediaType.APPLICATION_JSON)
        .content(AsJsonString.jsonToString(new Link("http://notavailableurl.not"))))
        .andDo(print())
        .andExpect(status().is(400))
        .andExpect(
            jsonPath("$.errMessage").value("Переданный URL не существует или временно недоступен"));
  }

  @Test
  public void goToFullUrlNormal() throws Exception {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    UserLink testUserLink = Objects
        .requireNonNull(new RestTemplate().postForEntity("http://localhost:" + port,
            new HttpEntity<>(AsJsonString.jsonToString(new Link("http://google.com")),
                headers), UserLink.class).getBody());
    this.mockMvc.perform(get("http://localhost:" + port + "/" + testUserLink.getShortLink()))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(testUserLink.getLongLink() + "?redirectedFrom=Doppelganger.ru"));
  }
}