package serverEndPoint;

import serverEndPoint.threads.ClientProcessingThread;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Server {

    private int serverPort;

    private final ServerSocket acceptingSocket;

    private final Properties properties;

    private static Properties getPropertiesFromConfig() throws IOException {

        var properties = new Properties();
        String propFileName = "Server/DataLayer/src/main/resources/dbLayer/config.properties";
        var inputStream = new FileInputStream(propFileName);
        if (inputStream == null)
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        properties.load(inputStream);
        return properties;
    }

    private final List<ClientProcessingThread> processingThreads;

    Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable exception) {

            processingThreads.remove(Integer.parseInt(thread.getName()));
            new RuntimeException(exception);

        }
    };

    public Server(int serverPort) throws Exception {
        this.serverPort = serverPort;
        acceptingSocket = new ServerSocket(serverPort);
        processingThreads = new ArrayList<ClientProcessingThread>();
        properties = getPropertiesFromConfig();
    }

    public void runServer() throws Exception {

        while (true) {

            var newClientSocket = acceptingSocket.accept();
            var newClient = new ConnectedClientInfo(newClientSocket);
            var newThread = new ClientProcessingThread(newClient, DriverManager.getConnection(properties.getProperty("dbServerConnectionString")
                     + properties.getProperty("dbName"),
                    properties.getProperty("userName"),
                    properties.getProperty("password")));
            newThread.setName(String.valueOf(processingThreads.size()));
            newThread.setUncaughtExceptionHandler(exceptionHandler);
            newThread.start();
            processingThreads.add(newThread);
        }
    }

    public void stopServer() throws IOException {

        acceptingSocket.close();
        for (var thread : processingThreads) {
            thread.interrupt();
        }
    }

    public int getAmountOfConnectedClients(){
        return processingThreads.size();
    }

}