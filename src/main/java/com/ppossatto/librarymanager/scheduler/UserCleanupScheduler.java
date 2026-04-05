package com.ppossatto.librarymanager.scheduler;

import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.UsersRepository;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

  private final UsersRepository usersRepository;

  @Scheduled(cron = "${scheduler.cleanup.users}")
  @Transactional
  void cleanOldInactiveUsers() {
    log.debug("Cleaning old inactive users");
    try {
      long inactiveUsers = usersRepository.deleteInactiveUsersOlderThan(LocalDateTime.now().minusMonths(6));
      log.info("Inactive users deleted: {}", inactiveUsers);
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA deletion for old inactive users.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while deleting old inactive users.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while deleting old inactive users.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }
}
