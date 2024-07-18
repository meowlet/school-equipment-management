package org.example.schoolequipment.api;

import org.example.schoolequipment.model.Permission;
import org.example.schoolequipment.util.Constant;
import org.example.schoolequipment.util.HttpRequestHelper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
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

        System.out.println(HttpRequestHelper.sendGetRequest(requestUrl, headers).getBody());
        System.out.println(requestUrl);

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

        return HttpRequestHelper.sendPatchRequest(requestUrl, requestBody, headers);
    }

    public HttpRequestHelper.HttpResponse addEquipment(Map<String, String> equipmentData) {
        String requestUrl = Constant.API_ENDPOINT + "/equipment" ;


        String requestBody = String.format("""
                {
                    "name": "%s",
                    "description": "%s",
                    "type": "%s",
                    "status": "%s",
                    "location": "%s",
                    "supplier": "%s",
                    "price": %s
                }""", equipmentData.get("name"), equipmentData.get("description"), equipmentData.get("type"), equipmentData.get("status"), equipmentData.get("location"), equipmentData.get("supplier"), equipmentData.get("price"));


        System.out.println(requestBody);
        System.out.println(requestUrl);
        System.out.println(headers);
        return HttpRequestHelper.sendPostRequest(requestUrl, requestBody, headers);
    }

    public HttpRequestHelper.HttpResponse deleteEquipment(String equipmentId) {
        String requestUrl = Constant.API_ENDPOINT + "/equipment/" + equipmentId;

        return HttpRequestHelper.sendDeleteRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse getUser() {
        String requestUrl = Constant.API_ENDPOINT + "/user/me";

        return HttpRequestHelper.sendGetRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse fetchUsers(String query) {
        String requestUrl = Constant.API_ENDPOINT + "/user?" + query;

        System.out.println(requestUrl);

        return HttpRequestHelper.sendGetRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse fetchRoles(String query) {
        String requestUrl = Constant.API_ENDPOINT + "/role?" + query;

        System.out.println(requestUrl);

        return HttpRequestHelper.sendGetRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse fetchUsersByRole(String id) {
        String requestUrl = Constant.API_ENDPOINT + "/user/role/" + id;

        return HttpRequestHelper.sendGetRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse searchUsers(String query) {
        String requestUrl = Constant.API_ENDPOINT + "/user?query=" + query;

        return HttpRequestHelper.sendGetRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse createUser(String name, String email, String password, String id) {
        return null;
    }

    public HttpRequestHelper.HttpResponse updateUser(String userId, String name, String email, String password, String id) {
        String requestUrl = Constant.API_ENDPOINT + "/user";

        String requestBody = String.format("""
                {
                    "userId": "%s",
                    "fullName": "%s",
                    "email": "%s",
                    "password": "%s",
                    "roleId": "%s"
                }""", userId, name, email, password, id);

        return HttpRequestHelper.sendPatchRequest(requestUrl, requestBody, headers);
    }

    public HttpRequestHelper.HttpResponse deleteUser(String userId) {
        String requestUrl = Constant.API_ENDPOINT + "/user/" + userId;

        return HttpRequestHelper.sendDeleteRequest(requestUrl, headers);
    }

    public HttpRequestHelper.HttpResponse createRole(String name, String description, List<Permission> permissions) {
        String requestUrl = Constant.API_ENDPOINT + "/role";

        String requestBody = String.format("""
                {
                    "name": "%s",
                    "description": "%s",
                    "permissions": %s
                }""", name, description, permissions);

        System.out.println(requestBody);

        return HttpRequestHelper.sendPostRequest(requestUrl, requestBody, headers);
    }

    public HttpRequestHelper.HttpResponse updateRole(String roleId, String name, String description, List<Permission> permissions) {
        String requestUrl = Constant.API_ENDPOINT + "/role";

        String requestBody = String.format("""
                {
                    "roleId": "%s",
                    "name": "%s",
                    "description": "%s",
                    "permissions": %s
                }""", roleId, name, description, permissions);

        return HttpRequestHelper.sendPatchRequest(requestUrl, requestBody, headers);
    }

    public HttpRequestHelper.HttpResponse deleteRole(String roleId) {
        String requestUrl = Constant.API_ENDPOINT + "/role/" + roleId;

        return HttpRequestHelper.sendDeleteRequest(requestUrl, headers);
    }
}
