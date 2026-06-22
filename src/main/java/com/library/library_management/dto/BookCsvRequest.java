package com.library.library_management.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCsvRequest {

    private String title;
    private String isbn;
    private Long authorId;
    private String publishedDate;
}