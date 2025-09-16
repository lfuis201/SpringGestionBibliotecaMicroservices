package com.example.reservation_service.client;

import com.example.reservation_service.dto.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "book-service",
        path = "/books",
        fallback = BookClientFallback.class
)
public interface BookClient {
    @GetMapping("/{id}")
    BookDTO getBookById(@PathVariable("id") Long id);
}

@Component
class BookClientFallback implements BookClient {
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
}
