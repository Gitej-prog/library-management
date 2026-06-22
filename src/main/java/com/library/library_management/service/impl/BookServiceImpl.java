package com.library.library_management.service.impl;

import com.library.library_management.dto.BookBulkUpdateRequest;
import com.library.library_management.dto.BookRequestDto;
import com.library.library_management.dto.BookResponseDto;
import com.library.library_management.entity.Author;
import com.library.library_management.entity.Book;
import com.library.library_management.exception.DuplicateResourceException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.AuthorRepository;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl
        implements BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    @Override
    public BookResponseDto createBook(
            BookRequestDto request
    ) {

        log.info(
                "Creating book with ISBN: {}",
                request.getIsbn()
        );

        if (bookRepository.existsByIsbn(
                request.getIsbn()
        )) {

            log.warn(
                    "Book already exists with ISBN: {}",
                    request.getIsbn()
            );

            throw new DuplicateResourceException(
                    "ISBN already exists"
            );
        }

        Author author =
                authorRepository.findById(
                                request.getAuthorId()
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "Author not found with id: {}",
                                    request.getAuthorId()
                            );

                            return new ResourceNotFoundException(
                                    "Author not found"
                            );
                        });

        Book book = Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publishedDate(
                        request.getPublishedDate()
                )
                .author(author)
                .build();

        Book savedBook =
                bookRepository.save(book);

        log.info(
                "Book created successfully with id: {}",
                savedBook.getId()
        );

        return mapToResponse(savedBook);
    }

    @Override
    @Transactional
    public void importBooksFromCsv(
            MultipartFile file
    ) {

        log.info(
                "Importing books from CSV: {}",
                file.getOriginalFilename()
        );

        try {

            CSVParser csvParser =
                    new CSVParser(
                            new InputStreamReader(
                                    file.getInputStream()
                            ),
                            CSVFormat.DEFAULT
                                    .builder()
                                    .setHeader()
                                    .setSkipHeaderRecord(true)
                                    .build()
                    );

            for (CSVRecord record : csvParser) {

                String title =
                        record.get("title");

                String isbn =
                        record.get("isbn");

                Long authorId =
                        Long.parseLong(
                                record.get("authorId")
                        );

                LocalDate publishedDate =
                        LocalDate.parse(
                                record.get("publishedDate")
                        );

                if (
                        bookRepository.existsByIsbn(
                                isbn
                        )
                ) {

                    log.warn(
                            "Skipping duplicate ISBN: {}",
                            isbn
                    );

                    continue;
                }

                Author author =
                        authorRepository.findById(
                                        authorId
                                )
                                .orElseThrow(
                                        () -> new ResourceNotFoundException(
                                                "Author not found"
                                        )
                                );

                Book book =
                        Book.builder()
                                .title(title)
                                .isbn(isbn)
                                .publishedDate(
                                        publishedDate
                                )
                                .author(author)
                                .build();

                bookRepository.save(book);
            }

            log.info(
                    "Books imported successfully"
            );

        } catch (Exception e) {

            log.error(
                    "Error importing books",
                    e
            );

            throw new RuntimeException(
                    "Failed to import books"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getBookById(
            Long id
    ) {

        log.info(
                "Fetching book with id: {}",
                id
        );

        Book book =
                bookRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Book not found with id: {}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Book not found"
                            );
                        });

        log.info(
                "Book found with id: {}",
                book.getId()
        );

        return mapToResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDto> getAllBooks(int page, int size) {

        log.info(
                "Fetching books - page: {}, size: {}",
                page,
                size
        );

        Pageable pageable = PageRequest.of(page, size);

        Page<Book> bookPage = bookRepository.findAll(pageable);

        log.info(
                "Total books found: {}",
                bookPage.getTotalElements()
        );

        return bookPage.map(this::mapToResponse);
    }

    @Override
    public BookResponseDto updateBook(
            Long id,
            BookRequestDto request
    ) {

        log.info(
                "Updating book with id: {}",
                id
        );

        Book book =
                bookRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Book not found for update with id: {}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Book not found"
                            );
                        });

        Author author =
                authorRepository.findById(
                                request.getAuthorId()
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "Author not found with id: {}",
                                    request.getAuthorId()
                            );

                            return new ResourceNotFoundException(
                                    "Author not found"
                            );
                        });

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublishedDate(
                request.getPublishedDate()
        );
        book.setAuthor(author);

        Book updatedBook =
                bookRepository.save(book);

        log.info(
                "Book updated successfully with id: {}",
                updatedBook.getId()
        );

        return mapToResponse(updatedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportBooksToCsv() {

        log.info("Exporting books to CSV");

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            CSVPrinter csvPrinter =
                    new CSVPrinter(
                            new OutputStreamWriter(
                                    outputStream
                            ),
                            CSVFormat.DEFAULT
                    );

            csvPrinter.printRecord(
                    "id",
                    "title",
                    "isbn",
                    "publishedDate",
                    "authorId",
                    "authorName"
            );

            List<Book> books =
                    bookRepository.findAll();

            for (Book book : books) {

                csvPrinter.printRecord(
                        book.getId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getPublishedDate(),
                        book.getAuthor().getId(),
                        book.getAuthor().getName()
                );
            }

            csvPrinter.flush();

            log.info(
                    "Exported {} books",
                    books.size()
            );

            return outputStream.toByteArray();

        } catch (Exception e) {

            log.error(
                    "Error exporting books",
                    e
            );

            throw new RuntimeException(
                    "Failed to export books"
            );
        }
    }

    @Override
    @Transactional
    public List<BookResponseDto> bulkUpdateBooks(
            List<BookBulkUpdateRequest> requests
    ) {

        log.info(
                "Bulk update started. Total books: {}",
                requests.size()
        );

        List<Book> booksToUpdate =
                requests.stream()
                        .map(request -> {

                            Book book =
                                    bookRepository.findById(
                                                    request.getId()
                                            )
                                            .orElseThrow(() -> {

                                                log.warn(
                                                        "Book not found with id: {}",
                                                        request.getId()
                                                );

                                                return new ResourceNotFoundException(
                                                        "Book not found with id: "
                                                                + request.getId()
                                                );
                                            });

                            Author author =
                                    authorRepository.findById(
                                                    request.getAuthorId()
                                            )
                                            .orElseThrow(() -> {

                                                log.warn(
                                                        "Author not found with id: {}",
                                                        request.getAuthorId()
                                                );

                                                return new ResourceNotFoundException(
                                                        "Author not found"
                                                );
                                            });

                            book.setTitle(request.getTitle());
                            book.setIsbn(request.getIsbn());
                            book.setPublishedDate(request.getPublishedDate());
                            book.setAuthor(author);

                            return book;
                        })
                        .toList();

        List<Book> savedBooks = bookRepository.saveAll(booksToUpdate);

        log.info(
                "Bulk update completed. Updated books: {}",
                savedBooks.size()
        );

        return savedBooks.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<BookResponseDto> createBooks(
            List<BookRequestDto> requests
    ) {

        log.info(
                "Bulk book creation started. Total books: {}",
                requests.size()
        );

        List<Book> booksToSave = requests.stream()
                .map(request -> {

                    if (bookRepository.existsByIsbn(request.getIsbn())) {
                        log.warn(
                                "Duplicate ISBN during bulk create: {}",
                                request.getIsbn()
                        );
                        throw new DuplicateResourceException(
                                "ISBN already exists: " + request.getIsbn()
                        );
                    }

                    Author author =
                            authorRepository.findById(request.getAuthorId())
                                    .orElseThrow(() -> new ResourceNotFoundException(
                                            "Author not found with id: " + request.getAuthorId()
                                    ));

                    return Book.builder()
                            .title(request.getTitle())
                            .isbn(request.getIsbn())
                            .publishedDate(request.getPublishedDate())
                            .author(author)
                            .build();
                })
                .toList();

        List<Book> savedBooks = bookRepository.saveAll(booksToSave);

        log.info(
                "Bulk book creation completed. Created: {}",
                savedBooks.size()
        );

        return savedBooks.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteBook(Long id) {

        log.info(
                "Deleting book with id: {}",
                id
        );

        Book book =
                bookRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Book not found for deletion with id: {}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Book not found"
                            );
                        });

        bookRepository.delete(book);

        log.info(
                "Book deleted successfully with id: {}",
                id
        );
    }

    private BookResponseDto mapToResponse(
            Book book
    ) {

        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publishedDate(
                        book.getPublishedDate()
                )
                .authorId(
                        book.getAuthor().getId()
                )
                .authorName(
                        book.getAuthor().getName()
                )
                .build();
    }
}