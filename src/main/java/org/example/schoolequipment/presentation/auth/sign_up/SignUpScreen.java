package org.example.schoolequipment.presentation.auth.sign_up;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SignUpScreen extends Application {

    SignUpViewModel viewModel;

    @Override
    public void start(Stage primaryStage) {
        viewModel = new SignUpViewModel(primaryStage);
        Label displayText = new Label("Sign Up");
        Label usernameText = new Label("Username");
        Label passwordText = new Label("Password");
        Label fullNameText = new Label("Full Name");
        Label emailText = new Label("Email");
        Label confirmPasswordText = new Label("Confirm Password");
        Button signUpButton = new Button("Sign Up");
        Label signUpError = new Label();

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField fullNameField = new TextField();
        TextField emailField = new TextField();
        PasswordField confirmPasswordField = new PasswordField();

        ChangeListener<String> fieldChangeListener = (observable, oldValue, newValue) -> {
            viewModel.clearError();
        };

        ChangeListener<String> passwordChangeListener = (observable, oldValue, newValue) -> {
            viewModel.realTimePasswordMatch();
        };

        usernameField.textProperty().bindBidirectional(viewModel.state.userName);
        passwordField.textProperty().bindBidirectional(viewModel.state.password);
        fullNameField.textProperty().bindBidirectional(viewModel.state.fullName);
        emailField.textProperty().bindBidirectional(viewModel.state.email);
        confirmPasswordField.textProperty().bindBidirectional(viewModel.state.confirmPassword);

        usernameField.textProperty().addListener(fieldChangeListener);
        passwordField.textProperty().addListener(passwordChangeListener);
        fullNameField.textProperty().addListener(fieldChangeListener);
        emailField.textProperty().addListener(fieldChangeListener);
        confirmPasswordField.textProperty().addListener(passwordChangeListener);

        signUpError.textProperty().bind(viewModel.state.error);

        signUpButton.setOnAction(e -> {
            viewModel.signUp();
        });

        displayText.setFont(new Font(20));

        GridPane layout = new GridPane();

        layout.setVgap(5);
        layout.setHgap(10);

        layout.add(displayText, 0, 0, 2, 1);

        layout.add(usernameText, 0, 1);
        layout.add(usernameField, 1, 1);
        layout.add(emailText, 0, 2);
        layout.add(emailField, 1, 2);
        layout.add(fullNameText, 0, 3);
        layout.add(fullNameField, 1, 3);
        layout.add(passwordText, 0, 4);
        layout.add(passwordField, 1, 4);
        layout.add(confirmPasswordText, 0, 5);
        layout.add(confirmPasswordField, 1, 5);
        layout.add(signUpButton, 0, 6, 2, 1);
        layout.add(signUpError, 0, 7, 2, 1);

        layout.setAlignment(Pos.CENTER);
        GridPane.setHalignment(displayText, HPos.CENTER);
        GridPane.setHalignment(usernameText, HPos.RIGHT);
        GridPane.setHalignment(passwordText, HPos.RIGHT);
        GridPane.setHalignment(fullNameText, HPos.RIGHT);
        GridPane.setHalignment(emailText, HPos.RIGHT);
        GridPane.setHalignment(confirmPasswordText, HPos.RIGHT);
        GridPane.setHalignment(signUpButton, HPos.CENTER);
        GridPane.setHalignment(signUpError, HPos.CENTER);

        displayText.setPadding(new Insets(10));
        signUpError.setTextFill(Color.web("#ba1a1a"));

        Scene scene = new Scene(layout, 300, 300);
        scene.getStylesheets().add(getClass().getResource("/org/example/schoolequipment/globals.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}