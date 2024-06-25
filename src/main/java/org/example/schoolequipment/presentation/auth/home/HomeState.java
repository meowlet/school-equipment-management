package org.example.schoolequipment.presentation.auth.home;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.schoolequipment.model.Equipment;

public class HomeState {
    StringProperty query = new SimpleStringProperty("");
    ObservableList<Equipment> equipments = FXCollections.observableArrayList();
    StringProperty error = new SimpleStringProperty("");
}
