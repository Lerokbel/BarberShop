import org.junit.Ignore;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class JDBC_CRUDTest {
    private static final String ADRESS = "jdbc:mysql://localhost:3306/barbershop_database";
    private static final String LOGIN="root";
    private static final String PASSWORD="11122517";
    private static final String TABLE_NAME="admins";

    @Test
    public void testClass() {
        try {
            new JDBC_CRUD(ADRESS,LOGIN,PASSWORD,TABLE_NAME);
        }
        catch(SQLException e) {
            e.printStackTrace();
            fail("Failed to connect. SQLException.");
        }
    }


    @Test
    public void testCreate() {
        try {
            JDBC_CRUD db = new JDBC_CRUD(ADRESS,LOGIN,PASSWORD,TABLE_NAME);
            db.createProgrammer(1, "admin", "admin");
            db.createProgrammer(2, "no", "no");
            db.createProgrammer(3, "vasya", "vasya");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to create new programmer. SQLException");
        }
    }

    @Test
    public void testDelete() {
        try {
            JDBC_CRUD db = new JDBC_CRUD(ADRESS,LOGIN,PASSWORD,TABLE_NAME);
            db.deleteProgrammer(3);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to delete programmer. SQLException");
        }
    }

    @Test
    public void testRead() {
        ResultSet rs=null;
        try {
            JDBC_CRUD db = new JDBC_CRUD(ADRESS,LOGIN,PASSWORD,TABLE_NAME);
            rs=db.readTable();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to read database. SQLException");
        }
        assertNotNull(rs);
    }

    @Test
    public void truncateTable() {
        try {
            JDBC_CRUD db = new JDBC_CRUD(ADRESS,LOGIN,PASSWORD,TABLE_NAME);
            db.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to truncate table. SQLException");
        }
    }

    @Test
    public void testUpdate() {
        try {
            JDBC_CRUD db = new JDBC_CRUD(ADRESS,LOGIN,PASSWORD,TABLE_NAME);
            db.updateProgrammer(2, "Petya", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to update database. SQLException");
        }
    }
}



