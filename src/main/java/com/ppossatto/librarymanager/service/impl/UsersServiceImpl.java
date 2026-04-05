package com.ppossatto.librarymanager.service.impl;

import com.ppossatto.librarymanager.dto.domain.enums.UserStatus;
import com.ppossatto.librarymanager.dto.request.ChangeEmailRequest;
import com.ppossatto.librarymanager.dto.request.ChangeRoleRequest;
import com.ppossatto.librarymanager.dto.request.CreateAccountRequest;
import com.ppossatto.librarymanager.dto.request.UpdatePasswordRequest;
import com.ppossatto.librarymanager.dto.request.UpdateUserRequest;
import com.ppossatto.librarymanager.dto.response.ChangeEmailResponse;
import com.ppossatto.librarymanager.dto.response.CreateAccountResponse;
import com.ppossatto.librarymanager.dto.response.GetUserResponse;
import com.ppossatto.librarymanager.dto.response.GetUsersResponse;
import com.ppossatto.librarymanager.dto.response.UpdateUserResponse;
import com.ppossatto.librarymanager.entity.RolesEntity;
import com.ppossatto.librarymanager.entity.UsersEntity;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.ReservationsRepository;
import com.ppossatto.librarymanager.repository.RolesRepository;
import com.ppossatto.librarymanager.repository.UsersRepository;
import com.ppossatto.librarymanager.security.service.JwtService;
import com.ppossatto.librarymanager.service.UsersService;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
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
  private final ReservationsRepository reservationsRepository;
  private final UserDetailsService userDetailsService;
  private final JwtService jwtService;

  private static final String ROLE_USER = "ROLE_USER";
  private static final String ROLE_LIBRARIAN = "ROLE_LIBRARIAN";

  @Value("${security.jwt.expiration}")
  private Long expiration;

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
  @Transactional(readOnly = true)
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
  @Transactional(readOnly = true)
  public GetUserResponse getUser(UUID userId) {
    log.debug("Getting user information with ID: [{}]", userId);
    try {
      Optional<UsersEntity> userFound = getUserIfAuthorizedOrLibrarianRole(userId);
      return userFound
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
         .orElseThrow(() -> new CoreException(CoreExceptionType.USER_NOT_FOUND_EXCEPTION));
    } catch (CoreException ce) {
      throw ce;
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

  @Override
  @Transactional
  public UpdateUserResponse updateUser(UUID userId, UpdateUserRequest request) {
    log.debug("Update user information with ID: [{}]", userId);
    try {
      Optional<UsersEntity> userFound = getUserIfAuthorizedOrLibrarianRole(userId);
      UsersEntity user = userFound.get();
      user.setUserName(request.name());
      user.setUserPhone(request.phone());
      UsersEntity savedUser = repository.save(user);

      return UpdateUserResponse.builder()
         .id(savedUser.getUserId())
         .name(savedUser.getUserName())
         .phone(savedUser.getUserPhone())
         .build();
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while updating data from user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while updating data from user with ID [{}] from the database.", userId);
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while updating data from user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public void updatePassword(UUID userId, UpdatePasswordRequest request) {
    log.debug("Update user password for user with ID: [{}]", userId);
    try {
      Optional<UsersEntity> userFound = getUserIfAuthorizedOrLibrarianRole(userId);
      UsersEntity user = userFound.get();
      if (!isLibrarian(getAuthentication()) &&
         !encoder.matches(request.oldPassword(), user.getUserPassword())) {
        throw new CoreException(CoreExceptionType.WRONG_PASSWORD_EXCEPTION);
      }
      user.setUserPassword(encoder.encode(request.newPassword()));
      repository.save(user);
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while updating password for user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while updating password for user with ID [{}] from the database.", userId);
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while updating password for user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public void softDeleteUser(UUID userId) {
    log.debug("Soft delete user with ID: [{}]", userId);
    try {
      Optional<UsersEntity> userFound = repository.findByUserId(userId);
      UsersEntity user = userFound.orElseThrow(
         () -> new CoreException(CoreExceptionType.USER_NOT_FOUND_EXCEPTION)
      );
      if (reservationsRepository.hasActiveOrOverdueReservations(userId)) {
        throw new CoreException(CoreExceptionType.USER_HAS_ACTIVE_RESERVATIONS_EXCEPTION);
      }
      user.setInactiveDateTime(LocalDateTime.now());
      user.setUserStatus(UserStatus.INACTIVE.getCode());
      repository.save(user);
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while soft deleting user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while soft deleting user with ID [{}] from the database.", userId);
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while soft deleting user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public void blockUser(UUID userId) {
    log.debug("Blocking user with ID: [{}]", userId);
    try {
      UsersEntity userFound = repository.findByUserId(userId).orElseThrow(
         () -> new CoreException(CoreExceptionType.USER_NOT_FOUND_EXCEPTION)
      );
      Authentication authentication = getAuthentication();
      if (userFound.getUserEmail().equals(authentication.getName())) {
        throw new CoreException(CoreExceptionType.SAME_USER_OPERATION_EXCEPTION);
      }
      userFound.setUserStatus(UserStatus.BLOCKED.getCode());
      repository.save(userFound);
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while blocking user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while blocking user with ID [{}] from the database.", userId);
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while blocking user with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public void changeUserRole(UUID userId, ChangeRoleRequest request) {
    log.debug("Changing user role with ID: [{}]", userId);
    try {
      UsersEntity userFound = repository.findByUserId(userId).orElseThrow(
         () -> new CoreException(CoreExceptionType.USER_NOT_FOUND_EXCEPTION)
      );
      Authentication authentication = getAuthentication();
      if (userFound.getUserEmail().equals(authentication.getName())) {
        throw new CoreException(CoreExceptionType.SAME_USER_OPERATION_EXCEPTION);
      }
      RolesEntity role = rolesRepository.findByRoleName(request.role()).orElseThrow(
         () -> new CoreException(CoreExceptionType.ROLE_NOT_FOUND_EXCEPTION, request.role())
      );
      userFound.setRolesEntity(
         Set.of(role)
      );
      repository.save(userFound);
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while changing user role with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while changing user role with ID [{}] from the database.", userId);
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while changing user role with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public ChangeEmailResponse changeEmail(UUID userId, ChangeEmailRequest request) {
    log.debug("Changing email for user with ID: [{}]", userId);
    try {
      UsersEntity userFound = repository.findByUserId(userId).orElseThrow(
         () -> new CoreException(CoreExceptionType.USER_NOT_FOUND_EXCEPTION)
      );
      Authentication authentication = getAuthentication();
      if (!isLibrarian(authentication) && !userFound.getUserEmail().equals(authentication.getName())) {
        throw new CoreException(CoreExceptionType.CHANGE_OTHER_USER_EMAIL_EXCEPTION);
      }
      if (!isLibrarian(authentication) &&
         !encoder.matches(request.password(), userFound.getUserPassword())) {
        throw new CoreException(CoreExceptionType.WRONG_PASSWORD_EXCEPTION);
      }
      if (repository.existsByUserEmail(request.newEmail())) {
        throw new CoreException(CoreExceptionType.EMAIL_ALREADY_EXISTS);
      }
      userFound.setUserEmail(request.newEmail());
      UsersEntity savedUser = repository.save(userFound);

      String newToken = null;
      if (!isLibrarian(authentication)) {
        UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(savedUser.getUserEmail());
        newToken = jwtService.generateToken(updatedUserDetails);
      }

      return ChangeEmailResponse.builder()
         .email(savedUser.getUserEmail())
         .newToken(newToken)
         .tokenType("Bearer")
         .expiresIn(newToken != null ? expiration : null)
         .build();

    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while changing user email with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while changing user email with ID [{}] from the database.", userId);
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while changing user email with ID [{}].", userId);
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  private Optional<UsersEntity> getUserIfAuthorizedOrLibrarianRole(UUID userId) {
    Optional<UsersEntity> userFound = repository.findByUserId(userId);
    Authentication authentication = getAuthentication();
    String authenticatedEmail = authentication.getName();
    if (userFound.isEmpty()) {
      throw new CoreException(CoreExceptionType.USER_NOT_FOUND_EXCEPTION);
    }
    if (!isLibrarian(authentication) && !userFound.get().getUserEmail().equals(authenticatedEmail)) {
      throw new CoreException(CoreExceptionType.FORBIDDEN_EXCEPTION);
    }
    return userFound;
  }

  private static Authentication getAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new CoreException(CoreExceptionType.NOT_LOGGED_IN_EXCEPTION);
    }
    return authentication;
  }

  private static boolean isLibrarian(Authentication authentication) {
    return authentication.getAuthorities().stream()
       .anyMatch(a -> Objects.equals(a.getAuthority(), ROLE_LIBRARIAN));
  }
}
