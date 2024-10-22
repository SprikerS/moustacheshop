package dev.sprikers.moustacheshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String names;
    private String paternalSurname;
    private String maternalSurname;
    private String dni;
    private String email;
    private Integer phoneNumber;
}
