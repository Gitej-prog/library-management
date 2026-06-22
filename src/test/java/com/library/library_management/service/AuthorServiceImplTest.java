package com.library.library_management.service;

import com.library.library_management.dto.AuthorRequestDto;
import com.library.library_management.dto.AuthorResponseDto;
import com.library.library_management.entity.Author;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.AuthorRepository;
import com.library.library_management.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void createAuthor_ShouldReturnAuthorResponse() {

        AuthorRequestDto request =
                AuthorRequestDto.builder()
                        .name("Robert Martin")
                        .email("unclebob@gmail.com")
                        .biography("Clean Code Author")
                        .build();

        Author author =
                Author.builder()
                        .id(1L)
                        .name(request.getName())
                        .email(request.getEmail())
                        .biography(request.getBiography())
                        .build();

        when(authorRepository.existsByEmail(
                request.getEmail()
        )).thenReturn(false);

        when(authorRepository.save(any(Author.class)))
                .thenReturn(author);

        AuthorResponseDto response =
                authorService.createAuthor(request);

        assertNotNull(response);
        assertEquals(
                "Robert Martin",
                response.getName()
        );
    }

    @Test
    void getAuthorById_ShouldReturnAuthor() {

        Author author =
                Author.builder()
                        .id(1L)
                        .name("Robert Martin")
                        .email("unclebob@gmail.com")
                        .biography("Clean Code Author")
                        .build();

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        AuthorResponseDto response =
                authorService.getAuthorById(1L);

        assertEquals(
                1L,
                response.getId()
        );
    }

    @Test
    void getAuthorById_ShouldThrowException() {

        when(authorRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.getAuthorById(1L)
        );
    }
}