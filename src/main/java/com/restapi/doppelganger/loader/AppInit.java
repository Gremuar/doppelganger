package com.restapi.doppelganger.loader;

import com.restapi.doppelganger.worker.SheduledDbCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppInit implements ApplicationRunner {

  @Autowired
//  private DbCleaner dbCleaner;
  private SheduledDbCleaner dbCleaner;

  @Override
  public void run(ApplicationArguments args) {
    dbCleaner.start();
  }
}