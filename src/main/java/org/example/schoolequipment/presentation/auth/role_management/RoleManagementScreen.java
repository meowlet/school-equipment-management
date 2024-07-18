package org.example.schoolequipment.presentation.auth.role_management;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.example.schoolequipment.model.*;
import org.example.schoolequipment.presentation.auth.home.HomeScreen;

import java.util.ArrayList;
import java.util.List;

public class RoleManagementScreen extends Application {
    private RoleManagementViewModel viewModel;
    private TableView<Role> tableView = new TableView<>();
    private Stage primaryStage;
    private VBox sidebar;
    private BooleanProperty isSidebarVisible = new SimpleBooleanProperty(false);
    private StackPane root;
    private BorderPane mainContent;
    StringProperty query = new SimpleStringProperty();
    private CheckBox dangerousRolesCheckBox;

    private Pagination pagination;
    private TextField itemsPerPageField;
    private int currentPage = 0;
    private int itemsPerPage = 5;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Role Management");
        viewModel = new RoleManagementViewModel(primaryStage);

        root = new StackPane();
        mainContent = new BorderPane();

        mainContent.setTop(createNavBar());
        mainContent.setCenter(createMainContent());

        sidebar = createSidebar();
        sidebar.setVisible(false);
        sidebar.setManaged(false);

        root.getChildren().addAll(mainContent, sidebar);
        StackPane.setAlignment(sidebar, Pos.CENTER_LEFT);

        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/org/example/schoolequipment/globals.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        viewModel.fetchRoles();
    }

    private HBox createNavBar() {
        Button showSidebarButton = new Button("☰");
        showSidebarButton.getStyleClass().add("nav-button");
        showSidebarButton.setOnAction(e -> toggleSidebar());

        Label appNameLabel = new Label("Role Management");
        appNameLabel.getStyleClass().add("app-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("Back to Home");
        backButton.setOnAction(e -> navigateToHome());

        HBox navBar = new HBox(10, showSidebarButton, appNameLabel, spacer, backButton);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(10));
        navBar.getStyleClass().add("nav-bar");

        return navBar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(200);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(10));

        VBox sidebarContent = new VBox(10);
        sidebarContent.getStyleClass().add("sidebar-content");

        String[] pages = {"Home", "Equipment", "Loan History", "Maintenance", "Users", "Roles"};
        for (String page : pages) {
            Button pageButton = new Button(page);
            if (page.equals("Roles")) {
                pageButton.getStyleClass().add("sidebar-button-active");
            } else {
                pageButton.getStyleClass().add("sidebar-button");
            }
            pageButton.setMaxWidth(Double.MAX_VALUE);
            pageButton.setOnAction(e -> handlePageNavigation(page));
            sidebarContent.getChildren().add(pageButton);
        }

        sidebar.getChildren().addAll(sidebarContent);

        return sidebar;
    }

    private void toggleSidebar() {
        isSidebarVisible.set(!isSidebarVisible.get());
        sidebar.setVisible(isSidebarVisible.get());
        sidebar.setManaged(isSidebarVisible.get());
    }

    private void handlePageNavigation(String page) {
        if (page.equals("Home")) {
            navigateToHome();
        } else {
            System.out.println("Navigating to " + page);
            // Implement navigation to other pages as needed
        }
    }

    private void navigateToHome() {
        new HomeScreen().start(primaryStage);
    }

    private BorderPane createMainContent() {
        BorderPane mainContent = new BorderPane();

        VBox contentArea = new VBox(10);
        contentArea.setPadding(new Insets(20));

        contentArea.getChildren().addAll(
                createTopBar(),
                createTableView(),
                createPagination()
        );

        mainContent.setCenter(contentArea);
        isSidebarVisible.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                mainContent.setLeft(sidebar);
            } else {
                mainContent.setLeft(null);
            }
        });

        return mainContent;
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
            viewModel.fetchRoles();
        });

        itemsPerPageField = new TextField(String.valueOf(itemsPerPage));
        itemsPerPageField.setOnAction(e -> updateItemsPerPage());

        Label itemsPerPageLabel = new Label("Items per page:");

        HBox paginationBox = new HBox(10, itemsPerPageLabel, itemsPerPageField, pagination);
        paginationBox.setAlignment(Pos.CENTER);

        viewModel.getState().totalItemsProperty().addListener((obs, oldVal, newVal) -> {
            pagination.setPageCount(calculatePageCount());
        });

        return paginationBox;
    }

    private void updateItemsPerPage() {
        try {
            int newItemsPerPage = Integer.parseInt(itemsPerPageField.getText());
            if (newItemsPerPage > 0) {
                itemsPerPage = newItemsPerPage;
                viewModel.getState().getFilters().put("limit", String.valueOf(itemsPerPage));
                currentPage = 0;
                pagination.setCurrentPageIndex(currentPage);
                pagination.setPageCount(calculatePageCount());
                viewModel.fetchRoles();
            }
        } catch (NumberFormatException ex) {
            itemsPerPageField.setText(String.valueOf(itemsPerPage));
        }
    }

    private int calculatePageCount() {
        int totalItems = viewModel.getState().getTotalItems();
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(10);
        topBar.getChildren().addAll(
                createSearchBar()
        );
        topBar.getStyleClass().add("top-bar");
        return topBar;
    }

    private HBox createSearchBar() {
        TextField searchBox = new TextField();
        searchBox.setPromptText("Search roles...");
        searchBox.setPrefWidth(300);
        searchBox.textProperty().bindBidirectional(query);

        query.addListener((obs, oldVal, newVal) -> {
            viewModel.getState().getFilters().put("query", newVal);
            viewModel.fetchRoles();
        });

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            viewModel.getState().getFilters().put("query", searchBox.getText());
            viewModel.fetchRoles();
        });

        dangerousRolesCheckBox = new CheckBox("Dangerous Roles");
        dangerousRolesCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                viewModel.getState().getFilters().put("isDangerous", "true");
            } else {
                viewModel.getState().getFilters().remove("isDangerous");
            }
            viewModel.fetchRoles();
        });

        Button createButton = new Button("Create Role");
        createButton.setOnAction(e -> showRoleForm(null));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBar = new HBox(10, searchBox, searchButton, dangerousRolesCheckBox, spacer, createButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        return searchBar;
    }

    private TableView<Role> createTableView() {
        tableView = new TableView<>(viewModel.getRoles());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Role, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Role, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<Role, String> permissionsCol = new TableColumn<>("Permissions");
        permissionsCol.setCellValueFactory(cellData -> {
            List<Permission> permissions = cellData.getValue().getPermissions();

            var totalPermissions = 0;
            for (Permission permission : permissions) {
                totalPermissions += permission.getActions().size();
            }

            return new SimpleStringProperty(totalPermissions + " permissions");
        });

        TableColumn<Role, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.getStyleClass().add("delete-button");
                editBtn.setOnAction(event -> showRoleForm(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> deleteRole(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        tableView.getColumns().addAll(nameCol, descriptionCol, permissionsCol, actionsCol);

        return tableView;
    }

    private void showRoleForm(Role role) {
        Stage formStage = new Stage();
        formStage.setTitle(role == null ? "Create Role" : "Edit Role");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);

        if (role != null) {
            nameField.setText(role.getName());
            descriptionArea.setText(role.getDescription());
        }

        form.addRow(0, new Label("Role Name:"), nameField);
        form.addRow(1, new Label("Description:"), descriptionArea);

        VBox permissionsBox = new VBox(10);
        permissionsBox.getChildren().add(new Label("Permissions:"));

        for (Resource resource : Resource.values()) {
            HBox resourceBox = new HBox(10);
            resourceBox.getChildren().add(new Label(resource.name()));

            CheckComboBox<Action> actionComboBox = new CheckComboBox<>(FXCollections.observableArrayList(Action.values()));
            if (role != null) {
                for (Permission permission : role.getPermissions()) {
                    if (permission.getResource() == resource) {
                        actionComboBox.getCheckModel().checkAll();
//                        actionComboBox.getCheckModel().checkAll(permission.getActions());
                    }
                }
            }
            resourceBox.getChildren().add(actionComboBox);

            permissionsBox.getChildren().add(resourceBox);
        }

        form.addRow(2, permissionsBox);

        Button submitButton = new Button(role == null ? "Create" : "Update");
        submitButton.setOnAction(e -> {
            List<Permission> permissions = new ArrayList<>();
            for (javafx.scene.Node node : permissionsBox.getChildren()) {
                if (node instanceof HBox) {
                    HBox resourceBox = (HBox) node;
                    Label resourceLabel = (Label) resourceBox.getChildren().get(0);
                    CheckComboBox<Action> actionComboBox = (CheckComboBox<Action>) resourceBox.getChildren().get(1);

                    Resource resource = Resource.valueOf(resourceLabel.getText());
                    List<Action> actions = actionComboBox.getCheckModel().getCheckedItems();

                    if (!actions.isEmpty()) {
                        permissions.add(new Permission(resource, actions));
                    }
                }
            }

            if (role == null) {
                viewModel.createRole(nameField.getText(), descriptionArea.getText(), permissions);
            } else {
                System.out.println(permissions);
                viewModel.updateRole(role.get_id(), nameField.getText(), descriptionArea.getText(), permissions);
            }
            formStage.close();
        });

        form.addRow(3, submitButton);

        Scene formScene = new Scene(form);
        formStage.setScene(formScene);
        formStage.show();
    }

    private void deleteRole(Role role) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Role");
        alert.setHeaderText("Are you sure you want to delete this role?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                viewModel.deleteRole(role.get_id());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}