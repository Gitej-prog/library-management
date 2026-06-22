package com.library.library_management.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookBulkUpdateRequest {

    private Long id;

    private String title;

    private String isbn;

    private LocalDate publishedDate;

    private Long authorId;
}