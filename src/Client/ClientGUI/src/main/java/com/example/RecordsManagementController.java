package com.example;

import connectionModule.ConnectionModule;
import entities.Purpose;
import entities.Record;
import enums.UserType;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecordsManagementController {

    boolean isHistory = false;
    private void updatePage(){

        ObservableList<Record> records = FXCollections.observableArrayList();

        String scostFrom = costFromInput.getText();
        String scostTo = costToInput.getText();
        String nameSearch = nameSearchInput.getText();

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
            List<Record> list = null;//создается пустой список записей
            if(Client.userType == UserType.USER) { //запрашивается нужный список записей с сервера
                list = ConnectionModule.getAllCurrentClientRecords();
            }
            else if(Client.userType == UserType.MASTER){
                list = ConnectionModule.getAllCurrentMasterRecords();
            }
            else {
                list = ConnectionModule.getAllRecordsAccepted();
            }
            for (var item: list) {
                boolean isNeedToShow = true; //для каждой записи создается переменная isNeedToShow

                if(costFrom != -1)
                    isNeedToShow &= item.getPurpose().getCost() >= costFrom;

                if(costTo != -1)
                    isNeedToShow &= item.getPurpose().getCost() <= costTo;

                if(!nameSearch.isEmpty())
                    isNeedToShow &= item.getPurpose().getName().contains(nameSearch);

                Date current = new Date();

                if(isHistory)
                    isNeedToShow &= item.getDate().getTime() < current.getTime();
                else {
                    isNeedToShow &= item.getDate().getTime() > current.getTime();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateWithoutTime = sdf.parse(sdf.format(new Date()));

                    if (checkToday.isSelected() && checkToday.isVisible() && !checkToday.isDisabled())
                        isNeedToShow &= sdf.parse(sdf.format(item.getDate())).equals(dateWithoutTime);
                }

                if(isNeedToShow)
                    records.add(item);
            }

            projectsTable.setItems(records);//Список records устанавливается как источник данных для таблицы (projectsTable)

        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "Ошбика соединения");
        }
    }

    @FXML
    public void initialize(){

        if(Client.userType == UserType.ADMIN){
            historyBtn.setVisible(false);
            isHistory = true;
        }

       if(Client.userType != UserType.MASTER){
           cancelBtn.setVisible(false);
           checkToday.setVisible(false);
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
        colClient.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return String.valueOf(financedProjectStringCellDataFeatures.getValue().getClient().getFullName());
                    }
                };
            }
        });
        colTelephone.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> financedProjectStringCellDataFeatures) {
                return new ObservableValueBase<String>() {
                    @Override
                    public String getValue() {
                        return financedProjectStringCellDataFeatures.getValue().getClient().getPhone();
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
    @FXML
    private TextField costFromInput;

    @FXML
    private TextField costToInput;

    @FXML
    private TextField nameSearchInput;

    @FXML
    private TableView<Record> projectsTable;

    @FXML
    private TableColumn<Record, String> colTelephone;

    @FXML
    private TableColumn<Record, String> colCost;

    @FXML
    private TableColumn<Record, String> colDate;

    @FXML
    private TableColumn<Record, String> colClient;

    @FXML
    private TableColumn<Record, String> colName;

    @FXML
    private Button historyBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private CheckBox checkToday;
    @FXML
    void onApply(ActionEvent event) {
        updatePage();
    }

    @FXML
    void onCancel(ActionEvent event) {
        Record record = projectsTable.getSelectionModel().getSelectedItem();
        if(record == null)
            return;
        try {
            ConnectionModule.deleteAcception(record.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onGoBack(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.typesView, Client.changingWindowUtility.typesW, Client.changingWindowUtility.typesH, "Предоставляемые услуги");
    }


    @FXML
    void onHistory(ActionEvent event) {
        isHistory = !isHistory;

        historyBtn.setText(isHistory? "Активные": "История");

        if(Client.userType == UserType.MASTER){
            cancelBtn.setDisable(isHistory);
            checkToday.setDisable(isHistory);
        }

        updatePage();
    }

}
