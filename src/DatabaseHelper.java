import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHelper {

    private static DatabaseHelper instance;
    private MysqlDataSource dataSource;

    private DatabaseHelper(){
        dataSource = new MysqlDataSource();
        dataSource.setUser("java");
        dataSource.setPassword("password");
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(3306);
        dataSource.setDatabaseName("test_verifinger");
    }

    public static DatabaseHelper getInstance(){
        if(instance == null)
            instance = new DatabaseHelper();
        return instance;
    }

    public void saveUser(String username, byte[] template) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into user(user_name,finger_template) values(?,?)");
        statement.setString(1,username);
        Blob blob = new SerialBlob(template);
        statement.setBlob(2, blob);
        statement.executeUpdate();
        connection.close();
    }
}
