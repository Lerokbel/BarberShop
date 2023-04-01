package com.example;

import connectionModule.ConnectionModule;
import enums.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthorizationController {

    @FXML
    private TextField loginInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    void onEnter(ActionEvent event) {
        var login = loginInput.getText();
        var password = passwordInput.getText();

        try {
            Client.userType = ConnectionModule.singUp(login, password);
            if(Client.userType == UserType.UNDEFINED){
                AlertManager.showErrorAlert("Пользователь не найден!", "");
            }
            else{
                if(Client.userType == UserType.USER) {
                    Client.connectedUser = ConnectionModule.getCurrentProfileClient();
                }
                if(Client.userType == UserType.MASTER) {
                    Client.connectedMaster = ConnectionModule.getCurrentProfileMaster();
                }
                Client.changingWindowUtility.showWindow(Client.changingWindowUtility.typesView, Client.changingWindowUtility.typesW, Client.changingWindowUtility.typesH, "Предоставляемые услуги");
            }
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка соединения", "");
        }
    }

    @FXML
    void onRegistration(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.registrationView, Client.changingWindowUtility.registrationW, Client.changingWindowUtility.registrationH, "Регистрация");
    }

}
