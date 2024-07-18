package org.example.schoolequipment.presentation.auth.user_management;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.schoolequipment.model.Role;
import org.example.schoolequipment.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserManagementState {
    private final StringProperty query = new SimpleStringProperty("");
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<Role> roles = FXCollections.observableArrayList();
    private final StringProperty error = new SimpleStringProperty("");
    private final StringProperty formError = new SimpleStringProperty("");
    private final ObjectProperty<Map<String, String>> filters = new SimpleObjectProperty<>(new HashMap<>());
    private final ObjectProperty<Map<String, String>> updateData = new SimpleObjectProperty<>(new HashMap<>());
    private final ObjectProperty<Map<String, String>> userData = new SimpleObjectProperty<>(new HashMap<>());
    private final IntegerProperty totalItems = new SimpleIntegerProperty(0);
    private final SimpleBooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<User> selectedUser = new SimpleObjectProperty<>();
    private final ObjectProperty<Role> selectedRole = new SimpleObjectProperty<>();


    // Getters and setters for all properties
    public String getQuery() {
        return query.get();
    }

    public StringProperty queryProperty() {
        return query;
    }

    public void setQuery(String query) {
        this.query.set(query);
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public ObservableList<Role> getRoles() {
        return roles;
    }

    public String getError() {
        return error.get();
    }

    public StringProperty errorProperty() {
        return error;
    }

    public void setError(String error) {
        this.error.set(error);
    }

    public String getFormError() {
        return formError.get();
    }

    public StringProperty formErrorProperty() {
        return formError;
    }

    public void setFormError(String formError) {
        this.formError.set(formError);
    }

    public Map<String, String> getFilters() {
        return filters.get();
    }

    public ObjectProperty<Map<String, String>> filtersProperty() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters.set(filters);
    }

    public Map<String, String> getUpdateData() {
        return updateData.get();
    }

    public ObjectProperty<Map<String, String>> updateDataProperty() {
        return updateData;
    }

    public void setUpdateData(Map<String, String> updateData) {
        this.updateData.set(updateData);
    }

    public Map<String, String> getUserData() {
        return userData.get();
    }

    public ObjectProperty<Map<String, String>> userDataProperty() {
        return userData;
    }

    public void setUserData(Map<String, String> userData) {
        this.userData.set(userData);
    }

    public int getTotalItems() {
        return totalItems.get();
    }

    public IntegerProperty totalItemsProperty() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems.set(totalItems);
    }

    public boolean isLoading() {
        return loading.get();
    }

    public SimpleBooleanProperty loadingProperty() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading.set(loading);
    }

    public User getSelectedUser() {
        return selectedUser.get();
    }

    public ObjectProperty<User> selectedUserProperty() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser.set(selectedUser);
    }

    public Role getSelectedRole() {
        return selectedRole.get();
    }

    public ObjectProperty<Role> selectedRoleProperty() {
        return selectedRole;
    }

    public void setSelectedRole(Role selectedRole) {
        this.selectedRole.set(selectedRole);
    }
}