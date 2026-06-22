package com.library.library_management.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookResponseDto {

    private Long id;

    private String title;

    private String isbn;

    private LocalDate publishedDate;

    private Long authorId;

    private String authorName;
}