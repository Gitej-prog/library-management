package com.library.library_management.service;

import com.library.library_management.dto.AuthorRequestDto;
import com.library.library_management.dto.AuthorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface AuthorService {

    AuthorResponseDto createAuthor(
            AuthorRequestDto request
    );

    AuthorResponseDto getAuthorById(
            Long id
    );

    Page<AuthorResponseDto> getAllAuthors(int page, int size);

    AuthorResponseDto updateAuthor(
            Long id,
            AuthorRequestDto request
    );

    void deleteAuthor(
            Long id
    );

    void importAuthorsFromCsv(
            MultipartFile file
    );

    byte[] exportAuthorsToCsv();
}