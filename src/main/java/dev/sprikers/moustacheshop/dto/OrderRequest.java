package dev.sprikers.moustacheshop.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String orderDate;
    private String dni;
    private String names;
    private String paternalSurname;
    private String maternalSurname;
    private List<Map<String, Object>> products; // Se usan mapas para los productos
}
