package dev.sprikers.moustacheshop.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoModelo {
    private String id;
    private String nombre;
    private double precio;
    private int cantidad;
}
