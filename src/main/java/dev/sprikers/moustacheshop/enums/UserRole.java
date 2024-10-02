package dev.sprikers.moustacheshop.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    CUSTOMER("customer"),
    EMPLOYEE("employee"),
    ADMIN("admin"),
    SUPERUSER("super-user");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }
}