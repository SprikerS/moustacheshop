package dev.sprikers.moustacheshop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ReniecModel {
    private String dni;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
}
