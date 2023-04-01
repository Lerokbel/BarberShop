module com.example.servergui {
    requires javafx.controls;
    requires javafx.fxml;
    requires TransportLayer;

    opens ServerGui to javafx.fxml;
    exports ServerGui;
}