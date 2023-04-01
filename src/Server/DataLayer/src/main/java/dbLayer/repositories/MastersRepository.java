package dbLayer.repositories;

import entities.Master;
import entities.Status;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MastersRepository {

    private final Connection dbConnection;

    public MastersRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    private Master convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new Master();
        return convertResultSetToObj(resultSet);
    }

    private Master convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new Master();
        obj.setId(resultSet.getInt("id"));
        obj.setLogin(resultSet.getString("login"));
        obj.setPassword(resultSet.getString("password"));
        obj.setFullName(resultSet.getString("fullName"));
        obj.setExperience(resultSet.getInt("experience"));
        switch (resultSet.getInt("status")){
            case 0 -> {
                obj.setStatus(Status.BANNED);
            }
            case 1 -> {
                obj.setStatus(Status.NOT_BANNED);
            }
        }
        return obj;
    }

    private List<Master> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<Master>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet)); //добавляется объект admin в список
        }
        return list;
    }

    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from masters;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int create(Master obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO masters (login, password, fullName, experience, status) " +
                        "values (?, ?, ?, ?, ?)");

        insertStatement.setString(1, obj.getLogin());
        insertStatement.setString(2, obj.getPassword());
        insertStatement.setString(3, obj.getFullName());
        insertStatement.setInt(4, obj.getExperience());
        insertStatement.setInt(5, obj.getStatus().ordinal());
        insertStatement.executeUpdate();
        return getMaxId();
    }

    public void update(Master obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE masters SET login=?, password=?, fullName=?, experience=?, status=? where id = ?");
        updateStatement.setString(1, obj.getLogin());
        updateStatement.setString(2, obj.getPassword());
        updateStatement.setString(3, obj.getFullName());
        updateStatement.setInt(4, obj.getExperience());
        updateStatement.setInt(5, obj.getStatus().ordinal());
        updateStatement.setInt(6, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from masters where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public Master getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM masters where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    //получает запись из таблицы "masters" по заданным логину и паролю и возвращает объект "Master" соответствующий этой записи
    public Master get(String login, String password) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM masters where login = ? AND password = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setString(1, login);
        statement.setString(2, password);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public Master get(String login) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM masters where login = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setString(1, login);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public List<Master> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM masters;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }
}
