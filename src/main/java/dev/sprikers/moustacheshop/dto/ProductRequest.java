package dev.sprikers.moustacheshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String nombre;
    private double precio;
    private int stock;
    private String descripcion;
    private String categoriaId;
    private boolean activo;
}
