package org.example.schoolequipment.presentation.auth.user_management;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.example.schoolequipment.api.API;
import org.example.schoolequipment.model.Equipment;
import org.example.schoolequipment.model.Permission;
import org.example.schoolequipment.model.Role;
import org.example.schoolequipment.model.User;
import org.example.schoolequipment.presentation.auth.sign_in.SignInScreen;
import org.example.schoolequipment.util.HttpRequestHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserManagementViewModel {
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<Role> roles = FXCollections.observableArrayList();
    private final API api = new API();
    private final Stage stage;

    public UserManagementViewModel(Stage stage) {
        this.stage = stage;
    }

    private final UserManagementState state = new UserManagementState();

    public ObservableList<User> getUsers() {
            return users;
    }

    public ObservableList<Role> getRoles() {
        return roles;
    }

    public void fetchUsers() {
        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String query = buildQuery(true);
                HttpRequestHelper.HttpResponse users = api.fetchUsers(query);

                responseHandler(users);

                HttpRequestHelper.HttpResponse fullUser = api.fetchUsers(buildQuery(false));
                if (fullUser.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    Type equipmentListType = new TypeToken<List<User>>() {
                    }.getType();
                    List<Equipment> userList = gson.fromJson(fullUser.getBody(), equipmentListType);
                    Platform.runLater(() -> {
                        state.setTotalItems(userList.size());
                    });
                } else {
                    Platform.runLater(() -> {
                        state.setError(users.getBody());
                        state.setLoading(false);
                    });
                }

                if (users.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    Type equipmentListType = new TypeToken<List<User>>() {
                    }.getType();
                    List<User> equipmentList = gson.fromJson(users.getBody(), equipmentListType);

                    Platform.runLater(() -> {
                        getUsers().clear();
                        getUsers().addAll(equipmentList);
                        System.out.println(equipmentList);
                    });
                } else {
                    Platform.runLater(() -> state.setError(users.getBody()));
                    getUsers().clear();
                    state.setLoading(false);
                }

                return null;
            }
        };

        fetchTask.setOnFailed(e -> {
            Throwable exception = e.getSource().getException();
            exception.printStackTrace();
        });

        new Thread(fetchTask).start();
    }

    private void responseHandler(HttpRequestHelper.HttpResponse response) {
        if (response.getStatusCode() == 401) {
            Platform.runLater(this::navigateToSignIn);
        }
    }

    private void navigateToSignIn() {
        new SignInScreen().start(stage);
    }

    private String buildQuery(boolean includeLimit) {
        StringBuilder query = new StringBuilder();

        if (!state.getQuery().isEmpty()) {
            query.append("query=").append(state.getQuery().replace(" ", "+"));
        }

        boolean limitSet = false;

        // Iterate over filters, ignoring the limit filter if found
        for (Map.Entry<String, String> filter : state.getFilters().entrySet()) {
            if ("limit".equals(filter.getKey()) && !includeLimit) {
                continue; // Skip adding the limit filter to the query
            }

            if ("page".equals(filter.getKey()) && !includeLimit) {
                continue; // Skip adding the page filter to the query
            }

            if ("roleId".equals(filter.getKey()) && "all".equals(filter.getValue())) {
                continue; // Skip adding the roleId filter if "all" is selected
            }

            if (!filter.getValue().isEmpty()) {
                if (!query.isEmpty()) {
                    query.append("&");
                }
                query.append(filter.getKey()).append("=").append(filter.getValue());
                if ("limit".equals(filter.getKey())) {
                    limitSet = true;
                }
            }
        }

        if (!limitSet && includeLimit) {
            if (!query.isEmpty()) {
                query.append("&");
            }
            query.append("limit=5");
        }

        return query.toString();
    }

    public void fetchRoles() {
        // Implement API call to fetch roles
        HttpRequestHelper.HttpResponse response = api.fetchRoles("");
        if (response.getStatusCode() == 200) {
            List<Role> roleList = new ArrayList<>();
            roleList.add(new Role("all", "All Roles", "All Roles", new ArrayList<>(), new Date(), new Date(), 0));
            roleList.addAll(parseRolesFromResponse(response.getBody()));
            roles.setAll(roleList);
        } else {
            // Handle error
        }
    }

    public void clearFilters() {
        getState().getFilters().clear();
        state.totalItemsProperty().set(0);
        fetchUsers();
    }

    public void createUser(String name, String email, String password, Role role) {
        // Implement API call to create user
        HttpRequestHelper.HttpResponse response = api.createUser(name, email, password, role.get_id());
        if (response.getStatusCode() == 200) {
            fetchUsers();
        } else {
            // Handle error
        }
    }

    public void updateUser(String userId, String name, String email, String password, Role role) {
        // Implement API call to update user
        HttpRequestHelper.HttpResponse response = api.updateUser(userId, name, email, password, role.get_id());
        if (response.getStatusCode() == 200) {
            fetchUsers();
        } else {
            // Handle error
        }
    }

    public void deleteUser(String userId) {
        // Implement API call to delete user
        HttpRequestHelper.HttpResponse response = api.deleteUser(userId);
        if (response.getStatusCode() == 200) {
            fetchUsers();
        } else {
            // Handle error
        }
    }

    public void createRole(String name, String description, List<Permission> permissions) {
        // Implement API call to create role
        HttpRequestHelper.HttpResponse response = api.createRole(name, description, permissions);
        if (response.getStatusCode() == 200) {
            fetchRoles();
        } else {
            // Handle error
        }
    }

    private List<Role> parseRolesFromResponse(String responseBody) {
        Gson gson = new Gson();
        Role[] roleArray = gson.fromJson(responseBody, Role[].class);
        return List.of(roleArray);
    }

    public UserManagementState getState() {
        return state;
    }
}