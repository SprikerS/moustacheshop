package dev.sprikers.moustacheshop.models;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderModel {
    private String id;
    private String fecha;
    private UserModel cliente;
    private UserModel empleado;
    private ArrayList<OrderDetailModel> detalles;
}
