package com.example;

import javafx.scene.control.Alert;

public class AlertManager {

    public static void showErrorAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(rs -> {
        });
    }

    public static void showWarningAlert(String header, String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(rs -> {
        });
    }

    public static void showInformationAlert(String header, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(rs -> {
        });
    }

}
