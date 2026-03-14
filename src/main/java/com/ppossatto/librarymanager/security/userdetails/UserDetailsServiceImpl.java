package com.ppossatto.librarymanager.security.userdetails;

import com.ppossatto.librarymanager.dto.domain.enums.UserStatus;
import com.ppossatto.librarymanager.entity.UsersEntity;
import com.ppossatto.librarymanager.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UsersRepository usersRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UsersEntity userFound = usersRepository.findByUserEmailWithRoles(username)
       .orElseThrow(
          () -> new UsernameNotFoundException(String.format("User with email %s was not found", username))
       );
    UserStatus userStatus = UserStatus.getStatusByCode(userFound.getUserStatus());
    return User.builder()
       .username(userFound.getUserEmail())
       .password(userFound.getUserPassword())
       .disabled(isUserInactive(userStatus))
       .accountLocked(isUserBlocked(userStatus))
       .authorities(extractRoles(userFound))
       .build();
  }

  private static List<SimpleGrantedAuthority> extractRoles(UsersEntity userFound) {
    return userFound.getRolesEntity().stream()
       .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
       .toList();
  }

  private static boolean isUserInactive(UserStatus userStatus) {
    return userStatus.equals(UserStatus.INACTIVE);
  }

  private static boolean isUserBlocked(UserStatus userStatus) {
    return userStatus.equals(UserStatus.BLOCKED);
  }
}
