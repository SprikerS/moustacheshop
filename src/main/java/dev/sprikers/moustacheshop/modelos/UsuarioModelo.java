package dev.sprikers.moustacheshop.modelos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioModelo {
    private String id;
    private String nombres;
    private String apellidosPaternos;
    private String apellidosMaternos;
    private String DNI;
    private String correo;
    private Integer telefono;
    private List<String> roles;
    private String token;

    @JsonProperty("isActive")
    private boolean activo;
}
