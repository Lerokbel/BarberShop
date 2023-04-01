package dbLayer.managers;

import dbLayer.repositories.*;

import java.sql.Connection;

public class DataAccessManager {

    public final ClientsRepository clientsRepository;
    public final AdminsRepository adminsRepository;
    public final MastersRepository mastersRepository;
    public final PurposesRepository purposesRepository;
    public final RecordsRepository recordsRepository;

    //инициализация каждого репозитория
    public DataAccessManager(Connection connection) {
        clientsRepository = new ClientsRepository(connection);
        adminsRepository = new AdminsRepository(connection);
        mastersRepository = new MastersRepository(connection);
        purposesRepository = new PurposesRepository(connection);
        recordsRepository = new RecordsRepository(connection);
    }
}
