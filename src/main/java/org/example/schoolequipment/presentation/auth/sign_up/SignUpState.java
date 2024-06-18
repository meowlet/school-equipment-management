package org.example.schoolequipment.presentation.auth.sign_up;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SignUpState {
    StringProperty userName = new SimpleStringProperty("");
    StringProperty password = new SimpleStringProperty("");
    StringProperty confirmPassword = new SimpleStringProperty("");
    StringProperty fullName = new SimpleStringProperty("");
    StringProperty email = new SimpleStringProperty("");

    StringProperty error = new SimpleStringProperty("");
}