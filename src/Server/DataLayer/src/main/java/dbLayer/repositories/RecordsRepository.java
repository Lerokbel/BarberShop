package dbLayer.repositories;

import dbLayer.managers.DataAccessManager;
import entities.Record;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordsRepository {

    private final Connection dbConnection;

    public RecordsRepository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    private Record convertResultSetToSingleObj(ResultSet resultSet) throws SQLException {

        resultSet.beforeFirst();
        if (!resultSet.next()) return new Record();
        return convertResultSetToObj(resultSet);
    }

    private Record convertResultSetToObj(ResultSet resultSet) throws SQLException {

        var obj = new Record();
        obj.setId(resultSet.getInt("id"));
        var access = new DataAccessManager(dbConnection);
        obj.setClient(access.clientsRepository.getById(resultSet.getInt("clientId")));
        obj.setPurpose(access.purposesRepository.getById(resultSet.getInt("purposeId")));
        Date date = new Date();
        date.setTime(resultSet.getTimestamp("clearanceDateTime").getTime());
        obj.setDate(date);
        return obj;
    }

    private List<Record> convertResultSetToList(ResultSet resultSet) throws SQLException {

        var list = new ArrayList<Record>();
        resultSet.beforeFirst();
        while (resultSet.next()) {

            list.add(convertResultSetToObj(resultSet));
        }
        return list;
    }

    private int getMaxId() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT MAX(id) from records;");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int create(Record obj) throws SQLException {

        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO records (purposeId, clientId, clearanceDateTime) " +
                        "values (?, ?, ?)");

        insertStatement.setInt(1, obj.getPurpose().getId());
        insertStatement.setInt(2, obj.getClient().getId());
        insertStatement.setTimestamp(3, new Timestamp(obj.getDate().getTime()));
        insertStatement.executeUpdate();
        return getMaxId();
    }

    public void update(Record obj) throws SQLException {

        var updateStatement = dbConnection.prepareStatement(
                "UPDATE records SET purposeId=?, clientId=?, clearanceDateTime=? where id = ?");
        updateStatement.setInt(1, obj.getPurpose().getId());
        updateStatement.setInt(2, obj.getClient().getId());
        updateStatement.setTimestamp(3, new Timestamp(obj.getDate().getTime()));
        updateStatement.setInt(4, obj.getId());
        updateStatement.executeUpdate();
    }

    public void delete(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from records where id=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public void deleteAcception(int id) throws SQLException {

        var deleteStatement = dbConnection.prepareStatement(
                "DELETE from masters_records where recordId=?");
        deleteStatement.setInt(1, id);
        deleteStatement.executeUpdate();
    }

    public Record getById(int id) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM records where id = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, id);
        statement.executeQuery();
        return convertResultSetToSingleObj(statement.getResultSet());
    }

    public List<Record> getAll() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM records;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }

    public List<Record> getAllNotAccepted() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM records WHERE id NOT IN (SELECT recordId FROM masters_records);",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }

    public List<Record> getAllAccepted() throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM records WHERE id IN (SELECT recordId FROM masters_records);",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }

    public List<Record> getAllMasterRecords(int masterId) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM masters_records inner join records r on masters_records.recordId = r.id where masterId = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, masterId);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }

    public List<Record> getAllClientRecords(int clientId) throws SQLException {

        var statement = dbConnection.prepareStatement(
                "SELECT * FROM records  where clientId = ? AND id IN (SELECT recordId FROM masters_records);",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setInt(1, clientId);
        statement.executeQuery();
        return convertResultSetToList(statement.getResultSet());
    }

    public void addRecordToMaster(int masterId, int recordId) throws SQLException {
        var insertStatement = dbConnection.prepareStatement(
                "INSERT INTO masters_records (recordId, masterId) " +
                        "values (?, ?)");

        insertStatement.setInt(1, recordId);
        insertStatement.setInt(2, masterId);
        insertStatement.executeUpdate();
    }

    public void deleteRecordFromMaster(int masterId, int recordId) throws SQLException {
        var insertStatement = dbConnection.prepareStatement(
                "delete from masters_records where masterId=? and recordId = ?");

        insertStatement.setInt(1, masterId);
        insertStatement.setInt(2, recordId);
        insertStatement.executeUpdate();
    }
}
