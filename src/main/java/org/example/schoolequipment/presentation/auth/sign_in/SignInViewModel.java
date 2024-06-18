package org.example.schoolequipment.presentation.auth.sign_in;

import org.example.schoolequipment.api.API;
import org.example.schoolequipment.util.HttpRequestHelper;

public class SignInViewModel {

    SignInState state;

    SignInViewModel() {
        this.state = new SignInState();
    }

    public Boolean fieldsValid() {
        return !state.identifier.get().isEmpty() && !state.password.get().isEmpty();
    }

    public void clearError() {
        state.error.set("");
    }

    public void signIn() {
        if (!fieldsValid()) {
            state.error.set("All fields are required");
            return;
        }
        state.error.set("");

        HttpRequestHelper.HttpResponse signInResponse = new API().signIn(state.identifier.get(), state.password.get());
        if (signInResponse.getStatusCode()== 200) {
            state.error.set("Sign in successful");
        } else {
            state.error.set(signInResponse.getBody());
        }
    }
}