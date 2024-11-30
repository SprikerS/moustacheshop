package dev.sprikers.moustacheshop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SummaryModel {
    private int usersTotal;
    private int usersActive;
    private int usersInactive;
    private int usersCustomers;
    private int usersEmployees;
    private int usersAdmins;
    private int produtcsActive;
    private int productsTotal;
    private int produtcsInactive;
    private int ordersTotal;
    private String topEmployee;
    private String topCustomer;
}
