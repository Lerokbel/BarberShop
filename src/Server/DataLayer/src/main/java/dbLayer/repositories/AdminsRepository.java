package dbLayer.repositories;

import entities.Admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminsRepository {

    private final Connection dbConnection;

    public AdminsRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }


    //Этот метод преобразует единственную строку ResultSet в объект типа Admin.
    //Если строка существует, метод использует ее для создания и заполнения объекта Admin
    // Если ResultSet не содержит строк, метод возвращает новый экземпляр Admin.
    private Admin convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next())
            return new Admin();
        return convertResultSetToObj(resultSet);
    }


    //ResultSet представляет таблицу данных, полученную из базы данных,
    // когда выполнен запрос к ней.

    //В этом методе каждое поле из ResultSet извлекается по имени столбца и устанавливается в соответствующее поле объекта Admin.
    // Затем созданный объект Admin возвращается.
    // Если в ResultSet нет строк, то возвращается пустой объект Admin.
    private Admin convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new Admin();
        obj.setId(resultSet.getInt("id"));
        obj.setLogin(resultSet.getString("login"));
        obj.setPassword(resultSet.getString("password"));
        return obj;
    }

    private List<Admin> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<Admin>(); //создание нового ArrayList для хранения объектов Admin
        resultSet.beforeFirst();// для перемещения курсора на первую строку, если он уже был перемещен
        while (resultSet.next()) { //перемещается курсор и добавляется объект admin в список
            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from admins;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1); //метод "getInt(1)" вызывается,
        // чтобы получить значение первого столбца текущей строки, который является максимальным ID
    }

    public int create(Admin obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO admins (login, password) " +
                        "values (?, ?)");

        insertStatement.setString(1, obj.getLogin());
        insertStatement.setString(2, obj.getPassword());
        insertStatement.executeUpdate();//выполнение запроса на вставку новой записи в таблицу admins
        return getMaxId();
    }

    public void update(Admin obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE admins SET login=?, password=?  where id = ?");
        updateStatement.setString(1, obj.getLogin());
        updateStatement.setString(2, obj.getPassword());
        updateStatement.setInt(3, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from admins where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public Admin getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM admins where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);//устанавливается значение параметра в запросе
        statement.executeQuery(); //выполняет запрос на выборку записи из таблицы "admins"
        return convertResultSetToSingleObj(statement.getResultSet());//Метод "convertResultSetToSingleObj" преобразует первую строку
        // ResultSet в объект "Admin" и возвращает его
    }


    //получает запись из таблицы "admins" по заданным логину и паролю и возвращает объект "Admin" соответствующий этой записи.
    public Admin get(String login, String password) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM admins where login = ? AND password = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setString(1, login);
        statement.setString(2, password);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public Admin get(String login) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM admins where login = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setString(1, login);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    //выбирает все записи из таблицы admin
    public List<Admin> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM admins;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }


}
