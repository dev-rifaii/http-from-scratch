package dev.rifaii.http.spec;

//TODO: validate. GPT generated
public class HttpHeader {
    public enum HttpHeaders {
        // General Headers
        CACHE_CONTROL("Cache-Control"),
        CONNECTION("Connection"),
        DATE("Date"),
        PRAGMA("Pragma"),
        TRAILER("Trailer"),
        TRANSFER_ENCODING("Transfer-Encoding"),
        UPGRADE("Upgrade"),
        VIA("Via"),
        WARNING("Warning"),

        // Request Headers
        ACCEPT("Accept"),
        ACCEPT_CHARSET("Accept-Charset"),
        ACCEPT_ENCODING("Accept-Encoding"),
        ACCEPT_LANGUAGE("Accept-Language"),
        AUTHORIZATION("Authorization"),
        EXPECT("Expect"),
        FROM("From"),
        HOST("Host"),
        IF_MATCH("If-Match"),
        IF_MODIFIED_SINCE("If-Modified-Since"),
        IF_NONE_MATCH("If-None-Match"),
        IF_RANGE("If-Range"),
        IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
        MAX_FORWARDS("Max-Forwards"),
        PROXY_AUTHORIZATION("Proxy-Authorization"),
        RANGE("Range"),
        REFERER("Referer"),
        TE("TE"),
        USER_AGENT("User-Agent"),

        // Response Headers
        ACCEPT_RANGES("Accept-Ranges"),
        AGE("Age"),
        ETAG("ETag"),
        LOCATION("Location"),
        PROXY_AUTHENTICATE("Proxy-Authenticate"),
        RETRY_AFTER("Retry-After"),
        SERVER("Server"),
        VARY("Vary"),
        WWW_AUTHENTICATE("WWW-Authenticate"),

        // Entity Headers
        ALLOW("Allow"),
        CONTENT_ENCODING("Content-Encoding"),
        CONTENT_LANGUAGE("Content-Language"),
        CONTENT_LENGTH("Content-Length"),
        CONTENT_LOCATION("Content-Location"),
        CONTENT_MD5("Content-MD5"),
        CONTENT_RANGE("Content-Range"),
        CONTENT_TYPE("Content-Type"),
        EXPIRES("Expires"),
        LAST_MODIFIED("Last-Modified"),

        // Commonly Used Non-Standard Headers
        X_REQUESTED_WITH("X-Requested-With"),
        X_FORWARDED_FOR("X-Forwarded-For"),
        X_FRAME_OPTIONS("X-Frame-Options"),
        X_XSS_PROTECTION("X-XSS-Protection"),
        X_CONTENT_TYPE_OPTIONS("X-Content-Type-Options"),
        DNT("DNT"),
        ORIGIN("Origin"),
        UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests");

        private final String headerName;

        HttpHeaders(String headerName) {
            this.headerName = headerName;
        }

        public String getHeaderName() {
            return headerName;
        }

        @Override
        public String toString() {
            return headerName;
        }
    }
}
