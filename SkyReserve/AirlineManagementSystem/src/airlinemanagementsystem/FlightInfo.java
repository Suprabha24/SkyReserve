/*
 * FlightInfo.java - This class displays all flight details stored in the database.
 * It retrieves flight records from the 'flight' table and shows them in a JTable.
 */

package airlinemanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import net.proteanit.sql.DbUtils;   // Utility to directly convert ResultSet into a TableModel

public class FlightInfo extends JFrame {
    
    // Constructor - sets up the GUI and loads flight data
    public FlightInfo() {
        
        // Set background color and disable layout manager (absolute positioning)
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        // JTable to display flight details
        JTable table = new JTable();
        
        try {
            // Establish database connection
            Conn conn = new Conn();
            
            // Execute SQL query to fetch all flight records
            ResultSet rs = conn.s.executeQuery("select * from flight");
            
            // Use DbUtils to populate JTable directly from ResultSet
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch(Exception e) {
            // Print stack trace if error occurs
            e.printStackTrace();
        }
        
        // Add JTable to a scrollable pane
        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(0, 0, 800, 500); // Position and size of scroll pane
        add(jsp);
        
        // Set JFrame size and position
        setSize(800, 500);
        setLocation(400, 200);
        setVisible(true); // Make window visible
    }

    // Main method to run the FlightInfo window
    public static void main(String[] args) {
        new FlightInfo();
    }
}
