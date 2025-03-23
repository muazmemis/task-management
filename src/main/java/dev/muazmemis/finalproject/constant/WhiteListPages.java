package dev.muazmemis.finalproject.constant;

import java.util.List;
import java.util.stream.Stream;

public class WhiteListPages {

    private WhiteListPages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final List<String> SWAGGER_PAGES = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    private static final List<String> LOGIN_PAGES = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register"
    );

    public static String[] getWhiteListPages() {
        return Stream.concat(LOGIN_PAGES.stream(), SWAGGER_PAGES.stream())
                .toArray(String[]::new);
    }

}

