package dev.rifaii.http.path;

public class PathMatcher {

    private static final String REGEX = "^\\/([a-zA-Z0-9]+|\\{[a-zA-Z0-9]+\\})(\\/([a-zA-Z0-9]+|\\{[a-zA-Z0-9]+\\}))*([a-zA-Z0-9]+)*$";

    public static boolean isValidPath(String path) {
        return path.matches(REGEX) || path.equals("/");
    }
}
