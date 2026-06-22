package com.library.library_management.service.impl;

import com.library.library_management.dto.AuthorRequestDto;
import com.library.library_management.dto.AuthorResponseDto;
import com.library.library_management.entity.Author;
import com.library.library_management.exception.DuplicateResourceException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.AuthorRepository;
import com.library.library_management.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public AuthorResponseDto createAuthor(
            AuthorRequestDto request
    ) {

        log.info(
                "Creating author with email: {}",
                request.getEmail()
        );

        if (authorRepository.existsByEmail(
                request.getEmail()
        )) {

            log.warn(
                    "Author already exists with email: {}",
                    request.getEmail()
            );

            throw new DuplicateResourceException(
                    "Author email already exists"
            );
        }

        Author author = Author.builder()
                .name(request.getName())
                .email(request.getEmail())
                .biography(request.getBiography())
                .build();

        Author savedAuthor =
                authorRepository.save(author);

        log.info(
                "Author created successfully with id: {}",
                savedAuthor.getId()
        );

        return AuthorResponseDto.builder()
                .id(savedAuthor.getId())
                .name(savedAuthor.getName())
                .email(savedAuthor.getEmail())
                .biography(savedAuthor.getBiography())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponseDto getAuthorById(Long id) {

        log.info(
                "Fetching author with id: {}",
                id
        );

        Author author =
                authorRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Author not found with id: {}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Author not found"
                            );
                        });

        log.info(
                "Author found with id: {}",
                author.getId()
        );

        return AuthorResponseDto.builder()
                .id(author.getId())
                .name(author.getName())
                .email(author.getEmail())
                .biography(author.getBiography())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthorResponseDto> getAllAuthors(int page, int size) {

        log.info(
                "Fetching authors - page: {}, size: {}",
                page,
                size
        );

        Pageable pageable = PageRequest.of(page, size);

        Page<Author> authorPage = authorRepository.findAll(pageable);

        log.info(
                "Total authors found: {}",
                authorPage.getTotalElements()
        );

        return authorPage.map(author ->
                AuthorResponseDto.builder()
                        .id(author.getId())
                        .name(author.getName())
                        .email(author.getEmail())
                        .biography(author.getBiography())
                        .build()
        );
    }

    @Override
    public AuthorResponseDto updateAuthor(
            Long id,
            AuthorRequestDto request
    ) {

        log.info(
                "Updating author with id: {}",
                id
        );

        Author author =
                authorRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Author not found for update with id: {}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Author not found"
                            );
                        });

        author.setName(request.getName());
        author.setEmail(request.getEmail());
        author.setBiography(
                request.getBiography()
        );

        Author updated =
                authorRepository.save(author);

        log.info(
                "Author updated successfully with id: {}",
                updated.getId()
        );

        return AuthorResponseDto.builder()
                .id(updated.getId())
                .name(updated.getName())
                .email(updated.getEmail())
                .biography(updated.getBiography())
                .build();
    }

    @Override
    public void deleteAuthor(Long id) {

        log.info(
                "Deleting author with id: {}",
                id
        );

        Author author =
                authorRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Author not found for deletion with id: {}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Author not found"
                            );
                        });

        authorRepository.delete(author);

        log.info(
                "Author deleted successfully with id: {}",
                id
        );
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportAuthorsToCsv() {

        log.info(
                "Exporting authors to CSV"
        );

        List<Author> authors =
                authorRepository.findAll();

        StringBuilder csv =
                new StringBuilder();

        csv.append(
                "Id,Name,Email,Biography\n"
        );

        authors.forEach(author ->

                csv.append(author.getId())
                        .append(",")
                        .append(author.getName())
                        .append(",")
                        .append(author.getEmail())
                        .append(",")
                        .append(author.getBiography())
                        .append("\n")
        );

        log.info(
                "Successfully exported {} authors",
                authors.size()
        );

        return csv.toString()
                .getBytes(
                        StandardCharsets.UTF_8
                );
    }
    @Override
    @Transactional
    public void importAuthorsFromCsv(
            MultipartFile file
    ) {

        log.info(
                "Importing authors from CSV file: {}",
                file.getOriginalFilename()
        );

        try (

                Reader reader =
                        new InputStreamReader(
                                file.getInputStream()
                        );

                CSVParser csvParser =
                        new CSVParser(
                                reader,
                                CSVFormat.DEFAULT
                                        .builder()
                                        .setHeader()
                                        .setSkipHeaderRecord(true)
                                        .build()
                        )

        ) {

            for (CSVRecord record : csvParser) {

                String name =
                        record.get("name");

                String email =
                        record.get("email");

                String biography =
                        record.get("biography");

                if (
                        authorRepository.existsByEmail(
                                email
                        )
                ) {

                    log.warn(
                            "Skipping duplicate author email: {}",
                            email
                    );

                    continue;
                }

                Author author =
                        Author.builder()
                                .name(name)
                                .email(email)
                                .biography(biography)
                                .build();

                authorRepository.save(author);

                log.info(
                        "Imported author: {}",
                        email
                );
            }

        } catch (Exception e) {

            log.error(
                    "Error importing authors from CSV",
                    e
            );

            throw new RuntimeException(
                    "Failed to import CSV file"
            );
        }
    }
}