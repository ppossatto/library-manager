package com.ppossatto.librarymanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Table(name = "AUTHORS")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorsEntity extends CommonEntity {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private UUID authorId;

  @Column(name = "NAME", length = 150, nullable = false)
  private String authorName;

  @ManyToMany(mappedBy = "authorsEntity")
  private Set<BooksEntity> booksEntity;

  @Column(name = "BIRTH_DATE")
  private LocalDate authorBirthDate;

  @Column(name = "NATIONALITY", length = 100)
  private String authorNationality;

  @Column(name = "BIOGRAPHY")
  private String authorBiography;
}
