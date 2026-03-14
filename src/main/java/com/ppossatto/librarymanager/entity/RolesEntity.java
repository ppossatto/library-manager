package com.ppossatto.librarymanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "ROLES")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolesEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long roleId;

  @Column(name = "NAME", length = 100, nullable = false, unique = true)
  private String roleName;
}
