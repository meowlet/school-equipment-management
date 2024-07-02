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
import java.util.List;
import java.util.Map;

public class HomeViewModel {
    public HomeState state;

    private Stage stage;

    public boolean isClearingFilters = false;


    public HomeViewModel(Stage stage) {
        this.stage = stage;
        state = new HomeState();
    }

    public void responseHandler(HttpRequestHelper.HttpResponse response) {
        if (response.getStatusCode() == 401) {
            Platform.runLater(this::navigateToSignIn);
        }
    }

    public void navigateToSignIn() {
        new SignInScreen().start(stage);
    }

    public void fetchEquipments(String query) {
        System.out.println(isClearingFilters);
        if (isClearingFilters) {
            return;
        }
        Task<Void> fetchTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                API api = new API();
                HttpRequestHelper.HttpResponse equipments = api.fetchEquipments(query);

                responseHandler(equipments);

                if (equipments.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    Type equipmentListType = new TypeToken<List<Equipment>>() {
                    }.getType();
                    List<Equipment> equipmentList = gson.fromJson(equipments.getBody(), equipmentListType);

                    // Update the state on the JavaFX Application Thread
                    javafx.application.Platform.runLater(() -> {
                        state.equipments.clear();
                        state.equipments.addAll(equipmentList);
                    });
                } else {
                    javafx.application.Platform.runLater(() -> {
                        state.error.set(equipments.getBody());
                    });
                }

                return null;
            }
        };

        fetchTask.setOnFailed(e -> {
            // Handle any exceptions here
            Throwable exception = e.getSource().getException();
            exception.printStackTrace();
        });

        new Thread(fetchTask).start();
    }

    //clear states
    public void clearFilters() {
        this.isClearingFilters = true;
        state.filters.clear();
        fetchEquipments("");
    }

    public void fetchAll() {
        Task<Void> fetchTask = new Task<Void>() {
            API api = new API();
            @Override
            protected Void call() throws Exception {
                fetchEquipments("");
                HttpRequestHelper.HttpResponse equipmentTypes = api.fetchTypes();
                HttpRequestHelper.HttpResponse suppliers = api.fetchSuppliers();
                HttpRequestHelper.HttpResponse locations = api.fetchLocations();

                responseHandler(equipmentTypes);

                if (locations.getStatusCode() == 200 || equipmentTypes.getStatusCode() == 200 || suppliers.getStatusCode() == 200) {
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

                    // Update the state on the JavaFX Application Thread
                    javafx.application.Platform.runLater(() -> {
                        state.equipmentTypes.clear();
                        state.equipmentTypes.addAll(equipmentTypesList);
                        state.suppliers.clear();
                        state.suppliers.addAll(supplierList);
                        state.locations.clear();
                        state.locations.addAll(locationList);
                    });
                } else {
                    javafx.application.Platform.runLater(() -> {
                        state.error.set(equipmentTypes.getBody());
                    });
                }

                return null;
            }
        };

        fetchTask.setOnFailed(e -> {
            // Handle any exceptions here
            Throwable exception = e.getSource().getException();
            exception.printStackTrace();
        });

        new Thread(fetchTask).start();
    }


    public void updateQuery() {
        StringBuilder query = new StringBuilder();

        // Add the searchTerm to the query if it's not null or empty
        if (state.query != null && !state.query.isEmpty()) {
            String queryWithPlus = state.query.replace(" ", "+");
            query.append("query=").append(queryWithPlus);
        }

        // Add the filters to the query
        for (Map.Entry<String, String> filter : state.filters.entrySet()) {
            // Check if the filter value is not null or empty
            if (filter.getValue() != null && !filter.getValue().isEmpty()) {
                // If the query is not empty, add an '&' before the filter
                if (query.length() > 0) {
                    query.append("&");
                }
                query.append(filter.getKey()).append("=").append(filter.getValue());
            }
        }

        fetchEquipments(query.toString());
    }

    public void updateEquipment() {
        API api = new API();
        api.updateEquipment(state.updateData);
    }
}
