/*
 * JourneyDetails.java - Displays journey details for a passenger based on their PNR number.
 * This class provides a GUI where the user enters a PNR number and views 
 * corresponding reservation details fetched from the database.
 */

package airlinemanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import net.proteanit.sql.DbUtils;

public class JourneyDetails extends JFrame implements ActionListener {
    
    // GUI Components
    JTable table;        // To display journey details in tabular form
    JTextField pnr;      // Input field for entering PNR
    JButton show;        // Button to fetch and show details
    
    // Constructor: sets up the UI
    public JourneyDetails() {
        
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);  // Absolute positioning of components
        
        // Label for PNR input
        JLabel lblpnr = new JLabel("PNR Details");
        lblpnr.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblpnr.setBounds(50, 50, 100, 25);
        add(lblpnr);
        
        // Text field to enter PNR number
        pnr = new JTextField();
        pnr.setBounds(160, 50, 120, 25);
        add(pnr);
        
        // Button to fetch journey details
        show = new JButton("Show Details");
        show.setBackground(Color.BLACK);
        show.setForeground(Color.WHITE);
        show.setBounds(290, 50, 120, 25);
        show.addActionListener(this);
        add(show);
        
        // Table to display journey details
        table = new JTable();
        
        // Adding table inside a scroll pane
        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(0, 100, 800, 150);
        jsp.setBackground(Color.WHITE);
        add(jsp);
        
        // Frame settings
        setSize(800, 600);
        setLocation(400, 150);
        setVisible(true);
    }
    
    // Handles button click event
    public void actionPerformed(ActionEvent ae) {
        try {
            Conn conn = new Conn();
            
            // SQL query to fetch journey details based on entered PNR
            ResultSet rs = conn.s.executeQuery("select * from reservation where PNR = '"+pnr.getText()+"'");
            
            // If no results found, show a message
            if (!rs.isBeforeFirst()) { 
                JOptionPane.showMessageDialog(null, "No Information Found");
                return;
            }
            
            // Populate JTable with result set data
            table.setModel(DbUtils.resultSetToTableModel(rs));
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Main method to run the class independently
    public static void main(String[] args) {
        new JourneyDetails();
    }
}
