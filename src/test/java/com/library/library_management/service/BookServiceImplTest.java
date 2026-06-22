package com.library.library_management.service;

import com.library.library_management.dto.BookRequestDto;
import com.library.library_management.dto.BookResponseDto;
import com.library.library_management.entity.Author;
import com.library.library_management.entity.Book;
import com.library.library_management.repository.AuthorRepository;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void createBook_ShouldReturnBookResponse() {

        Author author =
                Author.builder()
                        .id(1L)
                        .name("Robert Martin")
                        .build();

        BookRequestDto request =
                BookRequestDto.builder()
                        .title("Clean Code")
                        .isbn("9780132350884")
                        .publishedDate(LocalDate.now())
                        .authorId(1L)
                        .build();

        Book book =
                Book.builder()
                        .id(1L)
                        .title(request.getTitle())
                        .isbn(request.getIsbn())
                        .publishedDate(
                                request.getPublishedDate()
                        )
                        .author(author)
                        .build();

        when(bookRepository.existsByIsbn(
                request.getIsbn()
        )).thenReturn(false);

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(bookRepository.save(any(Book.class)))
                .thenReturn(book);

        BookResponseDto response =
                bookService.createBook(request);

        assertNotNull(response);

        assertEquals(
                "Clean Code",
                response.getTitle()
        );
    }

    @Test
    void getBookById_ShouldReturnBook() {

        Author author =
                Author.builder()
                        .id(1L)
                        .name("Robert Martin")
                        .build();

        Book book =
                Book.builder()
                        .id(1L)
                        .title("Clean Code")
                        .isbn("9780132350884")
                        .author(author)
                        .build();

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        BookResponseDto response =
                bookService.getBookById(1L);

        assertEquals(
                "Clean Code",
                response.getTitle()
        );
    }
}