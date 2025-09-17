package com.example.book_service.exception;


// 409 - Conflict (por ejemplo, crear libro duplicado o conflicto de datos)
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}