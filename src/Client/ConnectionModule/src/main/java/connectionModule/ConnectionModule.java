package connectionModule;

import Commands.AuthorizationCommand;
import Commands.Command;
import Commands.Response;
import entities.*;
import entities.Record;
import enums.UserType;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
/*предоставляет интерфейс для взаимодействия клиента с сервером;
     отвечает за отправку/получение данных от сервера*/
public class ConnectionModule {
    /*Статический блок инициализации выполняется один раз,
    при первом вызове класса ConnectionModule и загружает параметры подключения
    к серверу из файла config.properties*/
    private static Socket connectionSocket;
    private static final String serverIp;
    private static final int serverPort;
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;

    private static Properties getPropertiesFromConfig() throws IOException {

        var properties = new Properties();
        String propFileName = "Client/ConnectionModule/src/main/resources/config.properties";
        var inputStream = new FileInputStream(propFileName);
        if (inputStream == null)
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        properties.load(inputStream);
        return properties;
    }

    static {
        try {
            var properties = getPropertiesFromConfig();
            serverIp = properties.getProperty("serverIp");
            serverPort = Integer.parseInt(properties.getProperty("serverPort"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
/* connectToServer() устанавливает соединение с сервером и возвращает true,
 если соединение было успешно установлено*/
    public static boolean connectToServer() throws IOException {

        connectionSocket = new Socket(serverIp, serverPort);
        if (!connectionSocket.isConnected()) return false;
        objectOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
        return true;
    }
/*отправляет объект на сервер*/
    private static void sendObject(Serializable object) throws IOException {

        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }
    /*ожидает получения объекта от сервера и возвращает его*/
    private static  <T> T receiveObject() throws Exception {

        return (T) objectInputStream.readObject();
    }

    public static UserType singUp(String login, String password) throws Exception {

        sendObject(AuthorizationCommand.AUTHORIZE);//отправление команды авторизации на сервер
        sendObject(login);
        sendObject(password);
        return receiveObject();// получение ответа от сервера
    }

    public static Response registration(User user) throws Exception { // регистрация клиента

        sendObject(AuthorizationCommand.REGISTER);// запрос на регистрацию пользователя
        sendObject(user);
        return receiveObject();
    }

    public static Response registrationMaster(Master master) throws Exception { //регистрация мастера

        sendObject(AuthorizationCommand.REGISTER_MASTER);
        sendObject(master);
        return receiveObject();
    }

    //ТОЛЬКО ПРИ РЕГИСТРАЦИИ
    public static boolean checkIfLoginExists(String login) throws Exception {
        sendObject(AuthorizationCommand.CHECK_IF_LOGIN_EXISTS);
        sendObject(login);
        Response response = receiveObject(); //сохраняет ответ в переменную response
        return response == Response.SUCCESSFULLY;
    }

    //разрыв соединения между клиентом и сервером
    public static void exit() throws IOException {
        sendObject(Command.EXIT);
    }

    public static List<Purpose> getAllPurposes() throws Exception {
        sendObject(Command.GET_ALL_PURPOSES);
        return receiveObject();
    }

    public static Response createPurpose(Purpose purpose) throws Exception {
        sendObject(Command.CREATE_PURPOSE);
        sendObject(purpose);
        return receiveObject();
    }

    public static Response editPurpose(Purpose purpose) throws Exception {
        sendObject(Command.EDIT_PURPOSE);
        sendObject(purpose);
        return receiveObject();
    }

    public static Response deletePurpose(int purposeId) throws Exception {
        System.out.println("Выполняем запрос на удаление записи с ID " + purposeId);
        sendObject(Command.DELETE_PURPOSE);
        sendObject(purposeId);
        System.out.println("Запись успешно удалена");
        return receiveObject();
    }

    public static Response banClient(int clientId) throws Exception {
        sendObject(Command.BAN_CLIENT);
        sendObject(clientId);
        return receiveObject();
    }

    public static Response unbanClient(int clientId) throws Exception {
        sendObject(Command.UNBAN_CLIENT);
        sendObject(clientId);
        return receiveObject();
    }

    public static Response unbanMaster(int masterId) throws Exception {
        sendObject(Command.UNBAN_MASTER);
        sendObject(masterId);
        return receiveObject();
    }

    public static Response banMaster(int masterId) throws Exception {
        sendObject(Command.BAN_MASTER);
        sendObject(masterId);
        return receiveObject();
    }

    public static Response registerMaster(Master master) throws Exception {
        sendObject(Command.REGISTER_MASTER);
        sendObject(master);
        return receiveObject();
    }

    public static Response registerUser(User user) throws Exception {
        sendObject(Command.REGISTER_USER);
        sendObject(user);
        return receiveObject();
    }

    public static List<Record> getAllCurrentMasterRecords() throws Exception {
        sendObject(Command.GET_ALL_CURRENT_MASTER_RECORDS);
        return receiveObject();
    }

    public static List<Record> getAllCurrentClientRecords() throws Exception {
        sendObject(Command.GET_ALL_CURRENT_CLIENT_RECORDS);
        return receiveObject();
    }

    public static List<Record> getAllRecordsNotAccepted() throws Exception {
        sendObject(Command.GET_ALL_RECORDS_NOT_ACCEPTED);
        return receiveObject();
    }

    public static List<Record> getAllRecord() throws Exception {
        sendObject(Command.GET_ALL_RECORDS);
        return receiveObject();
    }

    public static List<Record> getAllRecordsAccepted() throws Exception {
        sendObject(Command.GET_ALL_RECORDS_ACCEPTED);
        return receiveObject();
    }

    public static List<User> getAllClients() throws Exception {
        sendObject(Command.GET_ALL_CLIENTS);
        return receiveObject();
    }

    public static List<Master> getAllMasters() throws Exception {
        sendObject(Command.GET_ALL_MASTERS);
        return receiveObject();
    }
    public static Response acceptRecordToCurrentMaster(int recordId) throws Exception {
        sendObject(Command.ACCEPT_RECORD_TO_CURRENT_MASTER);
        sendObject(recordId);
        return receiveObject();
    }

    public static Response deleteRecord(int recordId) throws Exception {
        sendObject(Command.DELETE_RECORD);
        sendObject(recordId);
        return receiveObject();
    }

    public static Response deleteAcception(int recordId) throws Exception {
        sendObject(Command.DELETE_ACCEPTION);
        sendObject(recordId);
        return receiveObject();
    }

    public static Response createRecord(Record record) throws Exception {
        sendObject(Command.CREATE_RECORD);
        sendObject(record);
        return receiveObject();
    }

    public static User getCurrentProfileClient() throws Exception {
        sendObject(Command.GET_CURRENT_PROFILE);
        return receiveObject();
    }

    public static Master getCurrentProfileMaster() throws Exception {
        sendObject(Command.GET_CURRENT_PROFILE);
        return receiveObject();
    }

    public static Admin getCurrentProfileAdmin() throws Exception {
        sendObject(Command.GET_CURRENT_PROFILE);
        return receiveObject();
    }

    public static Response editCurrentProfile(UserType type, User user) throws Exception {
        sendObject(Command.EDIT_CURRENT_PROFILE);
        sendObject(type);
        sendObject(user);
        return receiveObject();
    }

    public static Response editCurrentProfile(UserType type, Master master) throws Exception {
        sendObject(Command.EDIT_CURRENT_PROFILE);
        sendObject(type);
        sendObject(master);
        return receiveObject();
    }
}