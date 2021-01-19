package com.restapi.doppelganger.worker;

import com.restapi.doppelganger.entity.FreeShortUrl;
import com.restapi.doppelganger.repository.FreeShortUrlRepository;
import com.restapi.doppelganger.repository.UserLinkRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Component
@Slf4j
public class SheduledDbCleaner {
  @Autowired
  private FreeShortUrlRepository freeShortUrlRepository;
  @Autowired
  private UserLinkRepository userLinkRepository;
  @Value("${lnk.lifeCheckPeriod}")
  private Integer timeout;
  @Value("${lnk.lifeTime}")
  private Integer lnkLifeTime;
  private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> scheduledFuture;
  private final Runnable cleaneTask = () -> {
    log.debug("task work");
    userLinkRepository.findFirst100By()
        .stream()
        .filter(userLink ->
            ChronoUnit.SECONDS.between(userLink.getAddTime(), LocalDateTime.now()) >=
                lnkLifeTime)
        .forEachOrdered(userLink -> {
          try {
            freeShortUrlRepository.save(new FreeShortUrl(userLink.getShortLink()));
            log.info(
                "Добавлен свободный id в таблицу FreeShortUrl: " + userLink.getShortLink());
          } catch (DataIntegrityViolationException ex) {
            log.info("Не удалось добавить в таблицу свободный id=" + userLink.getShortLink() +
                "\n" + ex.getMessage());
          }
          userLinkRepository.delete(userLink);
          log.info("Deleted " + userLink);
        });
  };

  public void start() {
    if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
      stop();
    }
    scheduledFuture = ses.scheduleAtFixedRate(cleaneTask, 0, timeout, TimeUnit.SECONDS);
    log.info("DbCleaner started");
  }

  public void stop() {
    scheduledFuture.cancel(true);
    log.debug("DbCleaner stoped");
  }

  @PreDestroy
  public void preDestroy() {
    stop();
    ses.shutdown();
    log.debug("DbCleaner destroed");
  }
}
