package assignment2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import assignment2.FileSharePackage.ClientInfo;

public class FileShareImpl extends FileSharePOA {
	
	//COBRA METHODS

	@Override
	public void registerFile(String ownerID, String fileName, boolean shared) {
		// open database connection
		try {
			Connection con = DatabaseConnection.getConnection();
			
			//SQL Query
			String sql = "insert into shared_files (id, file_name, owner_id, shared)"
					+ " values (?, ?, ?, ?)";
			
			//Create statement object
			PreparedStatement ps = con.prepareStatement(sql);
			
			//fill in "?" place-holders in SQL
			ps.setString(1, ownerID + "_" + fileName);
			ps.setString(2, fileName);
			ps.setString(3, ownerID);
			ps.setBoolean(4, shared);
	        
	        //executeUpdate = execute SQL statement that changes the db
	        ps.executeUpdate();
	        System.out.println("File registered successfulyy!");
	        
	        con.close();
				
			
		} catch(SQLException e) {
			System.out.println(e);
		}
		
		
	}

	@Override
	public void updateFile(String ownerID, String fileName, boolean shared) {
		// get database connection
				try {
					Connection con = DatabaseConnection.getConnection();
					
					String sql = "update shared_files set shared = ? "
							+ "where file_name = ? and owner_id = ?";
					
					//Create statement object
					PreparedStatement ps = con.prepareStatement(sql);
					
					ps.setBoolean(1, shared);
					ps.setString(2, fileName);
					ps.setString(3, ownerID);
					
					ps.executeUpdate();
					System.out.println("File Updated!");
					
					con.close();
					
				} catch(SQLException e) {
					System.out.println(e);
				}
		
	}

	@Override
	public boolean searchFile(String fileName) {
		// get database connection
				try {
					Connection con = DatabaseConnection.getConnection();
					
					String sql = "select * from shared_files where "
							+ "file_name = ? and shared = true";
					
					//Create statement object
					PreparedStatement ps = con.prepareStatement(sql);
					
					//fill in "?" place-holders in SQL
					ps.setString(1, fileName);
					
					ResultSet rs = ps.executeQuery();
					if(rs.next()) {
						//if query finds at-least one row, return true
						return true;
					}
					
					con.close();
					
				} catch(SQLException e) {
					System.out.println(e);
				}
				
		return false;
		
	}

	@Override
	public ClientInfo downloadFile(String fileName) {
		// get database connection
				try {
					Connection con = DatabaseConnection.getConnection();
					
					
					// Select the owner's IP address and port
					String sql = "select clients.ip_address, clients.port "

					        // Start by looking in the shared_files table
					        + "from shared_files "

					        // Join the matching owner from the clients table
					        + "join clients "

					        // Match rows where both tables have the same owner_id
					        + "on shared_files.owner_id = clients.owner_id "

					        // Find the requested file
					        + "where shared_files.file_name = ? "

					        // Only return the file if it is currently shared
					        + "and shared_files.shared = true";
					
					// Create prepared statement object 
			        PreparedStatement ps = con.prepareStatement(sql);

			        // Fill in the ? placeholder with the requested file name
			        ps.setString(1, fileName);

			        // Execute SQL query
			        ResultSet rs = ps.executeQuery();

			        // If a matching shared file was found
			        if (rs.next()) {

			            // Retrieve the owner's IP address and port
			            String ipAddress = rs.getString("ip_address");
			            int port = rs.getInt("port");

			            con.close();

			            // Return the owner's connection information
			            return new ClientInfo(ipAddress, port);
			        }

			        con.close();
					
				} catch(SQLException e) {
					System.out.println(e);
				}
	
	
				return null;

	}
}
