package com.livequery.agent.runtime.core;

import java.util.HashMap;
import java.util.Map;

class HttpResponse {

    /**
     * State of HTTP response message
     */
    final String version = "HTTP/1.1";
    int statusCode;
    String message;

    /**
     * Headers
     */
    Map<Object, Object> headers = new HashMap<>();

    /**
     * Response body
     */
    String body;

    /**
     * Newline
     */
    static final String NEWLINE = "\n";

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("%s %s %s", version, statusCode, message))
            .append(NEWLINE);

        headers.entrySet().forEach(e -> {
            String s = String.format("%s : %s", e.getKey(), e.getValue());
            buffer.append(s)
                .append(NEWLINE);
        });

        buffer.append(NEWLINE);
        buffer.append(body);
        buffer.append(NEWLINE);
        return buffer.toString();
    }
}
