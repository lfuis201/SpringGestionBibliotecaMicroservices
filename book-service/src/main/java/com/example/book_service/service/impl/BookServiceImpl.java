package com.example.book_service.service.impl;

import com.example.book_service.dto.BookDTO;
import com.example.book_service.exception.BadRequestException;
import com.example.book_service.exception.ResourceNotFoundException;
import com.example.book_service.mapper.BookMapper;
import com.example.book_service.model.Book;
import com.example.book_service.repository.BookRepository;
import com.example.book_service.service.BookService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<BookDTO> getAllBooks() {
        List<BookDTO> books = bookRepository.findAll()
                .stream()
                .filter(Book::isActive)
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());

        if (books.isEmpty()) {
            throw new ResourceNotFoundException("No books available");
        }

        return books;
    }

    // Circuit Breaker aplicado
    @Override
    @CircuitBreaker(name = "bookServiceCB", fallbackMethod = "getBookByIdFallback")
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        if (!book.isActive()) {
            throw new ResourceNotFoundException("Book with id " + id + " is inactive");
        }

        return bookMapper.toDTO(book);
    }

    public BookDTO getBookByIdFallback(Long id, Throwable throwable) {
        // Retorna un objeto vacío o con valores por defecto en caso de fallo
        return BookDTO.builder()
                .title("Unavailable")
                .author("Unknown")
                .units(0)
                .available(false)
                .build();
    }

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        validateBookData(bookDTO);

        Book book = bookMapper.toEntity(bookDTO);
        book.setActive(true);
        book.setAvailable(book.getUnits() > 0);

        return bookMapper.toDTO(bookRepository.save(book));
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        if (!book.isActive()) {
            throw new BadRequestException("Cannot update an inactive book with id " + id);
        }

        validateBookData(bookDTO);

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setUnits(bookDTO.getUnits());
        book.setAvailable(bookDTO.getUnits() > 0);

        return bookMapper.toDTO(bookRepository.save(book));
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        bookRepository.delete(book);
    }

    @Override
    public void deactivateBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        if (!book.isActive()) {
            throw new BadRequestException("Book with id " + id + " is already inactive");
        }

        book.setActive(false);
        book.setAvailable(false);
        bookRepository.save(book);
    }


    public BookDTO reduceBookUnits(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        if (!book.isActive()) {
            throw new BadRequestException("Book with id " + id + " is inactive");
        }

        if (book.getUnits() <= 0) {
            throw new BadRequestException("Book with id " + id + " has no available units");
        }

        book.setUnits(book.getUnits() - 1);
        book.setAvailable(book.getUnits() > 0);

        return bookMapper.toDTO(bookRepository.save(book));
    }

    public BookDTO increaseBookUnits(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        if (!book.isActive()) {
            throw new BadRequestException("Book with id " + id + " is inactive");
        }

        book.setUnits(book.getUnits() + 1);
        book.setAvailable(true);

        return bookMapper.toDTO(bookRepository.save(book));
    }



    private void validateBookData(BookDTO bookDTO) {
        if (bookDTO.getTitle() == null || bookDTO.getTitle().isBlank()) {
            throw new BadRequestException("Invalid book data: title is required");
        }
        if (bookDTO.getUnits() <= 0) {
            throw new BadRequestException("Invalid book data: units must be greater than 0");
        }
    }
}