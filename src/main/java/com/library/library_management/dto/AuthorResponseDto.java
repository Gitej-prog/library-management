package com.library.library_management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorResponseDto {

    private Long id;

    private String name;

    private String email;

    private String biography;
}