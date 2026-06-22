package com.library.library_management.controller;

import com.library.library_management.dto.BookBulkUpdateRequest;
import com.library.library_management.dto.BookRequestDto;
import com.library.library_management.dto.BookResponseDto;
import com.library.library_management.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(
            @Valid @RequestBody BookRequestDto request
    ) {

        log.info(
                "POST /api/books called for ISBN: {}",
                request.getIsbn()
        );

        return bookService.createBook(request);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBooks() {

        byte[] csvData =
                bookService.exportBooksToCsv();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=books.csv"
                )
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        "text/csv"
                )
                .body(csvData);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<BookResponseDto> createBooks(
            @Valid @RequestBody
            List<BookRequestDto> requests
    ) {

        return bookService.createBooks(
                requests
        );
    }

    @PostMapping(
            value = "/import",
            consumes = "multipart/form-data"
    )
    public String importBooks(
            @RequestParam("file")
            MultipartFile file
    ) {

        bookService.importBooksFromCsv(
                file
        );

        return "Books imported successfully";
    }

    @GetMapping("/{id}")
    public BookResponseDto getBookById(
            @PathVariable Long id
    ) {

        log.info(
                "GET /api/books/{} called",
                id
        );

        return bookService.getBookById(id);
    }

    @GetMapping
    public Page<BookResponseDto> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        log.info(
                "GET /api/books called - page: {}, size: {}",
                page,
                size
        );

        return bookService.getAllBooks(page, size);
    }

    @PutMapping("/{id}")
    public BookResponseDto updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDto request
    ) {

        log.info(
                "PUT /api/books/{} called",
                id
        );

        return bookService.updateBook(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(
            @PathVariable Long id
    ) {

        log.info(
                "DELETE /api/books/{} called",
                id
        );

        bookService.deleteBook(id);
    }

    @PutMapping("/bulk")
    public List<BookResponseDto> bulkUpdateBooks(
            @RequestBody
            List<BookBulkUpdateRequest> requests
    ) {

        return bookService.bulkUpdateBooks(
                requests
        );
    }
}