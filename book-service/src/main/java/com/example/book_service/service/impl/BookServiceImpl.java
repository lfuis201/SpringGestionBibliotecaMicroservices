package com.biblioteca.bookservice.service.impl;

import com.biblioteca.bookservice.dto.BookDto;
import com.biblioteca.bookservice.entity.Book;
import com.biblioteca.bookservice.repository.BookRepository;
import com.biblioteca.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookDto saveBook(BookDto bookDto) {
        Book book = Book.builder()
                .id(bookDto.getId())
                .nombre(bookDto.getNombre())
                .autor(bookDto.getAutor())
                .unidades(bookDto.getUnidades())
                .build();
        return mapToDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::mapToDto)
                .orElse(null);
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto getBookByNombre(String nombre) {
        return bookRepository.findByNombre(nombre)
                .map(this::mapToDto)
                .orElse(null);
    }

    private BookDto mapToDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .nombre(book.getNombre())
                .autor(book.getAutor())
                .unidades(book.getUnidades())
                .build();
    }
}
