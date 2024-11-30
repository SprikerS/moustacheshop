package dev.sprikers.moustacheshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserUpdateRequest {
    private List<String> roles;
    private boolean activo;
}
