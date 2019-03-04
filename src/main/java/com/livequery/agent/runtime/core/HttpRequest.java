package com.livequery.agent.runtime.core;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class HttpRequest {

    /**
     * State (metadata) of Http request
     */
    String method;
    URL uri;
    String version;

    /**
     * Headers
     */
    Map<Object, Object> headers = new HashMap<>();

    /**
     * Body
     */
    String body;

    /**
     * Newline
     */
    static final String NEWLINE = "\n";

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("%s %s %s", method, uri.toString(), version))
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
