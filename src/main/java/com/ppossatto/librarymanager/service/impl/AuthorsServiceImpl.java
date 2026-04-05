package com.ppossatto.librarymanager.service.impl;

import com.ppossatto.librarymanager.dto.request.CreateAuthorRequest;
import com.ppossatto.librarymanager.dto.request.UpdateAuthorRequest;
import com.ppossatto.librarymanager.dto.response.CreateAuthorResponse;
import com.ppossatto.librarymanager.dto.response.GetAllAuthorsResponse;
import com.ppossatto.librarymanager.dto.response.GetAuthorResponse;
import com.ppossatto.librarymanager.dto.response.GetBookIdAndTitle;
import com.ppossatto.librarymanager.dto.response.UpdateAuthorResponse;
import com.ppossatto.librarymanager.entity.AuthorsEntity;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.AuthorsRepository;
import com.ppossatto.librarymanager.service.AuthorsService;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorsServiceImpl implements AuthorsService {

  private final AuthorsRepository authorsRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<GetAllAuthorsResponse> getAllAuthors(Pageable pageable, String name) {
    log.debug("Get all authors request in service");
    try{
      return authorsRepository.findAuthorsWithFilter(name, pageable)
         .map(author -> GetAllAuthorsResponse.builder()
            .id(author.getAuthorId())
            .name(author.getAuthorName())
            .build());
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA findAuthorsWithFilter query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while getting all authors data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while getting all authors.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public GetAuthorResponse getAuthorById(UUID id) {
    log.debug("Get author with id [{}] request in service",id);
    try{
      return authorsRepository.findById(id).map(
         author -> GetAuthorResponse.builder()
            .id(author.getAuthorId())
            .name(author.getAuthorName())
            .books(author.getBooksEntity().stream().map(
               book -> GetBookIdAndTitle.builder()
                  .id(book.getBookId())
                  .title(book.getBookTitle())
                  .build()
            ).collect(Collectors.toSet()))
            .birthDate(author.getAuthorBirthDate())
            .nationality(author.getAuthorNationality())
            .biography(author.getAuthorBiography())
            .build()
      ).orElseThrow(
         () -> new CoreException(CoreExceptionType.AUTHOR_NOT_FOUND_EXCEPTION, id.toString())
      );
    } catch (CoreException ce){
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA findById query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while getting author data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while getting author.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public CreateAuthorResponse createAuthor(CreateAuthorRequest request) {
    log.debug("Create author request in service");
    try{
      if (authorsRepository.existsByAuthorName(request.name())){
        throw new CoreException(CoreExceptionType.AUTHOR_ALREADY_EXISTS_EXCEPTION, request.name());
      }
      AuthorsEntity authorToSave = new AuthorsEntity();
      authorToSave.setAuthorName(request.name());
      authorToSave.setAuthorBirthDate(request.birthDate());
      authorToSave.setAuthorNationality(request.nationality());
      authorToSave.setAuthorBiography(request.biography());
      AuthorsEntity savedAuthor = authorsRepository.save(authorToSave);
      return CreateAuthorResponse.builder()
         .id(savedAuthor.getAuthorId())
         .name(savedAuthor.getAuthorName())
         .birthDate(savedAuthor.getAuthorBirthDate())
         .nationality(savedAuthor.getAuthorNationality())
         .biography(savedAuthor.getAuthorBiography())
         .build();
    } catch (CoreException ce){
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA save query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while saving author data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while saving author.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public UpdateAuthorResponse updateAuthor(UpdateAuthorRequest request, UUID authorId) {
    log.debug("Update author request in service");
    try{
      AuthorsEntity authorToUpdate = authorsRepository.findById(authorId).orElseThrow(
         () -> new CoreException(CoreExceptionType.AUTHOR_NOT_FOUND_EXCEPTION, authorId.toString())
      );
      if(request.name() != null &&
         !authorToUpdate.getAuthorName().equals(request.name()) &&
         authorsRepository.existsByAuthorName(request.name())){
        throw new CoreException(CoreExceptionType.AUTHOR_ALREADY_EXISTS_EXCEPTION, request.name());
      }
      if(request.name() != null){
        authorToUpdate.setAuthorName(request.name());
      }
      if(request.birthDate() != null){
        authorToUpdate.setAuthorBirthDate(request.birthDate());
      }
      if(request.nationality() != null){
        authorToUpdate.setAuthorNationality(request.nationality());
      }
      if(request.biography() != null){
        authorToUpdate.setAuthorBiography(request.biography());
      }
      AuthorsEntity updatedAuthor = authorsRepository.save(authorToUpdate);
      return UpdateAuthorResponse.builder()
         .id(updatedAuthor.getAuthorId())
         .name(updatedAuthor.getAuthorName())
         .birthDate(updatedAuthor.getAuthorBirthDate())
         .nationality(updatedAuthor.getAuthorNationality())
         .biography(updatedAuthor.getAuthorBiography())
         .build();
    } catch (CoreException ce){
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA update query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while updating author data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while updating author.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public void deleteAuthorById(UUID id) {
    log.debug("Delete author request in service");
    try{
      AuthorsEntity authorToDelete = authorsRepository.findById(id).orElseThrow(
         () -> new CoreException(CoreExceptionType.AUTHOR_NOT_FOUND_EXCEPTION, id.toString())
      );
      if(authorsRepository.hasBooksByAuthorId(id)){
        throw new CoreException(CoreExceptionType.AUTHOR_HAS_BOOKS_EXCEPTION, id.toString());
      }
      authorsRepository.delete(authorToDelete);
    } catch (CoreException ce){
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA delete query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while deleting author data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while deleting author.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }
}
