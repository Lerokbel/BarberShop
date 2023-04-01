package com.example;

import connectionModule.ConnectionModule;
import entities.Purpose;
import enums.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PurposeEditController {
    @FXML
    public void initialize(){
        if(Client.purpose != null) {
            nameInput.setText(Client.purpose.getName());
            costInput.setText(String.valueOf(Client.purpose.getCost()));
        }
    }
    @FXML
    private TextField nameInput;

    @FXML
    private TextField costInput;

    @FXML
    void onCancel(ActionEvent event) {
        Client.changingWindowUtility.showWindow(Client.changingWindowUtility.typesView, Client.changingWindowUtility.typesW, Client.changingWindowUtility.typesH, "Предоставляемые услуги");
    }

    @FXML
    void onOK(ActionEvent event) {
        String name = nameInput.getText();
        String costText = costInput.getText();

        if(name.isEmpty() || costText.isEmpty()){
            AlertManager.showErrorAlert("Ошибка!", "Введите все поля");
            return;
        }

        float cost = -1;

        try{
            cost = Float.valueOf(costText);
            if(cost<=0)
                throw new NumberFormatException();
        }
        catch (NumberFormatException e){
            AlertManager.showErrorAlert("Ошибка", "В поле стоиости должно быть число");
            return;
        }
        try {
            if(Client.purpose == null){
                ConnectionModule.createPurpose(new Purpose(0, name, cost));
            }
            else{
                Client.purpose.setName(name);
                Client.purpose.setCost(cost);
                ConnectionModule.editPurpose(Client.purpose);
            }
            Client.changingWindowUtility.showWindow(Client.changingWindowUtility.typesView, Client.changingWindowUtility.typesW, Client.changingWindowUtility.typesH, "Предоставляемые услуги");
        } catch (Exception e) {
            AlertManager.showErrorAlert("Ошибка!", "");
        }
    }

}
