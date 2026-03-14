package com.ppossatto.librarymanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
@Slf4j
public class Application {

  private static final int DASHES_COUNT = 100;
  private static final String START_LOG =
     "library-manager application started successfully in {} ms";

  public static void main(String[] args) {
    Instant start = Instant.now();
    SpringApplication.run(Application.class, args);
    Instant finish = Instant.now();

    log.info("-".repeat(DASHES_COUNT));
    log.info(START_LOG, Duration.between(start, finish).toMillis());
    log.info("-".repeat(DASHES_COUNT));
  }

}
