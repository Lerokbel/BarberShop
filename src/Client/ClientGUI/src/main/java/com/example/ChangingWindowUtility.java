package com.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

public class ChangingWindowUtility {
    public final String authorizationView = "AuthorizationView.fxml";
    public final double authorizationW = 228;
    public final double authorizationH = 123;

    public final String registrationView = "RegistrationView.fxml";
    public final double registrationW = 227;
    public final double registrationH = 273;

    public final String addUserView = "AddUserView.fxml";
    public final double addUserW = 227;
    public final double addUserH = 273;

    public final String profileView = "ProfileView.fxml";
    public final double profileW = 279;
    public final double profileH = 146;

    public final String projectsView = "RecordsManagementView.fxml";
    public final double projectsW = 955;
    public final double projectsH = 641;

    public final String purposeEditView = "PurposeEditView.fxml";
    public final double purposeEditW = 279;
    public final double purposeEditH = 145;

    public final String typesView = "PurposesManagementView.fxml";
    public final double typesW = 409;
    public final double typesH = 309;

    public final String createRecord = "CreateRecordView.fxml";
    public final double createW = 285;
    public final double createH = 223;

    public final String requestView = "RequestManagementView.fxml";
    public final double requestW = 769;
    public final double requestH = 575;

    public final String userView = "UserManagementView.fxml";
    public final double userW = 556;
    public final double userH = 400;
    private final Stage stage;

    //используется для загрузки fxml-файла с указанным именем и создания сцены заданной ширины и высоты, используя FXMLLoader.
    public <ControllerType> Pair<ControllerType, Scene> getScene(String viewName, double width, double height) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource( viewName));
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            ControllerType controller = fxmlLoader.getController();
            return new Pair<>(controller, scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //загружает элемент управления с заданным именем, используя FXMLLoader
    public <ControllerType, ItemType> Pair<ControllerType, ItemType> getItem(String itemName) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource(itemName));
            ItemType item = fxmlLoader.load();
            ControllerType controller = fxmlLoader.getController();
            return new Pair<>(controller, item);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showWindow(Scene scene, String title) {

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void showWindow(String name, double width, double height, String title) {
        stage.setTitle(title);
        stage.setScene(getScene(name, width, height).getValue());
        stage.show();
    }

    public ChangingWindowUtility(Stage stage) {
        this.stage = stage;
    }
}
