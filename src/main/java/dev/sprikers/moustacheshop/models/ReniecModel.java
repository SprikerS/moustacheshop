package dev.sprikers.moustacheshop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ReniecModel {
    private String dni;
    private String names;
    private String paternalSurname;
    private String maternalSurname;
}
