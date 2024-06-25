package org.example.schoolequipment.presentation.auth.home;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.schoolequipment.model.Equipment;

public class HomeScreen extends Application {
    HomeViewModel viewModel;

    @Override
    public void start(Stage primaryStage) {
        viewModel = new HomeViewModel();
        viewModel.fetchEquipments();

        AnchorPane layout = new AnchorPane();
        Button fetchButton = new Button("Fetch Equipments");
        fetchButton.setOnAction(e -> viewModel.fetchEquipments());
        TableView<Equipment> tableView = new TableView<Equipment>(viewModel.state.equipments);

        TableColumn<Equipment, String> nameColumn = new TableColumn<>("Equipment Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));

        TableColumn<Equipment, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Equipment, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Equipment, String> currentLocation = new TableColumn<>("Current Location");
        currentLocation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation().getLocationName()));

        TableColumn<Equipment, String> supplierColumn = new TableColumn<>("Supplier");
        supplierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSupplier().getName()));

        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(descriptionColumn);
        tableView.getColumns().add(statusColumn);
        tableView.getColumns().add(currentLocation);
        tableView.getColumns().add(supplierColumn);

        layout.getChildren().add(tableView);
        layout.getChildren().add(fetchButton);

        // Make the TableView fill the entire width of the AnchorPane
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);

        Scene scene = new Scene(layout, 960, 540);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}