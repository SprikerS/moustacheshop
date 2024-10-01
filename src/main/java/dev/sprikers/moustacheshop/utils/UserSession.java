package dev.sprikers.moustacheshop.utils;

import lombok.Getter;
import lombok.Setter;

import dev.sprikers.moustacheshop.models.UserModel;

@Getter
@Setter
public class UserSession {

    private static UserSession instance;
    private UserModel userModel;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

}
