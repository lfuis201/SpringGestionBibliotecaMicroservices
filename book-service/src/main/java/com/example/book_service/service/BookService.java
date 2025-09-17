package com.example.book_service.service;


import com.example.book_service.dto.BookDTO;

import java.util.List;

public interface BookService {
    List<BookDTO> getAllBooks();
    BookDTO getBookById(Long id);
    BookDTO createBook(BookDTO bookDTO);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
    void deactivateBook(Long id);

    BookDTO reduceBookUnits(Long id);
    BookDTO increaseBookUnits(Long id);
}