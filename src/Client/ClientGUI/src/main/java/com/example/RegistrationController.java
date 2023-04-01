package com.example;

import Commands.Response;
import connectionModule.ConnectionModule;
import entities.Master;
import entities.Status;
import entities.User;
import enums.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController {

    boolean isClient = true;
    @FXML
    private TextField fullnameInput;

    @FXML
    private TextField lognInput;

    @FXML
    private TextField telephoneInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private PasswordField repeatPasswordInput;

    @FXML
    private Button btnChangeType;

    @FXML
    void onChangeType(ActionEvent event) {
        isClient = !isClient;
        btnChangeType.setText(isClient? "Я сотрудник": "Я клиент");
        telephoneInput.setPromptText(isClient? "Телефон": "Стаж работы в годах");
    }
    @FXML
    void onCancel(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.authorizationView, Client.changingWindowUtility.authorizationW, Client.changingWindowUtility.authorizationH, "Авторизация");
    }

    @FXML
    void onEnter(ActionEvent event) {
        var login = lognInput.getText();
        var password = passwordInput.getText();
        var repeatPassword = repeatPasswordInput.getText();
        var fullname = fullnameInput.getText();
        var telephone = telephoneInput.getText();

        if(login.isEmpty() ||password.isEmpty() ||repeatPassword.isEmpty() ||fullname.isEmpty() ||telephone.isEmpty()){
            AlertManager.showWarningAlert("Поля должны быть заполнены", "Заполните все поля!");
            return;
        }
        if(!password.equals(repeatPassword)){
            AlertManager.showWarningAlert("Ошибка", "Пароли должны совпадать!");
            return;
        }

        int exp = 0;

        if(!isClient){
            try
            {
                exp = Integer.parseInt(telephone);
                if(exp < 0 || exp > 70)
                    throw new NumberFormatException();
            }
            catch (NumberFormatException e){
                AlertManager.showWarningAlert("Ошибка!", "Введите целое число в графу стажа от 0 до 70");
                return;
            }
        }

        try {
            if(isClient){
                if( ConnectionModule.registration(new User(0, login, password, fullname, telephone, Status.NOT_BANNED)) == Response.SUCCESSFULLY){
                    Client.userType = UserType.USER;
                    Client.connectedUser = ConnectionModule.getCurrentProfileClient();
                    Client.changingWindowUtility.showWindow(Client.changingWindowUtility.typesView, Client.changingWindowUtility.typesW, Client.changingWindowUtility.typesH, "Предоставляемые услуги");
                }
                else{
                    AlertManager.showErrorAlert("Ошибка", "Пользователь с таким логином уже существует");
                }
            }
            else{
                if( ConnectionModule.registrationMaster(new Master(0, login, password, exp, fullname, Status.NOT_BANNED)) == Response.SUCCESSFULLY){
                    Client.userType = UserType.MASTER;
                    Client.connectedMaster = ConnectionModule.getCurrentProfileMaster();
                    Client.changingWindowUtility.showWindow(Client.changingWindowUtility.typesView, Client.changingWindowUtility.typesW, Client.changingWindowUtility.typesH, "Предоставляемые услуги");
                }
                else{
                    AlertManager.showErrorAlert("Ошибка", "Пользователь с таким логином уже существует");
                }
            }

        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка соединения", "");
        }
    }

}
