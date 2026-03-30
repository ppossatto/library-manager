package com.ppossatto.librarymanager.service.impl;

import com.ppossatto.librarymanager.dto.domain.enums.UserStatus;
import com.ppossatto.librarymanager.dto.request.CreateAccountRequest;
import com.ppossatto.librarymanager.dto.response.CreateAccountResponse;
import com.ppossatto.librarymanager.dto.response.GetUserResponse;
import com.ppossatto.librarymanager.dto.response.GetUsersResponse;
import com.ppossatto.librarymanager.entity.RolesEntity;
import com.ppossatto.librarymanager.entity.UsersEntity;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.RolesRepository;
import com.ppossatto.librarymanager.repository.UsersRepository;
import com.ppossatto.librarymanager.service.UsersService;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

  private final UsersRepository repository;
  private final PasswordEncoder encoder;
  private final RolesRepository rolesRepository;

  private static final String ROLE_USER = "ROLE_USER";

  @Override
  @Transactional
  public CreateAccountResponse createAccount(CreateAccountRequest request) {

    log.debug("Account creation service.");
    try {
      if (repository.existsByUserEmail(request.email())) {
        log.warn("Email already stored in the database");
        throw new CoreException(CoreExceptionType.EMAIL_ALREADY_EXISTS);
      }

      RolesEntity roles = rolesRepository.findByRoleName(ROLE_USER)
         .orElseThrow(() -> new CoreException(CoreExceptionType.ROLE_NOT_FOUND_EXCEPTION, ROLE_USER));

      UsersEntity userToSave = new UsersEntity();
      userToSave.setUserName(request.name());
      userToSave.setUserEmail(request.email());
      userToSave.setUserPhone(request.phone());
      userToSave.setUserPassword(encoder.encode(request.password()));
      userToSave.setRolesEntity(Set.of(roles));

      UsersEntity savedUser = repository.save(userToSave);

      return CreateAccountResponse.builder()
         .id(savedUser.getUserId().toString())
         .name(savedUser.getUserName())
         .email(savedUser.getUserEmail())
         .phone(savedUser.getUserPhone())
         .status(savedUser.getUserStatus())
         .build();
    } catch (CoreException ce) {
      throw ce;
    } catch (Exception e) {
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  public Page<GetUsersResponse> getAllUsers(Pageable pageable) {
    log.debug("Get all users request in service");
    try {
      return repository.findAll(pageable)
         .map(user -> GetUsersResponse.builder()
            .id(user.getUserId())
            .name(user.getUserName())
            .email(user.getUserEmail())
            .status(UserStatus.getStatusByCode(user.getUserStatus()))
            .build());
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA getAll query for users.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while getting all users from the database.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while getting all users.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  public GetUserResponse getUser(UUID userId) {
    log.debug("Getting user information with ID: [{}]", userId);
    try {
      return repository.findByUserId(userId)
         .map(user -> GetUserResponse.builder()
            .id(user.getUserId())
            .name(user.getUserName())
            .email(user.getUserEmail())
            .phone(user.getUserPhone())
            .status(UserStatus.getStatusByCode(user.getUserStatus()))
            .inactiveDateTime(user.getInactiveDateTime())
            .roles(user.getRolesEntity().stream()
               .map(
                  RolesEntity::getRoleName
               )
               .collect(Collectors.toSet()))
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build())
         .orElse(null);
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA getAll query for user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while getting all user with ID [{}] from the database.", userId);
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while getting all user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }
}
