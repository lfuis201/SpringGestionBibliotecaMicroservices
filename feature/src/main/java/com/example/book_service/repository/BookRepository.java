package com.biblioteca.bookservice.repository;

import com.biblioteca.bookservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByNombre(String nombre);
}
