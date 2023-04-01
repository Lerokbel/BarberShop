package serverEndPoint;

import enums.UserType;

import java.net.Socket;

public class ConnectedClientInfo {

    private Socket connectionSocket;

    private int idInDB;

    private UserType userType;

    private ConnectedClientInfo() {
        connectionSocket = new Socket();
    }

    public ConnectedClientInfo(Socket connectionSocket) {
        this();
        setConnectionSocket(connectionSocket);
    }

    public synchronized Socket getConnectionSocket() {
        return connectionSocket;
    }

    public synchronized void setConnectionSocket(Socket connectionSocket) {
        if (connectionSocket == null) return;
        this.connectionSocket = connectionSocket;
    }

    public int getIdInDB() {
        return idInDB;
    }

    public void setIdInDB(int idInDB) {
        this.idInDB = idInDB;
    }

    public void setType(UserType type){this.userType = type;}
    public UserType getType(){return this.userType;}
}
