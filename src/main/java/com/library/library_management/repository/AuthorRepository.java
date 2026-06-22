package com.library.library_management.repository;

import com.library.library_management.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  AuthorRepository extends JpaRepository<Author,Long> {
    Optional<Author> findByEmail(String email);
    boolean existsByEmail(String email);
}
