package dev.sprikers.moustacheshop.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    CUSTOMER("customer", "Cliente"),
    EMPLOYEE("employee", "Empleado"),
    ADMIN("admin", "Administrador"),
    SUPERUSER("super-user", "Super Usuario");

    private final String role;
    private final String text;

    UserRole(String role, String text) {
        this.role = role;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}