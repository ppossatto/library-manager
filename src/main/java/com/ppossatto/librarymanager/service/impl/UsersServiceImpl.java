package com.ppossatto.librarymanager.service.impl;

import com.ppossatto.librarymanager.dto.request.CreateAccountRequest;
import com.ppossatto.librarymanager.dto.response.CreateAccountResponse;
import com.ppossatto.librarymanager.entity.RolesEntity;
import com.ppossatto.librarymanager.entity.UsersEntity;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.RolesRepository;
import com.ppossatto.librarymanager.repository.UsersRepository;
import com.ppossatto.librarymanager.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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
    } catch (CoreException ce){
      throw ce;
    } catch (Exception e) {
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }
}
