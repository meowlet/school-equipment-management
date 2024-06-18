package org.example.schoolequipment.api;

import org.example.schoolequipment.util.Constant;
import org.example.schoolequipment.util.HttpRequestHelper;

public class API {
    public HttpRequestHelper.HttpResponse signUp(String userName, String password, String fullName, String email) {
        String requestUrl = Constant.API_ENDPOINT + "/auth/signup";

        String requestBody = String.format("{\n" +
                "    \"userName\": \"%s\",\n" +
                "    \"password\": \"%s\",\n" +
                "    \"fullName\": \"%s\",\n" +
                "    \"email\": \"%s\"\n" +
                "}", userName, password, fullName, email);

        HttpRequestHelper.HttpResponse response = HttpRequestHelper.sendPostRequest(requestUrl, requestBody);
        return response;
    }

    public HttpRequestHelper.HttpResponse signIn(String userName, String password) {
        String requestUrl = Constant.API_ENDPOINT + "/auth/signin";

        String requestBody = String.format("{\n" +
                "    \"identifier\": \"%s\",\n" +
                "    \"password\": \"%s\"\n" +
                "}", userName, password);

        HttpRequestHelper.HttpResponse response = HttpRequestHelper.sendPostRequest(requestUrl, requestBody);
        return response;
    }
}
