package org.example.schoolequipment.api;

import org.example.schoolequipment.util.Constant;
import org.example.schoolequipment.util.HttpRequestHelper;

import java.util.HashMap;
import java.util.Map;

public class API {
    public HttpRequestHelper.HttpResponse signUp(String userName, String password, String fullName, String email) {
        String requestUrl = Constant.API_ENDPOINT + "/auth/signup";

        String requestBody = String.format("{\n" +
                "    \"userName\": \"%s\",\n" +
                "    \"password\": \"%s\",\n" +
                "    \"fullName\": \"%s\",\n" +
                "    \"email\": \"%s\"\n" +
                "}", userName, password, fullName, email);

        HttpRequestHelper.HttpResponse response = HttpRequestHelper.sendPostRequest(requestUrl, requestBody, null);
        return response;
    }

    public HttpRequestHelper.HttpResponse signIn(String userName, String password) {
        String requestUrl = Constant.API_ENDPOINT + "/auth/signin";

        String requestBody = String.format("{\n" +
                "    \"identifier\": \"%s\",\n" +
                "    \"password\": \"%s\"\n" +
                "}", userName, password);

        HttpRequestHelper.HttpResponse response = HttpRequestHelper.sendPostRequest(requestUrl, requestBody, null);
        return response;
    }

    public HttpRequestHelper.HttpResponse fetchEquipments() {
        String requestUrl = Constant.API_ENDPOINT + "/equipment";

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJfaWQiOiI2NjU0Y2Q4MjBiOGYyZDM1NzU0NmMyNWQifQ.I-yZuYke3rp7mCmtTvrjie55x-3ToEfrkPEeyCCcw3k");

        HttpRequestHelper.HttpResponse response = HttpRequestHelper.sendGetRequest(requestUrl, headers);
        return response;
    }
}
