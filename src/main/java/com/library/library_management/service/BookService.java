package com.library.library_management.service;

import com.library.library_management.dto.BookBulkUpdateRequest;
import com.library.library_management.dto.BookRequestDto;
import com.library.library_management.dto.BookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface BookService {

    BookResponseDto createBook(
            BookRequestDto request
    );

    List<BookResponseDto> createBooks(
            List<BookRequestDto> requests
    );

    BookResponseDto getBookById(
            Long id
    );

    List<BookResponseDto> bulkUpdateBooks(
            List<BookBulkUpdateRequest> requests
    );

    Page<BookResponseDto> getAllBooks(int page, int size);

    BookResponseDto updateBook(
            Long id,
            BookRequestDto request
    );

    void deleteBook(
            Long id
    );

    void importBooksFromCsv(
            MultipartFile file
    );



    byte[] exportBooksToCsv();
}