package dev.sprikers.moustacheshop.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class UserModel {
    private String id;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String dni;
    private String email;
    private Integer telefono;
    private List<String> roles;
    private String token;
    private boolean activo;

    public String getSurnames() {
        return apellidoPaterno + " " + apellidoMaterno;
    }
}
