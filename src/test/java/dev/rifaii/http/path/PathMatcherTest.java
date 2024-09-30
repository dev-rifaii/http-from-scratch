package dev.rifaii.http.path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class PathMatcherTest {

    @ParameterizedTest
    @MethodSource("pathsProvider")
    void isValidPath(String path, boolean isValid) {
        Assertions.assertEquals(isValid, PathMatcher.isValidPath(path));
    }

    static Stream<Arguments> pathsProvider() {
        return Stream.of(
            // Valid paths
            Arguments.of("/", true),
            Arguments.of("/users", true),
            Arguments.of("/products/123", true),
            Arguments.of("/categories/books", true),
            Arguments.of("/users/789/orders", true),
            Arguments.of("/settings/profile", true),

            // Valid variable paths
            Arguments.of("/users/{userId}", true),
            Arguments.of("/products/{productId}/reviews", true),
            Arguments.of("/categories/{categoryId}/items/{itemId}", true),
            Arguments.of("/orders/{orderId}/details", true),
            Arguments.of("/shops/{shopId}/employees/{employeeId}", true),

            // Valid mixed paths
            Arguments.of("/api/v1/products/{productId}", true),
            Arguments.of("/api/v1/users/{userId}/orders", true),
            Arguments.of("/users/{userId}/orders/{orderId}", true),
            Arguments.of("/stores/{storeId}/departments/electronics/items/{itemId}", true),
            Arguments.of("/customers/{customerId}/orders/{orderId}/status", true),

            // Invalid paths
            Arguments.of("/users//", false), // Double slash not allowed
            Arguments.of("/products//123", false), // Double slash in the middle
            Arguments.of("/categories//{categoryId}", false), // Double slash with variable
            Arguments.of("users/{userId}", false), // Missing leading slash
            Arguments.of("/products/{}reviews", false), // Missing variable content in curly braces
            Arguments.of("/api/v1/users//orders", false), // Double slash between segments
            Arguments.of("/users/{userId}/orders/{orderId//}", false), // Misplaced curly brace
            Arguments.of("/orders/{orderId/details", false), // Missing closing curly brace
            Arguments.of("/shops/{shopId}/employees/{employeeId}/", false), // Trailing slash after variable
            Arguments.of("/customers//{customerId}/orders/{orderId}", false) // Empty segment before variable
        );
    }
}