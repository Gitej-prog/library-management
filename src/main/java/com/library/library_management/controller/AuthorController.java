package com.library.library_management.controller;

import com.library.library_management.dto.AuthorRequestDto;
import com.library.library_management.dto.AuthorResponseDto;
import com.library.library_management.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Slf4j
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public AuthorResponseDto createAuthor(
            @Valid @RequestBody AuthorRequestDto request,
            Principal principal
    ) {

        log.info(
                "POST /api/authors called by user: {}",
                principal.getName()
        );

        return authorService.createAuthor(request);
    }

    @GetMapping("/{id}")
    public AuthorResponseDto getAuthorById(
            @PathVariable Long id
    ) {
        return authorService.getAuthorById(id);
    }

    @GetMapping
    public Page<AuthorResponseDto> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return authorService.getAllAuthors(page, size);
    }

    @PutMapping("/{id}")
    public AuthorResponseDto updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequestDto request
    ) {
        return authorService.updateAuthor(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(
            @PathVariable Long id
    ) {
        authorService.deleteAuthor(id);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAuthors() {

        byte[] csvData = authorService.exportAuthorsToCsv();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=authors.csv"
                )
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        "text/csv"
                )
                .body(csvData);
    }

    @PostMapping(
            value = "/import",
            consumes = "multipart/form-data"
    )
    public String importAuthors(
            @RequestParam("file") MultipartFile file
    ) {

        authorService.importAuthorsFromCsv(file);

        return "Authors imported successfully";
    }
}
// CI/CD Verification Test