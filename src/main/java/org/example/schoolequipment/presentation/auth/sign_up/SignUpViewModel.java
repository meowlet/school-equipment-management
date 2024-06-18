package org.example.schoolequipment.presentation.auth.sign_up;

import javafx.stage.Stage;
import org.example.schoolequipment.api.API;
import org.example.schoolequipment.presentation.auth.sign_in.SignInScreen;
import org.example.schoolequipment.util.HttpRequestHelper;

public class SignUpViewModel {

    SignUpState state;
    Stage stage;

    SignUpViewModel(Stage stage) {
        this.state = new SignUpState();
        this.stage = stage;
    }

    public Boolean fieldsValid() {
        return !state.userName.get().isEmpty() && !state.password.get().isEmpty() && !state.confirmPassword.get().isEmpty() && !state.fullName.get().isEmpty() && !state.email.get().isEmpty() ;
    }

    public Boolean emailValid() {
        return state.email.get().contains("@");
    }

    public Boolean passwordMatch() {
        return state.password.get().equals(state.confirmPassword.get());
    }

    public Boolean passwordLength() {
        return state.password.get().length() >= 8;
    }

    public void realTimePasswordMatch() {
        if (state.password.get().isEmpty() || state.confirmPassword.get().isEmpty()) {
            state.error.set("");
            return;
        }
        if (!state.password.get().equals(state.confirmPassword.get())) {
            state.error.set("Passwords do not match");
        } else {
            state.error.set("");
        }
    }

    public void clearError() {
        state.error.set("");
    }

    public void signUp() {
        if (!fieldsValid()) {
            state.error.set("All fields are required");
            return;
        }
        if (!emailValid()) {
            state.error.set("Invalid email");
            return;
        }
        if (!passwordMatch()) {
            state.error.set("Passwords do not match");
            return;
        }
        if (!passwordLength()) {
            state.error.set("Password must be at least 8 characters long");
            return;
        }
        state.error.set("");

        HttpRequestHelper.HttpResponse signUpResponse = new API().signUp(state.userName.get(), state.password.get(), state.fullName.get(), state.email.get());
        if (signUpResponse.getStatusCode()== 200) {
            SignInScreen signInScreen = new SignInScreen();
            signInScreen.start(stage);
        } else {
            state.error.set(signUpResponse.getBody());
        }
    }
}
