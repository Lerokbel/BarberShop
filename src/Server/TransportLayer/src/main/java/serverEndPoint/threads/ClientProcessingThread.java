package serverEndPoint.threads;

import Commands.AuthorizationCommand;
import Commands.Command;
import Commands.Response;
import dbLayer.managers.DataAccessManager;
import entities.*;
import entities.Record;
import enums.UserType;
import serverEndPoint.ConnectedClientInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

//В этом потоке происходит взаимодействие с клиентом
public class ClientProcessingThread extends Thread {

    private final DataAccessManager dataAccessManager;

    private final ConnectedClientInfo clientInfo;

    private final ObjectOutputStream objectOutputStream;

    private final ObjectInputStream objectInputStream;

    public ClientProcessingThread(ConnectedClientInfo clientInfo, Connection dbConnection) throws IOException {
        this.clientInfo = clientInfo;
        var socket = clientInfo.getConnectionSocket(); //создается сокет для подключения к клиенту
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());//запись данных в сокет
        objectInputStream = new ObjectInputStream(socket.getInputStream());//чтение данных из сокета
        dataAccessManager = new DataAccessManager(dbConnection);//обрабатывает запросы к бд
    }

    private void sendObject(Serializable object) throws IOException {

        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    private <T> T receiveObject() throws IOException, ClassNotFoundException {

        return (T) objectInputStream.readObject();
    }

    @Override
    public void run() {

        while (true) {
            try {
                switch (clientLobby()) {
                    case ADMIN, USER, MASTER -> {
                        processing();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void interrupt() {
        try {
            //Заканчиваем работу
            clientInfo.getConnectionSocket().close();
        } catch (IOException e) { //Аналогично
            throw new RuntimeException(e);
        }
        super.interrupt();
    }

    public ConnectedClientInfo getClientInfo() {
        return clientInfo;
    }

    private UserType clientLobby() throws Exception {

        while (true) {

            AuthorizationCommand command = receiveObject();
            switch (command) {
                case AUTHORIZE -> {

                    String login = receiveObject();
                    String password = receiveObject();
                    var user = dataAccessManager.clientsRepository.get(login, password);
                    if (user.getId() != 0 && user.getStatus() == Status.NOT_BANNED) {
                        sendObject(UserType.USER);
                        clientInfo.setIdInDB(user.getId());
                        clientInfo.setType(UserType.USER);
                        return UserType.USER;
                    }
                    var admin = dataAccessManager.adminsRepository.get(login, password);
                    if (admin.getId() != 0) {
                        sendObject(UserType.ADMIN);
                        clientInfo.setIdInDB(admin.getId());
                        clientInfo.setType(UserType.ADMIN);
                        return UserType.ADMIN;
                    }
                    var master = dataAccessManager.mastersRepository.get(login, password);
                    if (master.getId() != 0) {
                        sendObject(UserType.MASTER);
                        clientInfo.setIdInDB(master.getId());
                        clientInfo.setType(UserType.MASTER);
                        return UserType.MASTER;
                    }
                    clientInfo.setIdInDB(0);
                    sendObject(UserType.UNDEFINED);
                }
                case CHECK_IF_LOGIN_EXISTS -> {

                    String login = receiveObject();
                    var user = dataAccessManager.clientsRepository.get(login);
                    var admin = dataAccessManager.adminsRepository.get(login);
                    if (user.getId() == 0 && admin.getId() == 0) {
                        sendObject(Response.NOT_FOUND);
                    } else {
                        sendObject(Response.SUCCESSFULLY);
                    }
                }
                case REGISTER -> {
                    User user = receiveObject();
                    try {
                        int id = dataAccessManager.clientsRepository.create(user);
                        clientInfo.setIdInDB(id);
                        clientInfo.setType(UserType.USER);
                        sendObject(Response.SUCCESSFULLY);
                        return UserType.USER;
                    } catch (Exception e) {
                        sendObject(Response.ERROR);
                    }
                }
                case REGISTER_MASTER -> {
                    Master master = receiveObject();
                    try {
                        int id = dataAccessManager.mastersRepository.create(master);
                        clientInfo.setIdInDB(id);
                        clientInfo.setType(UserType.MASTER);
                        sendObject(Response.SUCCESSFULLY);
                        return UserType.MASTER;
                    } catch (Exception e) {
                        sendObject(Response.ERROR);
                    }
                }
                default -> {
                    sendObject(Response.UNKNOWN_COMMAND);
                }
            }
        }
    }

    private void processing() throws IOException, ClassNotFoundException {

        while (true) {

            Command command = receiveObject();
            switch (command) {

                case EXIT -> {
                    return;
                }
                case GET_ALL_PURPOSES -> {
                    try {
                        var list = dataAccessManager.purposesRepository.getAll();
                        sendObject(new ArrayList<>(list));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case CREATE_PURPOSE -> {
                    Purpose purpose = receiveObject();
                    try {
                        dataAccessManager.purposesRepository.create(purpose);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case EDIT_PURPOSE -> {
                    Purpose purpose = receiveObject();
                    try {
                        dataAccessManager.purposesRepository.update(purpose);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case DELETE_PURPOSE -> {
                    int id = receiveObject();
                    try {
                        dataAccessManager.purposesRepository.delete(id);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case BAN_CLIENT -> {
                    int id = receiveObject();
                    try {
                        var user = dataAccessManager.clientsRepository.getById(id);
                        if (user.getId() == 0) {
                            sendObject(Response.ERROR);
                            continue;
                        }
                        user.setStatus(Status.BANNED);
                        dataAccessManager.clientsRepository.update(user);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case UNBAN_CLIENT -> {
                    int id = receiveObject();
                    try {
                        var user = dataAccessManager.clientsRepository.getById(id);
                        if (user.getId() == 0) {
                            sendObject(Response.ERROR);
                            continue;
                        }
                        user.setStatus(Status.NOT_BANNED);
                        dataAccessManager.clientsRepository.update(user);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case UNBAN_MASTER -> {
                    int id = receiveObject();
                    try {
                        var user = dataAccessManager.mastersRepository.getById(id);
                        if (user.getId() == 0) {
                            sendObject(Response.ERROR);
                            continue;
                        }
                        user.setStatus(Status.NOT_BANNED);
                        dataAccessManager.mastersRepository.update(user);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case BAN_MASTER -> {
                    int id = receiveObject();
                    try {
                        var user = dataAccessManager.mastersRepository.getById(id);
                        if (user.getId() == 0) {
                            sendObject(Response.ERROR);
                            continue;
                        }
                        user.setStatus(Status.BANNED);
                        dataAccessManager.mastersRepository.update(user);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case REGISTER_MASTER -> {
                    Master master = receiveObject();
                    try {
                        dataAccessManager.mastersRepository.create(master);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case REGISTER_USER -> {
                    User user = receiveObject();
                    try {
                        dataAccessManager.clientsRepository.create(user);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case GET_ALL_CURRENT_MASTER_RECORDS -> {
                    try {
                        var list = dataAccessManager.recordsRepository.getAllMasterRecords(clientInfo.getIdInDB());
                        sendObject(new ArrayList<>(list));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case GET_ALL_CURRENT_CLIENT_RECORDS -> {
                    try {
                        var list = dataAccessManager.recordsRepository.getAllClientRecords(clientInfo.getIdInDB());
                        sendObject(new ArrayList<>(list));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case ACCEPT_RECORD_TO_CURRENT_MASTER -> {
                    int recordId = receiveObject();
                    try {
                        dataAccessManager.recordsRepository.addRecordToMaster(clientInfo.getIdInDB(), recordId);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case GET_ALL_RECORDS -> {
                    try {
                        var list = dataAccessManager.recordsRepository.getAll();
                        sendObject(new ArrayList<>(list));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case GET_ALL_RECORDS_NOT_ACCEPTED -> {
                    try {
                        var list = dataAccessManager.recordsRepository.getAllNotAccepted();
                        sendObject(new ArrayList<>(list));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case GET_ALL_RECORDS_ACCEPTED -> {
                    try {
                        var list = dataAccessManager.recordsRepository.getAllAccepted();
                        sendObject(new ArrayList<>(list));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case GET_ALL_CLIENTS -> {
                    try {
                        var list = dataAccessManager.clientsRepository.getAll();
                        sendObject(new ArrayList<>(list));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case GET_ALL_MASTERS -> {
                    try {
                        var list = dataAccessManager.mastersRepository.getAll();
                        sendObject(new ArrayList<>(list));
                    } catch (SQLException e) {
                        sendObject(new ArrayList<>());
                    }
                }
                case CREATE_RECORD -> {
                    Record record = receiveObject();
                    try {
                        dataAccessManager.recordsRepository.create(record);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case DELETE_RECORD -> {
                    int id = receiveObject();
                    try {
                        dataAccessManager.recordsRepository.delete(id);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case DELETE_ACCEPTION -> {
                    int id = receiveObject();
                    try {
                        dataAccessManager.recordsRepository.deleteAcception(id);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
                case GET_CURRENT_PROFILE -> {
                    try {
                        switch (clientInfo.getType()){
                            case ADMIN -> {
                                var obj = dataAccessManager.adminsRepository.getById(clientInfo.getIdInDB());
                                sendObject(obj);
                                break;
                            }
                            case USER -> {
                                var obj = dataAccessManager.clientsRepository.getById(clientInfo.getIdInDB());
                                sendObject(obj);
                                break;
                            }
                            case MASTER -> {
                                var obj = dataAccessManager.mastersRepository.getById(clientInfo.getIdInDB());
                                sendObject(obj);
                                break;
                            }
                        }
                    } catch (SQLException e) {
                        sendObject(new User());
                    }
                }
                case EDIT_CURRENT_PROFILE -> {
                    UserType type = receiveObject();

                    User user=null;
                    Master master=null;

                    if(type == UserType.USER)
                        user = receiveObject();
                    else
                        master = receiveObject();
                    try {
                        if(user != null)
                            dataAccessManager.clientsRepository.update(user);
                        else
                            dataAccessManager.mastersRepository.update(master);
                        sendObject(Response.SUCCESSFULLY);
                    } catch (SQLException e) {
                        sendObject(Response.ERROR);
                    }
                }
            }
        }
    }
}