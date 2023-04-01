package com.example;

import connectionModule.ConnectionModule;
import entities.Purpose;
import entities.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

//выпадающие списки типов услуг и времени записи
public class AddRecordController {

    @FXML
    public void initialize(){

        try {
            var list = ConnectionModule.getAllPurposes();

            Callback<ListView<Purpose>, ListCell<Purpose>> cellFactory = new Callback<ListView<Purpose>, ListCell<Purpose>>() {
                @Override
                public ListCell<Purpose> call(ListView<Purpose> l) {
                    return new ListCell<Purpose>() {
                        @Override
                        protected void updateItem(Purpose item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                setText(item.getName());
                            }
                        }
                    } ;
                }
            };
            typeCombo.setCellFactory(cellFactory);
            typeCombo.setConverter(new StringConverter<Purpose>() {
                @Override
                public String toString(Purpose projectType) {
                    return String.valueOf(projectType.getId()) + "." + projectType.getName() + "(" + projectType.getCost() + ")";
                }



                //substring принимает начальный (0) и конечный индексы подстроки
                //преобразования строки в объект типа Purpose
                @Override
                public Purpose fromString(String s) {
                    int id = Integer.parseInt(s.substring(0, s.indexOf('.') - 1));
                    String name = s.substring(s.indexOf('.')+1, s.indexOf('(')-1);
                    float cost = Float.valueOf(s.substring(s.indexOf('(') + 1, s.indexOf(')')-1));

                    Purpose projectType = new Purpose(id, name, cost); //создание объекта типа Purpose
                    return projectType;
                }
            });

            timeCombo.setConverter(new StringConverter<Date>() { //реализует интерфейс StringConverter
                @Override
                public String toString(Date date) { //для преобразования объекта Date в строку
                    DateFormat format = new SimpleDateFormat("HH:mm");
                    return format.format(date);
                }


                //принимает строку в формате "HH:mm" и преобразует ее в объект Date
                @Override
                public Date fromString(String s) {
                    int h = Integer.parseInt(s.substring(0, s.indexOf(':')));
                    int m = Integer.parseInt(s.substring(s.indexOf(':')+1));
                    Date date = new Date();
                    date.setTime(h*3600*1000 + m * 60 *1000);
                    return date;
                }
            });

            //заполнение выпадающего списка TimeCombo
            ObservableList<Purpose> projects = FXCollections.observableList(list);//заполнение ObservableList данными из переменной list
            typeCombo.setItems(projects);

            Date time = new Date();
            time.setTime(21*3600*1000 + 9*3600*1000);
            timeCombo.getItems().add(time);//добавление в выпадающий список
            time = new Date();
            time.setTime(21*3600*1000 + 9*3600*1000 + 30 * 60 * 1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 10*3600*1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 10*3600*1000 + 30 * 60 * 1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 11*3600*1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 11*3600*1000 + 30 * 60 * 1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 12*3600*1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 12*3600*1000 + 30 * 60 * 1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 13*3600*1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 13*3600*1000 + 30 * 60 * 1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 14*3600*1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 14*3600*1000 + 30 * 60 * 1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 15*3600*1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 15*3600*1000 + 30 * 60 * 1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 16*3600*1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 16*3600*1000 + 30 * 60 * 1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 17*3600*1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 17*3600*1000 + 30 * 60 * 1000);
            timeCombo.getItems().add(time);
            time = new Date();
            time.setTime(21*3600*1000 + 18*3600*1000);
            timeCombo.getItems().add(time);
            time = new Date();
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка", "");
        }
    }
    @FXML
    private ComboBox<Date> timeCombo;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Purpose> typeCombo;

    @FXML
    void onCancel(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.requestView, Client.changingWindowUtility.requestW, Client.changingWindowUtility.requestH, "Записи");
    }

    @FXML
    void onSave(ActionEvent event) {
        Purpose projectType = typeCombo.getValue();
        Date date =Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date time = timeCombo.getValue();
//сообщение об ошибке, если какое-то поле не заполнено
        if(projectType == null || date == null || time == null){
            AlertManager.showErrorAlert("Ошибка", "Заполните все поля");
            return;
        }
        date.setTime(date.getTime() + time.getTime() - 21 * 3600 * 1000);

        Record record = new Record(); //заполнение объекта полученными значениями
        record.setDate(date);
        record.setClient(Client.connectedUser);
        record.setPurpose(projectType);

        try {
            ConnectionModule.createRecord(record);// отправка в БД
            //если запись создана, то пользователь отправлятся в новое окно
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.requestView, Client.changingWindowUtility.requestW, Client.changingWindowUtility.requestH, "Записи");
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка соединения", "");
            return;
        }
    }

}
