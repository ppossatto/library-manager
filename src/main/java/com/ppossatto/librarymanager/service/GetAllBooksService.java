package com.ppossatto.librarymanager.service;

import com.ppossatto.librarymanager.dto.response.GetBookBasicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetAllBooksService {

  Page<GetBookBasicResponse> getAllBooks(Pageable pageable, UUID traceId);
}
