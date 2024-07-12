package org.example.schoolequipment.presentation.auth.home;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.schoolequipment.model.Equipment;
import org.example.schoolequipment.model.EquipmentType;
import org.example.schoolequipment.model.Location;
import org.example.schoolequipment.model.Supplier;

import java.util.HashMap;
import java.util.Map;

public class HomeState {
    private final StringProperty query = new SimpleStringProperty("");
    private final ObservableList<Equipment> equipments = FXCollections.observableArrayList();
    private final ObservableList<EquipmentType> equipmentTypes = FXCollections.observableArrayList();
    private final ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
    private final ObservableList<Location> locations = FXCollections.observableArrayList();
    private final StringProperty error = new SimpleStringProperty("");
    private final StringProperty formError = new SimpleStringProperty("");
    private final ObjectProperty<Map<String, String>> filters = new SimpleObjectProperty<>(new HashMap<>());
    private final ObjectProperty<Map<String, String>> updateData = new SimpleObjectProperty<>(new HashMap<>());
    private final ObjectProperty<Map<String, String>> equipmentData = new SimpleObjectProperty<>(new HashMap<>());
    private final IntegerProperty totalItems = new SimpleIntegerProperty(0);
    private final SimpleBooleanProperty loading = new SimpleBooleanProperty(false);

    public BooleanProperty loadingProperty() {
        return loading;
    }

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

    public ObservableList<Equipment> getEquipments() {
        return equipments;
    }

    public ObservableList<EquipmentType> getEquipmentTypes() {
        return equipmentTypes;
    }

    public ObservableList<Supplier> getSuppliers() {
        return suppliers;
    }

    public ObservableList<Location> getLocations() {
        return locations;
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

    public Map<String, String> getEquipmentData() {
        return equipmentData.get();
    }

    public ObjectProperty<Map<String, String>> equipmentDataProperty() {
        return equipmentData;
    }

    public void setEquipmentData(Map<String, String> equipmentData) {
        this.equipmentData.set(equipmentData);
    }

    public IntegerProperty totalItemsProperty() {
        return totalItems;
    }

    public int getTotalItems() {
        return totalItems.get();
    }

    public void setTotalItems(int totalItems) {
        this.totalItems.set(totalItems);
    }

    public void setLoading(boolean b) {
        loading.set(b);
    }
}