package dev.sprikers.moustacheshop.utils;

import java.util.prefs.Preferences;

public class JwtPreferencesManager {

    private static final String JWT_KEY = "jwt_token";
    private static final Preferences prefs = Preferences.userRoot().node("moustacheshop");

    public static String getJwt() {
        return prefs.get(JWT_KEY, null);
    }

    public static void setJwt(String jwt) {
        prefs.put(JWT_KEY, jwt);
    }

    public static void removeJwt() {
        prefs.remove(JWT_KEY);
    }

    public static boolean isJWT() {
        return getJwt() != null;
    }

}
