package airlinemanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// The Login class is a Swing-based GUI for logging into the Airline Management System.
// It extends JFrame to create a window and implements ActionListener to handle button actions.
public class Login extends JFrame implements ActionListener {
    JButton submit, reset, close;   // Buttons for actions
    JTextField tfusername;         // Input field for username
    JPasswordField tfpassword;     // Input field for password (hidden characters)

    public Login() {
        // Set background color of the window
        getContentPane().setBackground(Color.WHITE);
        setLayout(null); // Absolute positioning (not recommended, but works here)

        // Username Label
        JLabel lblusername = new JLabel("Username");
        lblusername.setBounds(20, 20, 100, 20);
        add(lblusername);

        // Username Input Field
        tfusername = new JTextField();
        tfusername.setBounds(130, 20, 200, 20);
        add(tfusername);

        // Password Label
        JLabel lblpassword = new JLabel("Password");
        lblpassword.setBounds(20, 60, 100, 20);
        add(lblpassword);

        // Password Input Field
        tfpassword = new JPasswordField();
        tfpassword.setBounds(130, 60, 200, 20);
        add(tfpassword);

        // Reset Button (clears inputs)
        reset = new JButton("Reset");
        reset.setBounds(40, 120, 120, 20);
        reset.addActionListener(this);
        add(reset);

        // Submit Button (attempt login)
        submit = new JButton("Submit");
        submit.setBounds(190, 120, 120, 20);
        submit.addActionListener(this);
        add(submit);

        // Close Button (closes window)
        close = new JButton("Close");
        close.setBounds(120, 160, 120, 20);
        close.addActionListener(this);
        add(close);

        // Set window size, position, and make it visible
        setSize(400, 250);
        setLocation(600, 250);
        setVisible(true);
    }

    // Handles button clicks
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == submit) { // When Submit is clicked
            String username = tfusername.getText();
            String password = tfpassword.getText();  // ⚠ Using getText() for password is not secure, better use getPassword()

            try {
                Conn c = new Conn(); // Custom database connection class (not shown here)

                // ⚠ Vulnerable to SQL Injection - better use PreparedStatement
                String query = "select * from login where username = '" + username + "' and password = '" + password + "'";

                ResultSet rs = c.s.executeQuery(query);

                if (rs.next()) {
                    // Successful login → Open Home screen
                    new Home();
                    setVisible(false);
                } else {
                    // Invalid credentials
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password");
                    setVisible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ae.getSource() == close) { // Close button clicked
            setVisible(false);
        } else if (ae.getSource() == reset) { // Reset button clicked
            tfusername.setText("");
            tfpassword.setText("");
        }
    }

    // Entry point
    public static void main(String[] args) {
        new Login();
    }
}
