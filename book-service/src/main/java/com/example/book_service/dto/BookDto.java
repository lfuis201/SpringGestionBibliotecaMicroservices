package com.biblioteca.bookservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDto {
    private Long id;
    private String nombre;
    private String autor;
    private Integer unidades;
}
