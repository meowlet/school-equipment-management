module org.example.schoolequipment {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    opens org.example.schoolequipment to javafx.fxml;
    exports org.example.schoolequipment;
}