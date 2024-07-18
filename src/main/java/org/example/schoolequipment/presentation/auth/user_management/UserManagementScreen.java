package org.example.schoolequipment.presentation.auth.user_management;

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

public class UserManagementScreen extends Application {
    private UserManagementViewModel viewModel;
    private TableView<User> tableView = new TableView<>();
    private ComboBox<Role> roleFilter;
    private Stage primaryStage;
    private VBox sidebar;
    private BooleanProperty isSidebarVisible = new SimpleBooleanProperty(false);
    private StackPane root;
    private BorderPane mainContent;
    StringProperty query = new SimpleStringProperty();

    private Pagination pagination;
    private TextField itemsPerPageField;
    private int currentPage = 0;
    private int itemsPerPage = 5;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("User Management");
        viewModel = new UserManagementViewModel(primaryStage);

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

        viewModel.fetchUsers();
        viewModel.fetchRoles();
    }

    private HBox createNavBar() {
        Button showSidebarButton = new Button("☰");
        showSidebarButton.getStyleClass().add("nav-button");
        showSidebarButton.setOnAction(e -> toggleSidebar());

        Label appNameLabel = new Label("User Management");
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
            if (page.equals("Users")) {
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
            viewModel.fetchUsers();
        });

        itemsPerPageField = new TextField(String.valueOf(itemsPerPage));
        itemsPerPageField.setId("itemsPerPageField");
        itemsPerPageField.setOnAction(e -> updateItemsPerPage());

        Label itemsPerPageLabel = new Label("Items per page:");

        HBox paginationBox = new HBox(10, itemsPerPageLabel, itemsPerPageField, pagination);
        paginationBox.setAlignment(Pos.CENTER);

        viewModel.getState().totalItemsProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Total items changed: " + newVal);
            pagination.setPageCount(calculatePageCount());
            System.out.println(calculatePageCount());
        });

        return paginationBox;
    }

    private void updateItemsPerPage() {
        try {
            int newItemsPerPage = Integer.parseInt(itemsPerPageField.getText());
            if (newItemsPerPage > 0) {
                itemsPerPage = newItemsPerPage;
                viewModel.getState().getFilters().put("limit", String.valueOf(itemsPerPage));
                currentPage = 0; // Reset to first page when changing items per page
                pagination.setCurrentPageIndex(currentPage);
                pagination.setPageCount(calculatePageCount());
                viewModel.fetchUsers();
            }
        } catch (NumberFormatException ex) {
            // Handle invalid input
            itemsPerPageField.setText(String.valueOf(itemsPerPage));
        }
    }

    private int calculatePageCount() {
        System.out.println("Total items: " + viewModel.getState().getTotalItems());
        int totalItems = viewModel.getState().getTotalItems();
        return (int) Math.ceil((double) totalItems / itemsPerPage);
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



        searchBox.setPromptText("Search users...");
        searchBox.setPrefWidth(300);
        searchBox.textProperty().bindBidirectional(query);

        query.addListener((obs, oldVal, newVal) -> {
            viewModel.getState().getFilters().put("query", newVal);
            viewModel.fetchUsers();
        });

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            viewModel.getState().getFilters().put("query", searchBox.getText());
            viewModel.fetchUsers();
        });

        Button createButton = new Button("Create User");
        createButton.setOnAction(e -> showUserForm(null));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBar = new HBox(10, searchBox, searchButton, spacer, createButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        return searchBar;
    }

    private HBox createFilterBar() {
        roleFilter = new ComboBox<>(viewModel.getRoles());
        roleFilter.setPromptText("Filter by Role");
        roleFilter.setOnAction(e -> {
            Role selectedRole = roleFilter.getValue();
            if (selectedRole != null && !"all".equals(selectedRole.get_id())) {
                viewModel.getState().getFilters().put("roleId", selectedRole.get_id());
            } else {
                viewModel.getState().getFilters().remove("roleId");
            }
            viewModel.fetchUsers();
        });

        Button clearFilterButton = new Button("Clear Filter");
        clearFilterButton.setOnAction(e -> {
            roleFilter.setValue(viewModel.getRoles().getFirst());
            query.set("");
            itemsPerPage = 5;
            itemsPerPageField.setText(String.valueOf(itemsPerPage));
            viewModel.clearFilters();
        });

        Button createRoleButton = new Button("Create New Role");
        createRoleButton.setOnAction(e -> showRoleForm());

        HBox filterBar = new HBox(20, roleFilter, clearFilterButton, createRoleButton);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        return filterBar;
    }

    private TableView<User> createTableView() {
        tableView = new TableView<>(viewModel.getUsers());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> {
            Role role = cellData.getValue().getRoleId();
            return new javafx.beans.property.SimpleStringProperty(role != null ? role.getName() : "N/A");
        });

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.getStyleClass().add("delete-button");
                editBtn.setOnAction(event -> showUserForm(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> deleteUser(getTableView().getItems().get(getIndex())));
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

        tableView.getColumns().addAll(nameCol, emailCol, roleCol, actionsCol);

        return tableView;
    }

    private void showUserForm(User user) {
        Stage formStage = new Stage();
        formStage.setTitle(user == null ? "Create User" : "Edit User");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<Role> roleComboBox = new ComboBox<>(viewModel.getRoles());

        if (user != null) {
            nameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            roleComboBox.setValue(user.getRoleId());
        }

        form.addRow(0, new Label("Full Name:"), nameField);
        form.addRow(1, new Label("Email:"), emailField);
        form.addRow(2, new Label("Password:"), passwordField);
        form.addRow(3, new Label("Role:"), roleComboBox);

        Button submitButton = new Button(user == null ? "Create" : "Update");
        submitButton.setOnAction(e -> {
            if (user == null) {
                viewModel.createUser(nameField.getText(), emailField.getText(), passwordField.getText(), roleComboBox.getValue());
            } else {
                viewModel.updateUser(user.get_id(), nameField.getText(), emailField.getText(), passwordField.getText(), roleComboBox.getValue());
            }
            formStage.close();
        });

        form.addRow(4, submitButton);

        Scene formScene = new Scene(form);
        formStage.setScene(formScene);
        formStage.show();
    }

    private void showRoleForm() {
        Stage formStage = new Stage();
        formStage.setTitle("Create New Role");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);

        form.addRow(0, new Label("Role Name:"), nameField);
        form.addRow(1, new Label("Description:"), descriptionArea);

        VBox permissionsBox = new VBox(10);
        permissionsBox.getChildren().add(new Label("Permissions:"));

        for (Resource resource : Resource.values()) {
            HBox resourceBox = new HBox(10);
            resourceBox.getChildren().add(new Label(resource.name()));

            CheckComboBox<Action> actionComboBox = new CheckComboBox<>(FXCollections.observableArrayList(Action.values()));
            resourceBox.getChildren().add(actionComboBox);

            permissionsBox.getChildren().add(resourceBox);
        }

        form.addRow(2, permissionsBox);

        Button submitButton = new Button("Create Role");
        submitButton.setOnAction(e -> {
            List<Permission> permissions = new ArrayList<>();
            for (javafx.scene.Node node : permissionsBox.getChildren()) {
                if (node instanceof HBox) {
                    HBox resourceBox = (HBox) node;
                    Label resourceLabel = (Label) resourceBox.getChildren().get(0);
                    CheckComboBox<Action> actionComboBox = (CheckComboBox<Action>) resourceBox.getChildren().get(1);

                    Resource resource = Resource.valueOf(resourceLabel.getText().toUpperCase());
                    List<Action> actions = actionComboBox.getCheckModel().getCheckedItems();

                    if (!actions.isEmpty()) {
                        permissions.add(new Permission(resource, actions));
                        System.out.println(permissions);
                    }
                }
            }

            viewModel.createRole(nameField.getText(), descriptionArea.getText(), permissions);
            formStage.close();
        });

        form.addRow(3, submitButton);

        Scene formScene = new Scene(form);
        formStage.setScene(formScene);
        formStage.show();
    }

    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                viewModel.deleteUser(user.get_id());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}