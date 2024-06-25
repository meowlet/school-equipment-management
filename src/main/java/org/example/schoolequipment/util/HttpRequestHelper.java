package org.example.schoolequipment.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequestHelper {

    public static HttpResponse sendPostRequest(String requestUrl, String requestBody, Map<String, String> headers) {
        HttpResponse response = new HttpResponse();
        try {
            HttpURLConnection connection = getUrlConnection(requestUrl, requestBody, "POST", headers);

            response.setStatusCode(connection.getResponseCode());
            response.setBody(readResponse(connection));

            connection.disconnect();
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody(e.getMessage());
        }
        return response;
    }

    public static HttpResponse sendGetRequest(String requestUrl, Map<String, String> headers) {
        HttpResponse response = new HttpResponse();
        try {
            HttpURLConnection connection = getUrlConnection(requestUrl, null, "GET", headers);

            response.setStatusCode(connection.getResponseCode());
            response.setBody(readResponse(connection));

            connection.disconnect();
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody(e.getMessage());
        }
        return response;
    }

    private static HttpURLConnection getUrlConnection(String requestUrl, String requestBody, String method, Map<String, String> headers) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        if ("POST".equals(method) && requestBody != null) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        return connection;
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream(),
                        StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBody.append(responseLine.trim());
            }
        }
        return responseBody.toString();
    }

    public static class HttpResponse {
        private int statusCode;
        private String body;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}