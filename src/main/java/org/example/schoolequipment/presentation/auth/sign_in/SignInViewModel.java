package org.example.schoolequipment.presentation.auth.sign_in;

import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.example.schoolequipment.api.API;
import org.example.schoolequipment.presentation.auth.home.HomeScreen;
import org.example.schoolequipment.presentation.auth.sign_up.SignUpScreen;
import org.example.schoolequipment.util.Constant;
import org.example.schoolequipment.util.HttpRequestHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SignInViewModel {

    SignInState state;
    Stage stage;

    SignInViewModel(Stage stage) {
        this.state = new SignInState();
        this.stage = stage;
    }

    public Boolean fieldsValid() {
        return !state.identifier.get().isEmpty() && !state.password.get().isEmpty();
    }

    public void clearError() {
        state.error.set("");
    }

    public void navigateToSignUp() {
        new SignUpScreen().start(stage);
    }

    public void navigateToHome() {
        new HomeScreen().start(stage);
    }

    public void signIn() {
        if (!fieldsValid()) {
            state.error.set("All fields are required");
            return;
        }
        state.error.set("");

        Task<HttpRequestHelper.HttpResponse> signInTask = new Task<>() {
            @Override
            protected HttpRequestHelper.HttpResponse call() {
                return new API().signIn(state.identifier.get(), state.password.get());
            }
        };

        signInTask.setOnSucceeded(workerStateEvent -> {
            HttpRequestHelper.HttpResponse signInResponse = signInTask.getValue();
            if (signInResponse.getStatusCode() == 200) {
                state.error.set("Sign in successful");
                String token = signInResponse.getBody();
                try {
                    Files.write(Paths.get(Constant.CREDENTIALS_FILE), token.getBytes());
                    navigateToHome();
                } catch (IOException e) {
                    state.error.set("Error saving token");
                }
            } else {
                state.error.set(signInResponse.getBody());
            }
        });

        signInTask.setOnFailed(workerStateEvent -> state.error.set("Sign in failed"));

        new Thread(signInTask).start();
    }
}