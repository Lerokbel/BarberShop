package com.example;

import connectionModule.ConnectionModule;
import entities.Master;
import entities.Status;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserManagementController {

    boolean isUsers;

    @FXML
    public void initialize(){
        isUsers = true;

        columnFullName.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<User, String>("phone"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<User, String>("strstatus"));

        columnMasterFullName.setCellValueFactory(new PropertyValueFactory<Master, String>("fullName"));
        columnMasterExp.setCellValueFactory(new PropertyValueFactory<Master, String>("strexp"));
        columnMasterStatus.setCellValueFactory(new PropertyValueFactory<Master, String>("strstatus"));

        updatePage();
    }

    private void updatePage(){
        if(isUsers){

            usersTable.setVisible(true);
            mastersTable.setVisible(false);
            mastersBtn.setText("Сотрудники");

            ObservableList<User> users = FXCollections.observableArrayList();

            try {
                var list = ConnectionModule.getAllClients();
                for (var item: list) {
                    users.add(item);
                }
            } catch (Exception e) {
                AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
            }

            usersTable.setItems(users);
        }
        else {

            usersTable.setVisible(false);
            mastersTable.setVisible(true);
            mastersBtn.setText("Клиенты");

            ObservableList<Master> masters = FXCollections.observableArrayList();

            try {
                var list = ConnectionModule.getAllMasters();
                for (var item: list) {
                    masters.add(item);
                }
            } catch (Exception e) {
                AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
            }

            mastersTable.setItems(masters);
        }
    }

    @FXML
    private Button mastersBtn;

    @FXML
    private Button addBtn;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> columnFullName;

    @FXML
    private TableColumn<User, String> columnPhone;

    @FXML
    private TableColumn<User, String> columnStatus;

    @FXML
    private TableView<Master> mastersTable;
    @FXML
    private TableColumn<Master, String> columnMasterFullName;

    @FXML
    private TableColumn<Master, String> columnMasterExp;

    @FXML
    private TableColumn<Master, String> columnMasterStatus;

    @FXML
    void onBan(ActionEvent event) {
        if(isUsers){
            var user = usersTable.getSelectionModel().getSelectedItem();
            if(user != null){
                user.setStatus(Status.BANNED);
                try {
                    ConnectionModule.banClient(user.getId());
                } catch (Exception e) {
                    AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
                }
            }
        }
        else{
            var master = mastersTable.getSelectionModel().getSelectedItem();
            if(master != null){
                master.setStatus(Status.BANNED);
                try {
                    ConnectionModule.banMaster(master.getId());
                } catch (Exception e) {
                    AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
                }
            }
        }
    }

    @FXML
    void onChangeView(ActionEvent event) {
        isUsers = !isUsers;
        updatePage();
    }

    @FXML
    void onEdit(ActionEvent event) {
        if(isUsers){
            var user = usersTable.getSelectionModel().getSelectedItem();
            if(user == null)
                return;

            Client.connectedUser = user;
            Client.connectedMaster=null;

            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.profileView, Client.changingWindowUtility.profileW, Client.changingWindowUtility.profileH, "Редактирование профиля");
        }
        else {
            var master = mastersTable.getSelectionModel().getSelectedItem();
            if(master == null)
                return;

            Client.connectedUser = null;
            Client.connectedMaster=master;

            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.profileView, Client.changingWindowUtility.profileW, Client.changingWindowUtility.profileH, "Редактирование профиля");
        }
    }

    @FXML
    void onGoBack(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.typesView, Client.changingWindowUtility.typesW, Client.changingWindowUtility.typesH, "Типы проектов");
    }

    @FXML
    void onAdd(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.addUserView, Client.changingWindowUtility.addUserW, Client.changingWindowUtility.addUserH, "Добавление аккаунта");
    }

    @FXML
    void onUnban(ActionEvent event) {
        if(isUsers){
            var user = usersTable.getSelectionModel().getSelectedItem();
            if(user != null){
                user.setStatus(Status.NOT_BANNED);
                try {
                    ConnectionModule.unbanClient(user.getId());
                } catch (Exception e) {
                    AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
                }
            }
        }
        else{
            var master = mastersTable.getSelectionModel().getSelectedItem();
            if(master != null){
                master.setStatus(Status.NOT_BANNED);
                try {
                    ConnectionModule.unbanMaster(master.getId());
                } catch (Exception e) {
                    AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
                }
            }
        }
    }

}
