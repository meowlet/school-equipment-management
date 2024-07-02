package org.example.schoolequipment.presentation.auth.sign_in;


import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SignInScreen extends Application {

    SignInViewModel viewModel;

    @Override
    public void start(Stage primaryStage) {
        viewModel = new SignInViewModel(primaryStage);
        Label displayText = new Label("Sign In");
        Label usernameText = new Label("Username");
        Label passwordText = new Label("Password");
        Button signInButton = new Button("Sign In");
        Label signInError = new Label();

        Hyperlink signUpLink = new Hyperlink();
        signUpLink.setText("Don't have an account? Sign up now!");

        signUpLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                viewModel.navigateToSignUp();
            }
        });

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        ChangeListener<String> fieldChangeListener = (observable, oldValue, newValue) -> {
            viewModel.clearError();
        };

        usernameField.textProperty().bindBidirectional(viewModel.state.identifier);
        passwordField.textProperty().bindBidirectional(viewModel.state.password);

        usernameField.textProperty().addListener(fieldChangeListener);
        passwordField.textProperty().addListener(fieldChangeListener);

        signInError.textProperty().bind(viewModel.state.error);

        signInButton.setOnAction(e -> {
            viewModel.signIn();
        });

        displayText.setFont(new Font(20));

        GridPane layout = new GridPane();

        layout.setVgap(5);
        layout.setHgap(10);

        layout.add(displayText, 0, 0, 2, 1);

        layout.add(usernameText, 0, 1);
        layout.add(usernameField, 1, 1);
        layout.add(passwordText, 0, 2);
        layout.add(passwordField, 1, 2);
        layout.add(signInButton, 0, 3, 2, 1);
        layout.add(signInError, 0, 4, 2, 1);
        layout.add(signUpLink, 0, 5, 2, 1);

        layout.setAlignment(Pos.CENTER);
        GridPane.setHalignment(displayText, HPos.CENTER);
        GridPane.setHalignment(usernameText, HPos.RIGHT);
        GridPane.setHalignment(passwordText, HPos.RIGHT);
        GridPane.setHalignment(signInButton, HPos.CENTER);
        GridPane.setHalignment(signInError, HPos.CENTER);

        displayText.setPadding(new Insets(10));
        signInError.setTextFill(Color.web("#ba1a1a"));

        Scene scene = new Scene(layout, 300, 300);
        scene.getStylesheets().add(getClass().getResource("/org/example/schoolequipment/globals.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

