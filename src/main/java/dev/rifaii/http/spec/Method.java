package dev.rifaii.http.spec;

import java.util.Arrays;

public enum Method {
    GET,
    POST,
    PUT,
    PATCH,
    OPTIONS, //https://datatracker.ietf.org/doc/html/rfc9110#name-options
    DELETE,
    HEAD,
    TRACE; //https://datatracker.ietf.org/doc/html/rfc9110#name-trace
    //CONNECT --Relevant only for proxies

    public static boolean isValidMethod(String method) {
        return Arrays.stream(Method.values())
            .map(Method::name)
            .anyMatch(method::equals);
    }
}
