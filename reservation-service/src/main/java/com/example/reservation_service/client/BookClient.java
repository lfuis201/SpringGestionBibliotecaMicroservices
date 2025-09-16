package com.example.reservation_service.client;

import com.example.reservation_service.dto.BookDTO;
import com.example.reservation_service.client.fallback.BookClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(
        name = "book-service",
        path = "/books",
        fallback = BookClientFallback.class
)
public interface BookClient {
    @GetMapping("/{id}")
    BookDTO getBookById(@PathVariable("id") Long id);

    @PutMapping("/{id}/reduce-units")
    BookDTO reduceBookUnits(@PathVariable("id") Long id);

    @PutMapping("/{id}/increase-units")
    BookDTO increaseBookUnits(@PathVariable("id") Long id);

}

