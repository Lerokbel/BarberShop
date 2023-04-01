import java.sql.*;

public class JDBC_CRUD {
    private Connection connection;
    private String tableName;

    /**
     * Constructor creates connection and statement.
     * @param adress Adress of database.
     * @param login Login for database.
     * @param password Password for database.
     * @throws SQLException
     */
    public JDBC_CRUD(String adress, String login, String password, String tableName) throws SQLException {
        connection= DriverManager.getConnection(adress,login,password);
        this.tableName=tableName;
    }

    /**
     * Creates new programmer in database.
     * @param id Programmer name.
     * @param login Programmer name.
     * @param password Programmer location.
     * @throws SQLException
     */
    public void createProgrammer(int id, String login, String password) throws SQLException {
        String query = "INSERT INTO "+tableName+" (id, login, password) VALUES (?,?,?);";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, id);
        pstmt.setString(2, login);
        pstmt.setString(3, password);
        pstmt.executeUpdate();
    }

    /**
     * Delete programmer from database.
     * @param
     * @throws SQLException
     */
    public void deleteProgrammer(int id) throws SQLException {
        String query = "DELETE FROM "+tableName+" WHERE id=?;";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }

    /**
     * Show table and return ResultSet.
     * @return Returns table contents in ResultSet.
     * @throws SQLException
     */
    public ResultSet readTable() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM "+tableName);
        int count=rs.getMetaData().getColumnCount();
        while(rs.next()) {
            for(int i=0; i<count; i++) {
                System.out.print(rs.getString(i+1)+" ");
            }
            System.out.print("\n");
        }
        return rs;
    }

    /**
     * Updates record based on ID.
     * @param id Programmer name.
     * @param login Programmer name.
     * @param password Programmer location.
     * @throws SQLException
     */
    public void updateProgrammer(int id, String login, String password) throws SQLException {
        String query = "UPDATE "+tableName+" SET login=?, password=? WHERE id=?;";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, login);
        pstmt.setString(2, password);
        pstmt.setInt(3, id);
        pstmt.executeUpdate();
    }

    /**
     * Truncates table.
     * @throws SQLException
     */
    public void clear() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("TRUNCATE TABLE "+tableName);
    }

}


