package com.ppossatto.librarymanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Table(name = "USERS")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersEntity extends CommonEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID", nullable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private UUID userId;

  @Column(name = "NAME", length = 150, nullable = false)
  private String userName;

  @Column(name = "EMAIL", length = 150, nullable = false, unique = true)
  private String userEmail;

  @Column(name = "PHONE", length = 20)
  private String userPhone;

  @Column(name = "PASSWORD", nullable = false)
  private String userPassword;

  @Column(name = "INACTIVE_SINCE")
  private LocalDateTime inactiveDateTime;

  @ManyToMany
  @JoinTable(
     name = "USER_ROLES",
     joinColumns = @JoinColumn(name = "USER_ID"),
     inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
  )
  private Set<RolesEntity> rolesEntity;

  @Column(name = "STATUS", length = 20)
  private String userStatus = "active";
}
