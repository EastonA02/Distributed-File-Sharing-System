package assignment2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	//Database connection information
	private static final String url =
	        "jdbc:postgresql://localhost:5432/assignment2";
	private static final String username = "YOUR_DATABASE_USERNAME";
	private static final String password = "YOUR_DATABASE_PASSWORD";

	
	// method to get database connection 
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
}

