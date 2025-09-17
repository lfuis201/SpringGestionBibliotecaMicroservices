package com.example.reservation_service.client.fallback;

import com.example.reservation_service.client.BookClient;
import com.example.reservation_service.dto.BookDTO;
import org.springframework.stereotype.Component;

@Component
public class BookClientFallback implements BookClient {
    @Override
    public BookDTO getBookById(Long id) {
        return BookDTO.builder()
                .id(id)
                .title("Unavailable Book")
                .author("N/A")
                .active(false)
                .available(false)
                .build();
    }

    @Override
    public BookDTO reduceBookUnits(Long id) {
        return BookDTO.builder()
                .id(id)
                .title("Unavailable Book")
                .author("N/A")
                .active(false)
                .available(false)
                .build();
    }

    @Override
    public BookDTO increaseBookUnits(Long id) {
        return BookDTO.builder()
                .id(id)
                .title("Unavailable Book")
                .author("N/A")
                .active(false)
                .available(false)
                .build();
    }
}