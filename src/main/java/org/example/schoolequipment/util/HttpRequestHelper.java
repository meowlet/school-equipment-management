package org.example.schoolequipment.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpRequestHelper {

    public static HttpResponse sendPostRequest(String requestUrl, String requestBody) {
        HttpResponse response = new HttpResponse();
        try {
            HttpURLConnection connection = getUrlConnection(requestUrl, requestBody);

            response.setStatusCode(connection.getResponseCode());

            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            response.getStatusCode() >= 400 ? connection.getErrorStream() : connection.getInputStream(),
                            StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBody.append(responseLine.trim());
                }
            }

            response.setBody(responseBody.toString());
            connection.disconnect();

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody(e.getMessage());
        }
        return response;
    }

    private static HttpURLConnection getUrlConnection(String requestUrl, String requestBody) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Set headers if needed
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
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
