package org.example.schoolequipment.presentation.auth.home;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.example.schoolequipment.api.API;
import org.example.schoolequipment.model.Equipment;
import org.example.schoolequipment.model.EquipmentType;
import org.example.schoolequipment.model.Location;
import org.example.schoolequipment.model.Supplier;
import org.example.schoolequipment.presentation.auth.sign_in.SignInScreen;
import org.example.schoolequipment.util.HttpRequestHelper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel {
    private final HomeState state;
    private final Stage stage;
    private final API api;

    public void setClearingFilters(boolean clearingFilters) {
        isClearingFilters = clearingFilters;
    }

    private boolean isClearingFilters = false;

    public HomeViewModel(Stage stage) {
        this.stage = stage;
        this.state = new HomeState();
        this.api = new API();

        // Set up listeners for reactive updates
        state.queryProperty().addListener((observable, oldValue, newValue) -> fetchEquipments());
        state.filtersProperty().addListener((observable, oldValue, newValue) -> fetchEquipments());
    }

    public HomeState getState() {
        return state;
    }

    private void responseHandler(HttpRequestHelper.HttpResponse response) {
        if (response.getStatusCode() == 401) {
            Platform.runLater(this::navigateToSignIn);
        }
    }

    private void navigateToSignIn() {
        new SignInScreen().start(stage);
    }

    public void fetchEquipments() {

        if (isClearingFilters) {
            return;
        }

        state.setLoading(true);

        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String query = buildQuery(true);
                HttpRequestHelper.HttpResponse equipments = api.fetchEquipments(query);

                responseHandler(equipments);

                HttpRequestHelper.HttpResponse fullEquipments = api.fetchEquipments(buildQuery(false));
                if (fullEquipments.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    Type equipmentListType = new TypeToken<List<Equipment>>() {
                    }.getType();
                    List<Equipment> equipmentList = gson.fromJson(fullEquipments.getBody(), equipmentListType);
                    Platform.runLater(() -> {
                        state.setTotalItems(equipmentList.size());
                    });
                } else {
                    Platform.runLater(() -> {
                        state.setError(equipments.getBody());
                        state.setLoading(false);
                    });
                }

                if (equipments.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    Type equipmentListType = new TypeToken<List<Equipment>>() {
                    }.getType();
                    List<Equipment> equipmentList = gson.fromJson(equipments.getBody(), equipmentListType);

                    Platform.runLater(() -> {
                        state.getEquipments().clear();
                        state.getEquipments().addAll(equipmentList);
                        state.setLoading(false);
                    });
                } else {
                    Platform.runLater(() -> state.setError(equipments.getBody()));
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
        state.setFilters(new HashMap<>());
        state.setQuery("");
        isClearingFilters = false;
        fetchEquipments();
    }

    public void fetchAll() {
        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                fetchEquipments();
                HttpRequestHelper.HttpResponse equipmentTypes = api.fetchTypes();
                HttpRequestHelper.HttpResponse suppliers = api.fetchSuppliers();
                HttpRequestHelper.HttpResponse locations = api.fetchLocations();

                responseHandler(equipmentTypes);

                if (locations.getStatusCode() == 200 && equipmentTypes.getStatusCode() == 200 && suppliers.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    Type equipmentTypeListType = new TypeToken<List<EquipmentType>>() {
                    }.getType();
                    Type supplierListType = new TypeToken<List<Supplier>>() {
                    }.getType();
                    Type locationListType = new TypeToken<List<Location>>() {
                    }.getType();
                    List<EquipmentType> equipmentTypesList = gson.fromJson(equipmentTypes.getBody(), equipmentTypeListType);
                    List<Supplier> supplierList = gson.fromJson(suppliers.getBody(), supplierListType);
                    List<Location> locationList = gson.fromJson(locations.getBody(), locationListType);

                    Platform.runLater(() -> {
                        state.getEquipmentTypes().clear();
                        state.getEquipmentTypes().addAll(equipmentTypesList);
                        state.getSuppliers().clear();
                        state.getSuppliers().addAll(supplierList);
                        state.getLocations().clear();
                        state.getLocations().addAll(locationList);
                    });
                } else {
                    Platform.runLater(() -> state.setError("Failed to fetch all data"));
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

    public void updateEquipment() {
        Task<Void> updateTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpRequestHelper.HttpResponse response = api.updateEquipment(state.getUpdateData());
                Platform.runLater(() -> {
                    if (response.getStatusCode() == 200) {
                        fetchEquipments();
                        state.setFormError("Equipment updated successfully");
                    } else {
                        state.setFormError(response.getBody());
                    }
                });
                return null;
            }
        };

        new Thread(updateTask).start();
    }

    public void createEquipment() {
        Task<Void> createTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpRequestHelper.HttpResponse response = api.addEquipment(state.getEquipmentData());
                Platform.runLater(() -> {
                    if (response.getStatusCode() == 200) {
                        fetchEquipments();
                        state.setFormError("Equipment added successfully");
                    } else {
                        state.setFormError(response.getBody());
                    }
                });
                return null;
            }
        };

        new Thread(createTask).start();
    }

    public void deleteEquipment(String equipmentId) {
        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpRequestHelper.HttpResponse response = api.deleteEquipment(equipmentId);
                Platform.runLater(() -> {
                    if (response.getStatusCode() == 200) {
                        fetchEquipments();
                        state.setFormError("Equipment deleted successfully");
                    } else {
                        state.setFormError("Failed to delete equipment: " + response.getBody());
                    }
                });
                return null;
            }
        };

        new Thread(deleteTask).start();
    }
}