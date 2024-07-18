package org.example.schoolequipment;

import javafx.stage.Stage;
import org.example.schoolequipment.presentation.auth.home.HomeScreen;
import org.example.schoolequipment.presentation.auth.sign_in.SignInScreen;
import org.example.schoolequipment.presentation.auth.sign_up.SignUpScreen;
import org.example.schoolequipment.presentation.auth.user_management.UserManagementScreen;
import org.example.schoolequipment.util.Constant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        if (checkTokenExists()) {
            HomeScreen homeScreen = new HomeScreen();
            homeScreen.start(stage);

//            UserManagementScreen userManagementScreen = new UserManagementScreen();
//            userManagementScreen.start(stage);
        } else {
            SignInScreen signInScreen = new SignInScreen();
            signInScreen.start(stage);
        }
//        SignUpScreen signUpScreen = new SignUpScreen();
//        signUpScreen.start(stage);
//        HomeScreen homeScreen = new HomeScreen();
//        homeScreen.start(stage);
    }

    private boolean checkTokenExists() {
        try {
            String token = new String(Files.readAllBytes(Paths.get(Constant.CREDENTIALS_FILE)));
            return !token.isEmpty();
        } catch (IOException ignored) {
            // File does not exist or is not readable, ignore
            return false;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}