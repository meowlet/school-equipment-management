package org.example.schoolequipment.presentation.auth.home;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.schoolequipment.model.Equipment;
import org.example.schoolequipment.model.EquipmentType;
import org.example.schoolequipment.model.Location;
import org.example.schoolequipment.model.Supplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeState {
    String query = "";
    ObservableList<Equipment> equipments = FXCollections.observableArrayList();
    ObservableList<EquipmentType> equipmentTypes = FXCollections.observableArrayList();
    ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
    ObservableList<Location> locations = FXCollections.observableArrayList();
    StringProperty error = new SimpleStringProperty("");
    Map<String, String> filters = new HashMap<>();
    Map<String, String> updateData = new HashMap<>();
}