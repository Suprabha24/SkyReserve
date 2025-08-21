/*
 * Conn.java - This class establishes a connection with the MySQL database.
 * It loads the JDBC driver, connects to the 'airlinemanagementsystem' database,
 * and creates a Statement object for executing SQL queries.
 */

package airlinemanagementsystem;

import java.sql.*;

public class Conn {
    
    // Database connection object
    Connection c;
    
    // Statement object to execute SQL queries
    Statement s;
    
    // Constructor - establishes database connection
    public Conn() {
        try {
            // 1. Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Establish connection to the database
            //    Format: jdbc:mysql://hostname:port/dbname, username, password
            c = DriverManager.getConnection("jdbc:mysql:///airlinemanagementsystem", "root", "Pass@123");
            
            // 3. Create statement object for running SQL queries
            s = c.createStatement();
        } catch (Exception e) {
            // Print error details if connection fails
            e.printStackTrace();
        }
    }
}
