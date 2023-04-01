package com.example;

import connectionModule.ConnectionModule;
import entities.Record;
import enums.UserType;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class RequestManagementController {

    @FXML
    public void initialize(){ //Проверка, является ли текущий пользователь типом USER
        if(Client.userType == UserType.USER) {
            applyButton.setText("Записаться");
        }
        else if(Client.userType == UserType.ADMIN)
            applyButton.setVisible(false);
        if(Client.userType != UserType.USER){
            discardButton.setVisible(false);
        }

        colName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return financedProjectStringCellDataFeatures.getValue().getPurpose().getName();
                    }
                };
            }
        });
        colCost.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return String.valueOf(financedProjectStringCellDataFeatures.getValue().getPurpose().getCost());
                    }
                };
            }
        });
        colPhone.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return String.valueOf(financedProjectStringCellDataFeatures.getValue().getClient().getPhone());
                    }
                };
            }
        });
        colCreator.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return financedProjectStringCellDataFeatures.getValue().getClient().getFullName();
                    }
                };
            }
        });
        colDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        DateFormat fmt = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                        return fmt.format(financedProjectStringCellDataFeatures.getValue().getDate());
                    }
                };
            }
        });

        updatePage();
    }

    private void updatePage(){
        ObservableList<Record> projects = FXCollections.observableArrayList();

        String scostFrom = costFromInput.getText();
        String scostTo = costToInput.getText();
        String nameSearch = nameInput.getText();

        double costFrom = -1;
        double costTo = -1;

        if(!scostFrom.isEmpty()){
            try {
                costFrom = Double.parseDouble(scostFrom);
            }
            catch (NumberFormatException e){
                AlertManager.showErrorAlert("Ошибка!", "Поле фильтрации должно содержать только цифры");
                return;
            }
        }

        if(!scostTo.isEmpty()){
            try {
                costTo = Double.parseDouble(scostTo);
            }
            catch (NumberFormatException e){
                AlertManager.showErrorAlert("Ошибка!", "Поле фильтрации должно содержать только цифры");
                return;
            }
        }

        try {
            var list = ConnectionModule.getAllRecordsNotAccepted();
            for (var item: list) {
                boolean isNeedToShow = true;

                if(costFrom != -1)
                    isNeedToShow &= item.getPurpose().getCost() >= costFrom;

                if(costTo != -1)
                    isNeedToShow &= item.getPurpose().getCost() <= costTo;

                if(!nameSearch.isEmpty())
                    isNeedToShow &= item.getPurpose().getName().contains(nameSearch);

                if(Client.userType == UserType.USER)
                    isNeedToShow &= Client.connectedUser.getId() == item.getClient().getId();

                if(isNeedToShow)
                    projects.add(item);
            }

            requestTable.setItems(projects);

        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
        }
    }
    @FXML
    private Button applyButton;

    @FXML
    private Button discardButton;

    @FXML
    private TextField costFromInput;

    @FXML
    private TextField costToInput;

    @FXML
    private TableView<Record> requestTable;

    @FXML
    private TableColumn<Record, String> colCost;

    @FXML
    private TableColumn<Record, String> colCreator;

    @FXML
    private TableColumn<Record, String> colDate;

    @FXML
    private TableColumn<Record, String> colName;

    @FXML
    private TableColumn<Record, String> colPhone;

    @FXML
    private TextField nameInput;

    @FXML
    void onApply(ActionEvent event) {
        if(Client.userType == UserType.MASTER){
            var item = requestTable.getSelectionModel().getSelectedItem();
            if(item != null){
                try {
                    ConnectionModule.acceptRecordToCurrentMaster(item.getId());
                    updatePage();
                } catch (Exception e) {
                    AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
                }
            }
        }
        else{
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.createRecord, Client.changingWindowUtility.createW, Client.changingWindowUtility.createH, "Создание записи");
        }
    }

    @FXML
    void onDiscard(ActionEvent event) {
        if(Client.userType == UserType.USER){
            var item = requestTable.getSelectionModel().getSelectedItem();
            if(item != null){
                try {
                    ConnectionModule.deleteRecord(item.getId());
                    updatePage();
                } catch (Exception e) {
                    AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
                }
            }
        }
    }

    @FXML
    void onApplyFilter(ActionEvent event) {
        updatePage();
    }

    @FXML
    void onGoBack(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.typesView, Client.changingWindowUtility.typesW, Client.changingWindowUtility.typesH, "Предоставляемые услуги");
    }

}