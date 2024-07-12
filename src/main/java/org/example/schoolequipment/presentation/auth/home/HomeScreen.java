package org.example.schoolequipment.presentation.auth.home;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.example.schoolequipment.model.Equipment;
import org.example.schoolequipment.model.EquipmentType;
import org.example.schoolequipment.model.Location;
import org.example.schoolequipment.model.Supplier;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeScreen extends Application {
    private HomeViewModel viewModel;
    private Stage updateStage;
    private TableView<Equipment> tableView = new TableView<>();
    private CheckComboBox<EquipmentType> typeFilter;
    private CheckComboBox<Location> locationFilter;
    private CheckComboBox<Supplier> supplierFilter;
    private Pagination pagination;
    private TextField itemsPerPageField;
    private int currentPage = 0;
    private int itemsPerPage = 5;
    private ProgressIndicator loadingIndicator = new ProgressIndicator();

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("School Equipment Management");
        updateStage = new Stage();
        viewModel = new HomeViewModel(primaryStage);
        viewModel.getState().loadingProperty().addListener((obs, oldVal, newVal) -> {
            showLoading(newVal);
        });
        viewModel.getState().totalItemsProperty().addListener((obs, oldVal, newVal) -> {
            pagination.setPageCount(calculatePageCount());
        });
        viewModel.getState().getFilters().put("limit", String.valueOf(itemsPerPage));
        viewModel.fetchAll();

        BorderPane layout = new BorderPane();
        layout.setTop(createNavBar());
        layout.setCenter(createMainContent());

        Scene scene = new Scene(layout, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/org/example/schoolequipment/globals.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    private void createLoadingIndicator() {
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setPrefSize(50, 50);
    }

    private HBox createPagination() {
        pagination = new Pagination();
        pagination.setPageCount(calculatePageCount());
        pagination.setCurrentPageIndex(currentPage);
        pagination.setMaxPageIndicatorCount(5);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            currentPage = newIndex.intValue();
            viewModel.getState().getFilters().put("page", String.valueOf(currentPage));
            viewModel.getState().getFilters().put("limit", String.valueOf(itemsPerPage));
            viewModel.fetchEquipments();
        });

        itemsPerPageField = new TextField(String.valueOf(itemsPerPage));
        itemsPerPageField.setId("itemsPerPageField");
        itemsPerPageField.setOnAction(e -> updateItemsPerPage());

        Label itemsPerPageLabel = new Label("Items per page:");

        HBox paginationBox = new HBox(10, itemsPerPageLabel, itemsPerPageField, pagination);
        paginationBox.setAlignment(Pos.CENTER);
        return paginationBox;
    }

    private void updateItemsPerPage() {
        try {
            int newItemsPerPage = Integer.parseInt(itemsPerPageField.getText());
            if (newItemsPerPage > 0) {
                itemsPerPage = newItemsPerPage;
                viewModel.getState().getFilters().put("limit", String.valueOf(itemsPerPage));
                viewModel.fetchEquipments();
            }
        } catch (NumberFormatException ex) {
            // Handle invalid input
            itemsPerPageField.setText(String.valueOf(itemsPerPage));
        }
    }

    private int calculatePageCount() {
        int totalItems = viewModel.getState().getTotalItems();
        System.out.println(totalItems);
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    private HBox createNavBar() {
        Button showSidebarButton = new Button("☰");
        showSidebarButton.getStyleClass().add("nav-button");

        Label appNameLabel = new Label("School Equipment Manager");
        appNameLabel.getStyleClass().add("app-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userNameLabel = new Label("John Doe");
        userNameLabel.getStyleClass().add("user-name");

        MenuButton userMenuButton = new MenuButton("▼");
        userMenuButton.getStyleClass().add("nav-button");
        userMenuButton.getItems().addAll(
                new MenuItem("Manage Profile"),
                new MenuItem("Settings"),
                new SeparatorMenuItem(),
                new MenuItem("Logout")
        );

        HBox navBar = new HBox(10, showSidebarButton, appNameLabel, spacer, userNameLabel, userMenuButton);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(10));
        navBar.getStyleClass().add("nav-bar");

        return navBar;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(10);
        mainContent.setPadding(new Insets(20));
        createLoadingIndicator();

        StackPane contentPane = new StackPane();
        contentPane.getChildren().addAll(
                new VBox(10, createTableView(), createPagination()),
                loadingIndicator
        );
        StackPane.setAlignment(loadingIndicator, Pos.CENTER);

        mainContent.getChildren().addAll(
                createTopBar(),
                contentPane
        );
        return mainContent;
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        tableView.setVisible(!show);
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(10);
        topBar.getChildren().addAll(
                createSearchBar(),
                createFilterBar()
        );
        topBar.getStyleClass().add("top-bar");
        return topBar;
    }

    private HBox createSearchBar() {
        TextField searchBox = new TextField();
        searchBox.setPromptText("Search");
        searchBox.textProperty().bindBidirectional(viewModel.getState().queryProperty());
        searchBox.setPrefWidth(300);
        searchBox.setId("searchBar");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> viewModel.fetchEquipments());

        Button createButton = new Button("Create Equipment");
        createButton.setOnAction(e -> showEquipmentForm(null));
        createButton.setId("createEquipmentButton");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBar = new HBox(10, searchBox, searchButton, spacer, createButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        return searchBar;
    }

    private HBox createFilterBar() {
        typeFilter = createFilterComboBox(
                "Equipment Types",
                viewModel.getState().getEquipmentTypes(),
                EquipmentType::get_id,
                "type"
        );

        locationFilter = createFilterComboBox(
                "Locations",
                viewModel.getState().getLocations(),
                Location::get_id,
                "location"
        );

        supplierFilter = createFilterComboBox(
                "Suppliers",
                viewModel.getState().getSuppliers(),
                Supplier::get_id,
                "supplier"
        );

        Button refreshButton = new Button("Clear Filters");
        refreshButton.setOnAction(e -> {
            viewModel.setClearingFilters(true);
            typeFilter.getCheckModel().clearChecks();
            locationFilter.getCheckModel().clearChecks();
            supplierFilter.getCheckModel().clearChecks();

            currentPage = 0;
            itemsPerPage = 5;
            itemsPerPageField.setText(String.valueOf(itemsPerPage));

            viewModel.clearFilters();
        });
        refreshButton.setId("clearButton");

        HBox filterBar = new HBox(20, typeFilter, locationFilter, supplierFilter, refreshButton);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        return filterBar;
    }

    private <T> CheckComboBox<T> createFilterComboBox(String title, javafx.collections.ObservableList<T> items,
                                                      java.util.function.Function<T, String> idExtractor,
                                                      String filterKey) {
        CheckComboBox<T> comboBox = new CheckComboBox<>(items);
        comboBox.setTitle(title);
        comboBox.setPrefWidth(200);

        comboBox.getCheckModel().getCheckedItems().addListener((javafx.collections.ListChangeListener.Change<? extends T> c) -> {
            int count = comboBox.getCheckModel().getCheckedItems().size();
            comboBox.setTitle(title + " (" + count + ")");

            String ids = comboBox.getCheckModel().getCheckedItems().stream()
                    .map(idExtractor)
                    .collect(Collectors.joining(","));
            viewModel.getState().getFilters().put(filterKey, ids);

            pagination.setCurrentPageIndex(0);

            viewModel.fetchEquipments();
        });

        return comboBox;
    }

    private TableView<Equipment> createTableView() {
        tableView = new TableView<>(viewModel.getState().getEquipments());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableView.setPrefHeight(itemsPerPage * 40 + 35);

        tableView.getColumns().addAll(
                createColumn("Equipment Name", "equipmentName", 200),
                createColumn("Price", e -> new SimpleStringProperty(NumberFormat.getNumberInstance(Locale.ENGLISH).format(e.getValue().getPrice())), 100),
                createColumn("Status", "status", 100),
                createColumn("Current Location", e -> new SimpleStringProperty(e.getValue().getLocation().getLocationName()), 150),
                createColumn("Supplier", e -> new SimpleStringProperty(e.getValue().getSupplier().getName()), 150),
                createActionColumn()
        );

        tableView.setRowFactory(tv -> {
            TableRow<Equipment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Equipment equipment = row.getItem();
                    showEquipmentDetails(equipment);
                }
            });
            return row;
        });

        return tableView;
    }

    private TableColumn<Equipment, String> createColumn(String title, String propertyName, double width) {
        TableColumn<Equipment, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setPrefWidth(width);
        return column;
    }

    private TableColumn<Equipment, String> createColumn(String title,
                                                        javafx.util.Callback<TableColumn.CellDataFeatures<Equipment, String>, javafx.beans.value.ObservableValue<String>> callback,
                                                        double width) {
        TableColumn<Equipment, String> column = new TableColumn<>(title);
        column.setCellValueFactory(callback);
        column.setPrefWidth(width);
        return column;
    }

    private TableColumn<Equipment, Void> createActionColumn() {
        TableColumn<Equipment, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnUpdate = new Button("Update");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(10, btnUpdate, btnDelete);

            {
                pane.setAlignment(Pos.CENTER);
                btnUpdate.getStyleClass().addAll("action-button", "update-button");
                btnDelete.getStyleClass().addAll("action-button", "delete-button");

                btnUpdate.setOnAction(event -> {
                    Equipment data = getTableView().getItems().get(getIndex());
                    showEquipmentForm(data);
                });

                btnDelete.setOnAction(event -> {
                    Equipment data = getTableView().getItems().get(getIndex());
                    viewModel.deleteEquipment(data.get_id());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        actionColumn.setPrefWidth(200);
        return actionColumn;
    }

    private void showEquipmentForm(Equipment equipment) {
        GridPane formLayout = new GridPane();
        formLayout.setPadding(new Insets(20));
        formLayout.setHgap(10);
        formLayout.setVgap(10);
        formLayout.getStyleClass().add("form-pane");

        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        TextField priceField = new TextField();
        ComboBox<String> statusComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "New", "In use", "Not in use", "On loan", "Broken", "Under Repair", "Disposed", "Lost"));
        ComboBox<String> typeComboBox = createComboBox(viewModel.getState().getEquipmentTypes(), EquipmentType::getTypeName);
        ComboBox<String> locationComboBox = createComboBox(viewModel.getState().getLocations(), Location::getLocationName);
        ComboBox<String> supplierComboBox = createComboBox(viewModel.getState().getSuppliers(), Supplier::getName);

        if (equipment != null) {
            nameField.setText(equipment.getEquipmentName());
            descriptionField.setText(equipment.getDescription());
            priceField.setText(String.valueOf(equipment.getPrice()));
            statusComboBox.setValue(formatStatus(equipment.getStatus()));
            typeComboBox.setValue(equipment.getType().getTypeName());
            locationComboBox.setValue(equipment.getLocation().getLocationName());
            supplierComboBox.setValue(equipment.getSupplier().getName());
        }

        Button submitButton = new Button(equipment == null ? "Create" : "Update");
        submitButton.setOnAction(e -> {
            updateViewModel(nameField, descriptionField, priceField, statusComboBox,
                    typeComboBox, locationComboBox, supplierComboBox, equipment);
            if (equipment == null) {
                viewModel.getState().getEquipmentData().put("name", nameField.getText());
                viewModel.getState().getEquipmentData().put("description", descriptionField.getText());
                viewModel.getState().getEquipmentData().put("price", priceField.getText());
                viewModel.getState().getEquipmentData().put("status", statusComboBox.getValue().replace(" ", "-").toLowerCase());
                viewModel.getState().getEquipmentData().put("type", getIdByName(viewModel.getState().getEquipmentTypes(), typeComboBox.getValue(), EquipmentType::getTypeName));
                viewModel.getState().getEquipmentData().put("location", getIdByName(viewModel.getState().getLocations(), locationComboBox.getValue(), Location::getLocationName));
                viewModel.getState().getEquipmentData().put("supplier", getIdByName(viewModel.getState().getSuppliers(), supplierComboBox.getValue(), Supplier::getName));
                viewModel.createEquipment();
            } else {
                viewModel.updateEquipment();
            }
            updateStage.close();
            viewModel.clearFilters();
        });

        addFormFields(formLayout, nameField, descriptionField, priceField, statusComboBox,
                typeComboBox, locationComboBox, supplierComboBox, submitButton);

        Scene scene = new Scene(formLayout, 400, 450);
        scene.getStylesheets().add(getClass().getResource("/org/example/schoolequipment/globals.css").toExternalForm());
        updateStage.setScene(scene);
        updateStage.show();
    }

    private void showEquipmentDetails(Equipment equipment) {
        Stage detailStage = new Stage();
        detailStage.setTitle("Equipment Details");

        GridPane detailLayout = new GridPane();
        detailLayout.setPadding(new Insets(20));
        detailLayout.setHgap(10);
        detailLayout.setVgap(10);
        detailLayout.getStyleClass().add("form-pane");

        addDetailField(detailLayout, 0, "Equipment Name", equipment.getEquipmentName());
        addDetailField(detailLayout, 1, "Description", equipment.getDescription());
        addDetailField(detailLayout, 2, "Status", formatStatus(equipment.getStatus()));
        addDetailField(detailLayout, 3, "Price", String.valueOf(equipment.getPrice()));
        addDetailField(detailLayout, 4, "Type", equipment.getType().getTypeName());
        addDetailField(detailLayout, 5, "Location", equipment.getLocation().getLocationName());
        addDetailField(detailLayout, 6, "Supplier", equipment.getSupplier().getName());

        Scene scene = new Scene(detailLayout, 400, 350);
        scene.getStylesheets().add(getClass().getResource("/org/example/schoolequipment/globals.css").toExternalForm());
        detailStage.setScene(scene);
        detailStage.show();
    }

    private void addDetailField(GridPane layout, int row, String labelText, String value) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-font-weight: bold;");
        Label valueLabel = new Label(value);
        layout.addRow(row, label, valueLabel);
    }

    private <T> ComboBox<String> createComboBox(javafx.collections.ObservableList<T> items,
                                                java.util.function.Function<T, String> nameExtractor) {
        return new ComboBox<>(items.stream()
                .map(nameExtractor)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private String formatStatus(String status) {
        return status.replace("-", " ")
                .substring(0, 1).toUpperCase() + status.replace("-", " ").substring(1);
    }

    private void updateViewModel(TextField nameField, TextField descriptionField, TextField priceField,
                                 ComboBox<String> statusComboBox, ComboBox<String> typeComboBox,
                                 ComboBox<String> locationComboBox, ComboBox<String> supplierComboBox,
                                 Equipment equipment) {
        viewModel.getState().getUpdateData().put("name", nameField.getText());
        viewModel.getState().getUpdateData().put("description", descriptionField.getText());
        viewModel.getState().getUpdateData().put("status", statusComboBox.getValue().replace(" ", "-").toLowerCase());
        viewModel.getState().getUpdateData().put("type", getIdByName(viewModel.getState().getEquipmentTypes(), typeComboBox.getValue(), EquipmentType::getTypeName));
        viewModel.getState().getUpdateData().put("location", getIdByName(viewModel.getState().getLocations(), locationComboBox.getValue(), Location::getLocationName));
        viewModel.getState().getUpdateData().put("supplier", getIdByName(viewModel.getState().getSuppliers(), supplierComboBox.getValue(), Supplier::getName));
        viewModel.getState().getUpdateData().put("price", priceField.getText());
        if (equipment != null) {
            viewModel.getState().getUpdateData().put("equipmentId", equipment.get_id());
        }
    }

    private <T> String getIdByName(javafx.collections.ObservableList<T> items, String name,
                                   java.util.function.Function<T, String> nameExtractor) {
        return items.stream()
                .filter(item -> nameExtractor.apply(item).equals(name))
                .findFirst()
                .map(item -> {
                    return switch (item) {
                        case Equipment equipment -> equipment.get_id();
                        case EquipmentType equipmentType -> equipmentType.get_id();
                        case Location location -> location.get_id();
                        case Supplier supplier -> supplier.get_id();
                        default -> "";
                    };
                })
                .orElse("");
    }

    private void addFormFields(GridPane layout, TextField nameField, TextField descriptionField,
                               TextField priceField, ComboBox<String> statusComboBox,
                               ComboBox<String> typeComboBox, ComboBox<String> locationComboBox,
                               ComboBox<String> supplierComboBox, Button submitButton) {
        layout.addRow(0, new Label("Equipment Name"), nameField);
        layout.addRow(1, new Label("Description"), descriptionField);
        layout.addRow(2, new Label("Status"), statusComboBox);
        layout.addRow(3, new Label("Type"), typeComboBox);
        layout.addRow(4, new Label("Price"), priceField);
        layout.addRow(5, new Label("Location"), locationComboBox);
        layout.addRow(6, new Label("Supplier"), supplierComboBox);
        layout.addRow(7, submitButton);

        Label formError = new Label();
        formError.getStyleClass().add("form-error");
        formError.setTextFill(javafx.scene.paint.Color.RED);
        formError.textProperty().bind(viewModel.getState().formErrorProperty());
        layout.addRow(8, formError);
    }

    public static void main(String[] args) {
        launch(args);
    }
}