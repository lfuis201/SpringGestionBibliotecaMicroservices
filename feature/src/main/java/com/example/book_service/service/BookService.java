package com.biblioteca.bookservice.service;

import com.biblioteca.bookservice.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto saveBook(BookDto bookDto);
    BookDto getBookById(Long id);
    List<BookDto> getAllBooks();
    void deleteBook(Long id);
    BookDto getBookByNombre(String nombre);
}
