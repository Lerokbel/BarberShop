package ServerGui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

//Контроллер главного окна сервера
public class ServerController {

    private int port;

    private ServerProcessingThread serverProcessingThread;

    private final Thread.UncaughtExceptionHandler exceptionHandler  = (th, ex) -> System.out.println(ex);

    @FXML
    public Label serverIpLabel;
    @FXML
    public Label portLabel;
    @FXML
    public Button runServerButton;
    @FXML
    public Button stopServerButton;
    @FXML
    public Label connectedClientsLabel;
    @FXML
    public Button refreshButton;

    private static Properties getPropertiesFromConfig() throws IOException {

        var properties = new Properties();
        String propFileName = "Server/TransportLayer/src/main/resources/config.properties";
        var inputStream = new FileInputStream(propFileName);
        if (inputStream == null)
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        properties.load(inputStream);
        return properties;
    }

    public void initialize() throws IOException {
       var properties = getPropertiesFromConfig();
       port = Integer.parseInt(properties.getProperty("serverPort"));
    }

    //Показ ошибки в окошке
    private void showAlert(String header, String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(rs -> {
        });
    }

    //Кнопка старта серва(Самый главный метод)
    public void onRunServerButtonClick(ActionEvent actionEvent) {

        try {

            //Запуск потока, в котором работает сервер
            serverProcessingThread = new ServerProcessingThread(port);

            //Ставим имя чтобы был хоть какой шанс что-то понять в дебаге
            serverProcessingThread.setName("Server processing thread");

            //Присваваем ему наш отловщик исключений (Область видимости исключения ограничивается потоком)
            //Соответственно, чтобы его увидеть здесь, нужно работать через exceptionHandler
            serverProcessingThread.setUncaughtExceptionHandler(exceptionHandler);

            //Стартуем...
            serverProcessingThread.start();

            serverIpLabel.setText(InetAddress.getLocalHost().getHostAddress());
            portLabel.setText(String.valueOf(port));
            runServerButton.setDisable(true);
            stopServerButton.setDisable(false);
            refreshButton.setDisable(false);
            connectedClientsLabel.setText("0");

        } catch (UnknownHostException e) {
            showAlert("Host error", "Unable to get local host");
        } catch (IOException e) {
            showAlert("Invalid input", "Invalid port");
        } catch (Exception e) {
            showAlert("Invalid input", e.getMessage());
        }
    }

    //Кнопка стопа серва
    public void onStopServerButtonClick(ActionEvent actionEvent) {

        //Прерываем выполнение потока серва
        serverProcessingThread.interrupt();

        runServerButton.setDisable(false);
        stopServerButton.setDisable(true);
    }

    //Обновление количества подключений
    public void onRefreshButtonClick(ActionEvent actionEvent) {

        connectedClientsLabel.setText(String.valueOf(serverProcessingThread.getAmountOfConnectedClients()));
    }
}