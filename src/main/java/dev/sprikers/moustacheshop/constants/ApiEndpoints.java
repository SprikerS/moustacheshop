package dev.sprikers.moustacheshop.constants;

public class ApiEndpoints {

    private static final String BASE_URL = "http://localhost:3000/api";

    public static final String AUTH_LOGIN = BASE_URL + "/user/login";
    public static final String CHECK_AUTH_STATUS = BASE_URL + "/user/check-auth-status";

    public static final String CATEGORIES = BASE_URL + "/categories";
    public static final String ORDER = BASE_URL + "/orders";
    public static final String PRODUCT = BASE_URL + "/products";
    public static final String REPORTS = BASE_URL + "/reports";
    public static final String USER = BASE_URL + "/user";

}
