package com.livequery.agent.runtime.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

class HttpRequestHandler implements Runnable {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /**
     * Socket state
     */
    private final Socket socket;

    /**
     * HTTP methods
     */
    private static final String[] METHODS = new String[]{"GET", "POST"};

    /* RegEx for parsing request and header line info */
    private static final Pattern HTTP_REQUEST_LINE = Pattern.compile("(.*)\\s+(.*)\\s+(.*)");
    private static final Pattern HTTP_HEADER_LINE = Pattern.compile("(.*):(.*)");

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        handle();
    }

    private void handle() {
        BufferedReader in = null;
        PrintStream out = null;

        try {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintStream(socket.getOutputStream());

                /* Fetch */
                List<String> lines = in.lines().collect(Collectors.toList());

                /* Decode */
                HttpRequest request = decode(lines);

                /* Filter */
                HttpResponse response = filter(request);

                /* Write response back to output stream of socket */
                write(response, out);
            } catch (IOException e) {
                logger.error(String.format("Exception handling Http request : {%s}", e));
            } finally {
                /* Close streams */
                if (null != in) {
                    in.close();
                }

                if (null != out) {
                    out.close();
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Exception while Http request handling : {%s}", e));
        }
    }

    private HttpRequest decode(List<String> lines) {
        HttpRequest request = new HttpRequest();

        boolean requestLine = true;
        boolean headerLine = true;

        StringBuffer buffer = new StringBuffer();
        for (String s : lines) {
            try {
                /* if this is the first line then parse as per semantics of a request line */
                if (requestLine) {
                    Matcher matcher = HTTP_REQUEST_LINE.matcher(s);
                    if (matcher.find() && matcher.groupCount() >= 4) {
                        request.method = matcher.group(1);
                        request.uri = new URL(matcher.group(2));
                        request.version = matcher.group(3);
                    }
                    requestLine = false;
                } else if (headerLine) {
                    /* Header lines are separated from message body via an empty line */
                    if (StringUtils.isEmpty(s)) {
                        headerLine = false;
                        continue;
                    }

                    /* Every header line should have <key> : <value> syntax */
                    Matcher matcher = HTTP_HEADER_LINE.matcher(s);
                    if (matcher.find() && matcher.groupCount() >= 2) {
                        request.headers.put(matcher.group(1), matcher.group(2));
                    }
                } else {
                    /* Every other line is considered part of message body */
                    buffer.append(s).append('\n');
                }
            } catch (MalformedURLException e) {
                logger.error(String.format("Exception parsing Http request headers : {%s}", e));
            }
        }

        /* Set message body */
        request.body = buffer.toString();
        logger.debug(String.format("Request: %s", request.toString()));
        return request;
    }

    private HttpResponse filter(HttpRequest request) {
        String method = request.method;

        HttpResponse response = new HttpResponse();
        if (StringUtils.equals(method, METHODS[0])) {
            /* Fill response line */
            response.statusCode = 200;
            response.message = "OK";

            /* Response headers */
            response.headers
                .put("Date: ", DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now()));
            response.headers.put("Server: ", "livequery/0.0.1");
            response.headers
                .put("Last-Modified: ", DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now()));
            response.headers.put("Content-Length: ", "2");
            response.headers.put("Content-type: ", "text/plain");

            /* Response body */
            response.body = "OK";
        } else {
            /* Fill response line */
            response.statusCode = 500;
            response.message = "Internal Server Error";

            /* Response headers */
            response.headers
                .put("Date: ", DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now()));
            response.headers.put("Server: ", "livequery/0.0.1");
            response.headers
                .put("Last-Modified: ", DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now()));
            response.headers.put("Content-Length: ", "27");
            response.headers.put("Content-type: ", "text/plain");

            /* Response body */
            response.body = "Internal Server Error (500)";
        }

        logger.debug(String.format("Response: %s", response.toString()));
        return response;
    }

    private void write(HttpResponse response, PrintStream out) {
        out.print(response.toString());
    }
}
