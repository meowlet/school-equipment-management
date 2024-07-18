package org.example.schoolequipment.presentation.auth.role_management;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.example.schoolequipment.api.API;
import org.example.schoolequipment.model.Permission;
import org.example.schoolequipment.model.Role;
import org.example.schoolequipment.presentation.auth.sign_in.SignInScreen;
import org.example.schoolequipment.util.HttpRequestHelper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class RoleManagementViewModel {
    private final ObservableList<Role> roles = FXCollections.observableArrayList();
    private final API api = new API();
    private final Stage stage;
    private final RoleManagementState state = new RoleManagementState();

    public RoleManagementViewModel(Stage stage) {
        this.stage = stage;
    }

    public ObservableList<Role> getRoles() {
        return roles;
    }

    public void fetchRoles() {
        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String query = buildQuery(true);
                HttpRequestHelper.HttpResponse roles = api.fetchRoles(query);

                responseHandler(roles);

                HttpRequestHelper.HttpResponse fullRoles = api.fetchRoles(buildQuery(false));
                if (fullRoles.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    Type roleListType = new TypeToken<List<Role>>() {}.getType();
                    List<Role> roleList = gson.fromJson(fullRoles.getBody(), roleListType);
                    Platform.runLater(() -> {
                        state.setTotalItems(roleList.size());
                    });
                } else {
                    Platform.runLater(() -> {
                        state.setError(roles.getBody());
                        state.setLoading(false);
                    });
                }

                if (roles.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    Type roleListType = new TypeToken<List<Role>>() {}.getType();
                    List<Role> roleList = gson.fromJson(roles.getBody(), roleListType);

                    Platform.runLater(() -> {
                        getRoles().clear();
                        getRoles().addAll(roleList);
                    });
                } else {
                    Platform.runLater(() -> state.setError(roles.getBody()));
                    getRoles().clear();
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

        for (Map.Entry<String, String> filter : state.getFilters().entrySet()) {
            if ("limit".equals(filter.getKey()) && !includeLimit) {
                continue;
            }

            if ("page".equals(filter.getKey()) && !includeLimit) {
                continue;
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

    public void clearFilters() {
        getState().getFilters().clear();
        state.totalItemsProperty().set(0);
        fetchRoles();
    }

    public void createRole(String name, String description, List<Permission> permissions) {
        HttpRequestHelper.HttpResponse response = api.createRole(name, description, permissions);
        if (response.getStatusCode() == 200) {
            fetchRoles();
        } else {
            // Handle error
            state.setError("Failed to create role: " + response.getBody());
        }
    }

    public void updateRole(String roleId, String name, String description, List<Permission> permissions) {
        HttpRequestHelper.HttpResponse response = api.updateRole(roleId, name, description, permissions);
        if (response.getStatusCode() == 200) {
            fetchRoles();
        } else {
            // Handle error
            state.setError("Failed to update role: " + response.getBody());
        }
    }

    public void deleteRole(String roleId) {
        HttpRequestHelper.HttpResponse response = api.deleteRole(roleId);
        if (response.getStatusCode() == 200) {
            fetchRoles();
        } else {
            // Handle error
            state.setError("Failed to delete role: " + response.getBody());
        }
    }

    public RoleManagementState getState() {
        return state;
    }
}