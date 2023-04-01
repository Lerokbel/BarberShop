package dbLayer.repositories;

import entities.Master;
import entities.Purpose;
import entities.Status;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurposesRepository {

    private final Connection dbConnection;

    public PurposesRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    private Purpose convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new Purpose();
        return convertResultSetToObj(resultSet);
    }

    private Purpose convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new Purpose();
        obj.setId(resultSet.getInt("id"));
        obj.setName(resultSet.getString("name"));
        obj.setCost(resultSet.getFloat("cost"));
        return obj;
    }

    private List<Purpose> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<Purpose>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    //возвращает максимальное значение идентификатора из таблицы "purposes" в базе данных
    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from purposes;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int create(Purpose obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO purposes (name, cost) " +
                        "values (?, ?)");

        insertStatement.setString(1, obj.getName());
        insertStatement.setFloat(2, obj.getCost());
        insertStatement.executeUpdate();
        return getMaxId();
    }

    public void update(Purpose obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE purposes SET name=?, cost=? where id = ?");
        updateStatement.setString(1, obj.getName());
        updateStatement.setFloat(2, obj.getCost());
        updateStatement.setInt(3, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from purposes where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public Purpose getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM purposes where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public List<Purpose> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM purposes;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }
}
