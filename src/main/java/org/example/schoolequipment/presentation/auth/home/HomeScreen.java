package org.example.schoolequipment.presentation.auth.home;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;
import org.example.schoolequipment.model.Equipment;
import org.example.schoolequipment.model.EquipmentType;
import org.example.schoolequipment.model.Location;
import org.example.schoolequipment.model.Supplier;

import java.util.stream.Collectors;

public class HomeScreen extends Application {
    HomeViewModel viewModel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("School Equipment Management");

        Stage updateStage = new Stage();

        viewModel = new HomeViewModel(primaryStage);
        viewModel.fetchAll();

        HBox topBar = new HBox(10);

        TextField searchBox = new TextField();
        searchBox.setPromptText("Search");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            viewModel.state.query = searchBox.getText();
            viewModel.updateQuery();
        });


        CheckComboBox<EquipmentType> typeFilter = new CheckComboBox<>();
        viewModel.state.equipmentTypes.addListener((ListChangeListener.Change<? extends EquipmentType> c) -> {
            typeFilter.getItems().setAll(viewModel.state.equipmentTypes);
        });
        typeFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<EquipmentType>) c -> {
            String types = typeFilter.getCheckModel().getCheckedItems().stream()
                    .map(EquipmentType::get_id)
                    .collect(Collectors.joining(","));
            viewModel.state.filters.put("type", types);
            viewModel.updateQuery();
        });


        CheckComboBox<Location> locationFilter = new CheckComboBox<>();
        viewModel.state.locations.addListener((ListChangeListener.Change<? extends Location> c) -> {
            locationFilter.getItems().setAll(viewModel.state.locations);
        });
        locationFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<Location>) c -> {
            String locations = locationFilter.getCheckModel().getCheckedItems().stream()
                    .map(Location::get_id)
                    .collect(Collectors.joining(","));
            viewModel.state.filters.put("location", locations);
            viewModel.updateQuery();
        });

        CheckComboBox<Supplier> supplierFilter = new CheckComboBox<>();
        viewModel.state.suppliers.addListener((ListChangeListener.Change<? extends Supplier> c) -> {
            supplierFilter.getItems().setAll(viewModel.state.suppliers);
        });
        supplierFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<Supplier>) c -> {
            String suppliers = supplierFilter.getCheckModel().getCheckedItems().stream()
                    .map(Supplier::get_id)
                    .collect(Collectors.joining(","));
            viewModel.state.filters.put("supplier", suppliers);
            viewModel.updateQuery();
        });

        typeFilter.setTitle("Equipment Types (0)");
        locationFilter.setTitle("Locations (0)");
        supplierFilter.setTitle("Suppliers (0)");

        typeFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<EquipmentType>) c -> {
            int count = typeFilter.getCheckModel().getCheckedItems().size();
            typeFilter.setTitle("Equipment Types (" + count + ")"); // Cập nhật tiêu đề

            String types = typeFilter.getCheckModel().getCheckedItems().stream()
                    .map(EquipmentType::get_id)
                    .collect(Collectors.joining(","));
            viewModel.state.filters.put("type", types); // Gửi danh sách ID vào query
            viewModel.updateQuery();
        });

        locationFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<Location>) c -> {
            int count = locationFilter.getCheckModel().getCheckedItems().size();
            locationFilter.setTitle("Locations (" + count + ")");
            String locations = locationFilter.getCheckModel().getCheckedItems().stream()
                    .map(Location::get_id)
                    .collect(Collectors.joining(","));
            viewModel.state.filters.put("location", locations);
            viewModel.updateQuery();
        });

        supplierFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<Supplier>) c -> {
            int count = supplierFilter.getCheckModel().getCheckedItems().size();
            supplierFilter.setTitle("Suppliers (" + count + ")");
            String suppliers = supplierFilter.getCheckModel().getCheckedItems().stream()
                    .map(Supplier::get_id)
                    .collect(Collectors.joining(","));
            viewModel.state.filters.put("supplier", suppliers);
            viewModel.updateQuery();
        });


        Button refreshButton = new Button("Clear filter");
        refreshButton.setOnAction(e -> {
            viewModel.clearFilters();
            typeFilter.getCheckModel().clearChecks();
            locationFilter.getCheckModel().clearChecks();
            supplierFilter.getCheckModel().clearChecks();
            viewModel.isClearingFilters = false;
            viewModel.updateQuery();
        });

        topBar.getChildren().addAll(searchBox, searchButton, typeFilter, locationFilter, supplierFilter, refreshButton);


        AnchorPane layout = new AnchorPane();

        TableView<Equipment> tableView = new TableView<>(viewModel.state.equipments);

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

        TableColumn<Equipment, Void> actionColumn = new TableColumn<>("Action");
        Callback<TableColumn<Equipment, Void>, TableCell<Equipment, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Equipment, Void> call(final TableColumn<Equipment, Void> param) {
                final TableCell<Equipment, Void> cell = new TableCell<>() {
                    private final Button btnUpdate = new Button("Update");
                    private final Button btnDelete = new Button("Delete");
                    private final HBox pane = new HBox(btnUpdate, btnDelete);

                    {
                        btnUpdate.setOnAction((ActionEvent event) -> {
                            Equipment data = getTableView().getItems().get(getIndex());
                            System.out.println("Update button clicked for: " + data.getEquipmentName());
                            Label equipmentName = new Label("Equipment Name");
                            Label description = new Label("Description");
                            Label statusLabel = new Label("Status");
                            Label typeLabel = new Label("Type");
                            Label priceLabel = new Label("Price");
                            Label locationLabel = new Label("Location");
                            Label supplierLabel = new Label("Supplier");

                            // combobox for status, type, location, supplier

                            ComboBox<String> status = new ComboBox<>();
                            ComboBox<String> type = new ComboBox<>();
                            ComboBox<String> location = new ComboBox<>();
                            ComboBox<String> supplier = new ComboBox<>();

                            type.setItems(viewModel.state.equipmentTypes.stream().map(EquipmentType::getTypeName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
                            location.setItems(viewModel.state.locations.stream().map(Location::getLocationName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
                            supplier.setItems(viewModel.state.suppliers.stream().map(Supplier::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
                            // textfield for equipment name, description
                            TextField equipmentNameField = new TextField();
                            TextField descriptionField = new TextField();
                            TextField priceField = new TextField();

                            equipmentNameField.setText(data.getEquipmentName());
                            descriptionField.setText(data.getDescription());
                            priceField.setText(String.valueOf(data.getPrice()));
                            //the status is in the format "in-use", "in-repair", "in-storage", convert to "In Use", "In Repair", "In Storage", replace the - with space
                            status.setValue(data.getStatus().replace("-", " ").substring(0, 1).toUpperCase() + data.getStatus().replace("-", " ").substring(1));
                            status.setItems(FXCollections.observableArrayList("New", "In use", "Not in use", "On loan", "Broken", "Under Repair", "Disposed", "Lost"));
                            type.setValue(data.getType().getTypeName());
                            location.setValue(data.getLocation().getLocationName());
                            supplier.setValue(data.getSupplier().getName());
                            // button for update
                            Button updateButton = new Button("Update");
                            updateButton.setOnAction(e -> {
                                System.out.println("Update button clicked");
                                viewModel.state.updateData.put("name", equipmentNameField.getText());
                                viewModel.state.updateData.put("description", descriptionField.getText());
                                //convert status to the format "in-use", "in-repair", "in-storage"
                                viewModel.state.updateData.put("status", status.getValue().replace(" ", "-").toLowerCase());
                                //for type, location, supplier, we need to get the ID of the selected item
                                viewModel.state.updateData.put("type", viewModel.state.equipmentTypes.stream().filter(equipmentType -> equipmentType.getTypeName().equals(type.getValue())).findFirst().get().get_id());
                                viewModel.state.updateData.put("location", viewModel.state.locations.stream().filter(location1 -> location1.getLocationName().equals(location.getValue())).findFirst().get().get_id());
                                viewModel.state.updateData.put("supplier", viewModel.state.suppliers.stream().filter(supplier1 -> supplier1.getName().equals(supplier.getValue())).findFirst().get().get_id());
                                viewModel.state.updateData.put("price", priceField.getText());
                                viewModel.state.updateData.put("equipmentId", data.get_id());
                                viewModel.updateEquipment();
                                updateStage.close();
                                viewModel.clearFilters();
                            });
                            GridPane layout = new GridPane();
                            layout.setHgap(10);
                            layout.setVgap(10);
                            layout.add(equipmentName, 0, 0);
                            layout.add(equipmentNameField, 1, 0);
                            layout.add(description, 0, 1);
                            layout.add(descriptionField, 1, 1);
                            layout.add(statusLabel, 0, 2);
                            layout.add(status, 1, 2);
                            layout.add(typeLabel, 0, 3);
                            layout.add(type, 1, 3);
                            layout.add(priceLabel, 0, 4);
                            layout.add(priceField, 1, 4);
                            layout.add(locationLabel, 0, 5);
                            layout.add(location, 1, 5);
                            layout.add(supplierLabel, 0, 6);
                            layout.add(supplier, 1, 6);
                            layout.add(updateButton, 1, 7);
                            Scene scene = new Scene(layout, 300, 300);
                            updateStage.setScene(scene);
                            updateStage.show();
                        });

                        btnDelete.setOnAction((ActionEvent event) -> {
                            Equipment data = getTableView().getItems().get(getIndex());
                            // Implement your delete logic here
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
                return cell;
            }
        };
        actionColumn.setCellFactory(cellFactory);


        nameColumn.setPrefWidth(200);
        descriptionColumn.setPrefWidth(200);
        statusColumn.setPrefWidth(100);
        currentLocation.setPrefWidth(150);
        supplierColumn.setPrefWidth(150);
        actionColumn.setPrefWidth(200);


        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(descriptionColumn);
        tableView.getColumns().add(statusColumn);
        tableView.getColumns().add(currentLocation);
        tableView.getColumns().add(supplierColumn);
        tableView.getColumns().add(actionColumn);

        layout.getChildren().addAll(topBar, tableView);

        AnchorPane.setTopAnchor(tableView, 30.0);
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