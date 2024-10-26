package dev.sprikers.moustacheshop.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequest {
    private String dni;
    private String names;
    private String paternalSurname;
    private String maternalSurname;
    private String email;
    private String password;
    private Integer phoneNumber;
    private List<String> roles;
}
