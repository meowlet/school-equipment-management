package org.example.schoolequipment.presentation.auth.sign_in;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SignInState {
    StringProperty identifier = new SimpleStringProperty("");
    StringProperty password = new SimpleStringProperty("");
    StringProperty error = new SimpleStringProperty("");
}