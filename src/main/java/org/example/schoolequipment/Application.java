package org.example.schoolequipment;

import javafx.stage.Stage;
import org.example.schoolequipment.presentation.auth.home.HomeScreen;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
//        SignUpScreen signUpScreen = new SignUpScreen();
//        signUpScreen.start(stage);
        HomeScreen homeScreen = new HomeScreen();
        homeScreen.start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}