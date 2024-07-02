package org.example.schoolequipment.api;

import org.example.schoolequipment.util.Constant;
import org.example.schoolequipment.util.HttpRequestHelper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class API {

    Map<String, String> headers = new HashMap<>();

    public API() {
        try {
            String token = new String(Files.readAllBytes(Paths.get(Constant.CREDENTIALS_FILE)));
            headers.put("Authorization", "Bearer " + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpRequestHelper.HttpResponse signUp(String userName, String password, String fullName, String email) {
        String requestUrl = Constant.API_ENDPOINT + "/auth/signup";

        String requestBody = String.format("""
                {
                    "userName": "%s",
                    "password": "%s",
                    "fullName": "%s",
                    "email": "%s"
                }""", userName, password, fullName, email);

        return HttpRequestHelper.sendPostRequest(requestUrl, requestBody, null);
    }

    public HttpRequestHelper.HttpResponse signIn(String userName, String password) {
        String requestUrl = Constant.API_ENDPOINT + "/auth/signin";

        String requestBody = String.format("{\n" +
                "    \"identifier\": \"%s\",\n" +
                "    \"password\": \"%s\"\n" +
                "}", userName, password);

        return HttpRequestHelper.sendPostRequest(requestUrl, requestBody, null);
    }

    public HttpRequestHelper.HttpResponse fetchTypes() {
        String requestUrl = Constant.API_ENDPOINT + "/type";
        HttpRequestHelper.HttpResponse response = HttpRequestHelper.sendGetRequest(requestUrl, headers);
        return HttpRequestHelper.sendGetRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse fetchLocations() {
        String requestUrl = Constant.API_ENDPOINT + "/location";

        return HttpRequestHelper.sendGetRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse fetchSuppliers() {
        String requestUrl = Constant.API_ENDPOINT + "/supplier";

        return HttpRequestHelper.sendGetRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse fetchEquipments(String query) {
        String requestUrl = Constant.API_ENDPOINT + "/equipment?" + query;

        return HttpRequestHelper.sendGetRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse updateEquipment(Map<String, String> updateData) {
        String requestUrl = Constant.API_ENDPOINT + "/equipment";

        String requestBody = String.format("""
                {
                    "equipmentId": "%s",
                    "name": "%s",
                    "description": "%s",
                    "type": "%s",
                    "status": "%s",
                    "location": "%s",
                    "supplier": "%s",
                    "price": %s
                }""", updateData.get("equipmentId"), updateData.get("name"), updateData.get("description"), updateData.get("type"), updateData.get("status"), updateData.get("location"), updateData.get("supplier"), updateData.get("price"));

        System.out.println(requestBody);
        return HttpRequestHelper.sendPatchRequest(requestUrl, requestBody, headers);
    }
}
