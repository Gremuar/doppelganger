package com.restapi.doppelganger.worker;

import com.restapi.doppelganger.entity.FreeShortUrl;
import com.restapi.doppelganger.entity.UserLink;
import com.restapi.doppelganger.repository.FreeShortUrlRepository;
import com.restapi.doppelganger.repository.UserLinkRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Slf4j
@Component
public class DbCleaner {

  private Thread t;
  private boolean closed;
  @Autowired
  private FreeShortUrlRepository freeShortUrlRepository;
  @Autowired
  private UserLinkRepository userLinkRepository;
  @Value("${lnk.lifeCheckPeriod}")
  private Integer timeout;
  @Value("${lnk.lifeTime}")
  private Integer lnkLifeTime;

  public void start() {
    closed = false;
    if (t == null || !t.isAlive()) {
      t = new Thread(() -> {
        while (!closed) {
          userLinkRepository.findFirst100By()
              .stream()
              .filter(userLink ->
                  ChronoUnit.SECONDS.between(userLink.getAddTime(), LocalDateTime.now()) >=
                      lnkLifeTime)
              .forEachOrdered(userLink -> {
                try {
                  freeShortUrlRepository.save(new FreeShortUrl(userLink.getShortLink()));
                } catch (DataIntegrityViolationException ex) {
                  log.info("Не удалось добавить в таблицу свободный id="+userLink.getShortLink()+"\n"+ex.getMessage());
                }
                userLinkRepository.delete(userLink);
                log.info("Deleted " + userLink);
              });
          try {
            Thread.sleep(timeout * 1000);
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
          }
        }
      });
    }
    t.start();
    log.info("DbCleaner started");
  }

  public Thread.State state() {
    return t.getState();
  }

  public void stop() {
    closed = true;
    log.info("DbCleaner stoped");
  }
}
