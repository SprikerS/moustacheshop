package dev.sprikers.moustacheshop.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    CUSTOMER("cliente", "Cliente"),
    EMPLOYEE("empleado", "Empleado"),
    ADMIN("administrador", "Administrador"),
    SUPERUSER("super-usuario", "Super Usuario");

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