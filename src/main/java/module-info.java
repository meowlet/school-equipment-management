module org.example.schoolequipment {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.net.http;
    requires com.google.gson;

    opens org.example.schoolequipment to javafx.fxml;
    exports org.example.schoolequipment;
    exports org.example.schoolequipment.presentation.auth.sign_in;
    exports org.example.schoolequipment.presentation.auth.sign_up;
    opens org.example.schoolequipment.presentation.auth.sign_up to javafx.fxml;
}