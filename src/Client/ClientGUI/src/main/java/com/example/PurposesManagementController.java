package com.example;

import connectionModule.ConnectionModule;
import entities.Purpose;
import enums.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class PurposesManagementController {

    private UserType userType;

    @FXML
    public void initialize(){
        userType = Client.userType;

        if(userType != UserType.ADMIN){
            usersButton.setText("Профиль");
            btnAdd.setVisible(false);
            btnEdit.setVisible(false);
            btnDel.setVisible(false);
        }
        else
            historyButton.setText("История");

        typeColumn.setCellValueFactory(new PropertyValueFactory<Purpose, String>("name"));
        costColumn.setCellValueFactory(new PropertyValueFactory<Purpose, Double>("cost"));

        updatePage();
    }

    private void updatePage(){
        ObservableList<Purpose> listEntities = FXCollections.observableArrayList();
        try {
            var list = ConnectionModule.getAllPurposes();// извлечение список объектов Purpose из БД
            for (var item: list) {
                listEntities.add(item);//добавление объектов Purpose в listEntities
            }
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
        }
        typesTable.setItems(listEntities); //обновление таблицы пользовательского интерфейса с последними данными из базы данных
    }
    @FXML
    private TableView<Purpose> typesTable;
    @FXML
    private TableColumn<Purpose, String> typeColumn;
    @FXML
    private TableColumn<Purpose, Double> costColumn;
    @FXML
    private Button historyButton;
    @FXML
    private Button usersButton;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDel;

    @FXML
    void onAbout(ActionEvent event) {
        String textInfo = "";
        switch (userType){
            case ADMIN -> {
                textInfo = "Вы администратор. Вы можете управлять типами услуг, заниматься управлением пользователей (блокировка и разблокировка).";
            }
            case USER -> {
                textInfo = "Вы пользователь. Вы можете просматривать типы проектов, заявки, составлять новые заявки на услуги.";
            }
            case MASTER -> {
                textInfo = "Вы мастер. Вы можете просматривать заявки, брать на себя их.";
            }
        }
        AlertManager.showInformationAlert("Информация о проекте", textInfo);
    }

    @FXML
    void onExit(ActionEvent event) {
        try {
            ConnectionModule.exit();
            Client.userType = UserType.UNDEFINED;
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.authorizationView, Client.changingWindowUtility.authorizationW, Client.changingWindowUtility.authorizationH, "Авторизация");
        } catch (IOException e) {
            AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
        }
    }

    @FXML
    void onAdd(ActionEvent event) {
        Client.purpose = null;
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.purposeEditView, Client.changingWindowUtility.purposeEditW, Client.changingWindowUtility.purposeEditH, "Добавление вида услуги");
    }

    @FXML
    void onEdit(ActionEvent event) {
        Purpose purpose = typesTable.getSelectionModel().getSelectedItem();
        if(purpose == null)
            return;

        Client.purpose = purpose;
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.purposeEditView, Client.changingWindowUtility.purposeEditW, Client.changingWindowUtility.purposeEditH, "Редактирование вида услуги");
    }

    @FXML
    void onDel(ActionEvent event) {
        System.out.println("Кнопка удаления нажата");
        Purpose purpose = typesTable.getSelectionModel().getSelectedItem();
        if(purpose != null){
            try {
                // Проверяем, есть ли связанные записи в других таблицах
             /*   if (ConnectionModule.checkPurposeUsage(purpose.getId())) {
                    AlertManager.showWarningAlert("Предупреждение", "Невозможно удалить выбранную цель, так как она используется в других таблицах.");
                } else {*/
                    ConnectionModule.deletePurpose(purpose.getId());
                    System.out.println("Запись успешно удалена");
//                }
            } catch (Exception e) {
                AlertManager.showErrorAlert("Ошибка", "Ошибка соединения с базой данных.");
            }
        }
        updatePage();
    }

    @FXML
    void onHistory(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.projectsView, Client.changingWindowUtility.projectsW, Client.changingWindowUtility.projectsH, "Записи на услуги");
    }

    @FXML
    void onRequestsButton(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.requestView, Client.changingWindowUtility.requestW, Client.changingWindowUtility.requestH, "Заявки");
    }

    @FXML
    void onUsersManagement(ActionEvent event) {
        if(userType == UserType.ADMIN)
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.userView, Client.changingWindowUtility.userW, Client.changingWindowUtility.userH, "Управление пользователями");
        else
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.profileView, Client.changingWindowUtility.profileW, Client.changingWindowUtility.profileH, "Профиль");
    }

}
