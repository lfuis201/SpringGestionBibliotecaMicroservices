package com.example.reservation_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleBadRequest() {
        BadRequestException ex = new BadRequestException("Invalid request");
        ResponseEntity<Map<String, Object>> response = handler.handleBadRequest(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(response.getBody().get("message")).isEqualTo("Invalid request");
    }

    @Test
    void testHandleNotFound() {
        ChangeSetPersister.NotFoundException ex = new ChangeSetPersister.NotFoundException();
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
    }

    @Test
    void testHandleGeneral() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(response.getBody().get("message")).isEqualTo("Unexpected error");
    }
}
