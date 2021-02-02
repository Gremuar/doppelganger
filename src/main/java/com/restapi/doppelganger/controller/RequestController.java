package com.restapi.doppelganger.controller;

import com.restapi.doppelganger.dto.Link;
import com.restapi.doppelganger.dto.ServerError;
import com.restapi.doppelganger.entity.FreeShortUrl;
import com.restapi.doppelganger.entity.UserLink;
import com.restapi.doppelganger.repository.FreeShortUrlRepository;
import com.restapi.doppelganger.repository.UserLinkRepository;
import com.restapi.doppelganger.util.Base62;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Slf4j
public class RequestController {
  @Autowired
  private UserLinkRepository userLinkRepository;
  @Autowired
  private FreeShortUrlRepository freeShortUrlRepository;
  @Value("${reqTimeout}")
  private Integer reqTimeout;
  private final AtomicLong reqIdCounter = new AtomicLong();
  private AtomicLong lnkIdCounter;


  @PostConstruct
  public void init() {
    lnkIdCounter = new AtomicLong(
        userLinkRepository.count() + freeShortUrlRepository.count()
    );
  }

  @GetMapping("/{shortUrl}")
  public Object goToFullUrl(RedirectAttributes attributes, @PathVariable String shortUrl) {
    Long reqId = reqIdCounter.incrementAndGet();
    attributes.addAttribute("redirectedFrom", "Doppelganger.ru");
    var linkInDb = userLinkRepository.findFirstByShortLink(shortUrl);
    if (linkInDb == null) {
      return new ServerError(reqId, 204, "Не найдена запрашиваемая ссылка: " + shortUrl);
    }
    linkInDb.setReqId(reqId);
    return new RedirectView(linkInDb.getLongLink());
  }

  @PostMapping("/")
  public ResponseEntity<?> getShortLink(@RequestBody Link url) {
    FreeShortUrl freeShortUrl;
    String shortLink;
    Long reqId = reqIdCounter.incrementAndGet();
    var linkInDb = userLinkRepository.findFirstByLongLink(url.getUrl());
    if (linkInDb != null) {
      linkInDb.setReqId(reqId);
      return ResponseEntity
          .status(200)
          .body(linkInDb);
    }
    if (!url.isValid()) {
      return ResponseEntity
          .status(400)
          .body(new ServerError(reqId, 400, "Переданный параметр не является URL"));
    }
    if (!url.isAvailable(reqTimeout * 1000)) {
      return ResponseEntity
          .status(400)
          .body(
              new ServerError(reqId, 400, "Переданный URL не существует или временно недоступен")
          );
    }
    freeShortUrl = freeShortUrlRepository.findFirstBy();
    if (freeShortUrl != null) {
      shortLink = freeShortUrl.getShortUrl();
      freeShortUrlRepository.delete(freeShortUrl);
      log.info("Из таблицы FreeShortUrl удален id: " + freeShortUrl.getShortUrl());
    } else {
      shortLink = Base62.getHash(String.valueOf(lnkIdCounter.incrementAndGet()));
    }
    UserLink userLink = new UserLink(reqId, Base62.getUnHash(shortLink), url.getUrl(), shortLink,
        LocalDateTime.now());
    try {
      userLinkRepository.save(userLink);
      log.info("Добавлен в таблицу UserLink: " + userLink);
    } catch (DataAccessException ex) {
      log.error("Не удалось сохранить объект в базу данных!\nЗапрос: POST /\nОбъект: " + userLink);
      freeShortUrlRepository.save(new FreeShortUrl(shortLink));
      log.info("Сохранили неиспользуемый shortLink(" + shortLink + ") в таблицу FreeShortUrl");
      return ResponseEntity
          .status(500)
          .body(
              new ServerError(reqId, 500, "Не удалось создать короткую ссылку для вашего запроса"));
    }
    return ResponseEntity
        .status(201)
        .body(userLink);
  }
}