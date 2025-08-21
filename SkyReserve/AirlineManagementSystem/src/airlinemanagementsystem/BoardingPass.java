package airlinemanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BoardingPass extends JFrame {

    public BoardingPass(String pnr) {
        setTitle("Boarding Pass");
        setSize(600, 500);
        setLocation(300, 150);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        try {
            Conn c = new Conn();
            String query = "SELECT * FROM reservation WHERE PNR = ?";
            PreparedStatement ps = c.c.prepareStatement(query);
            ps.setString(1, pnr);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String details =
                        "--------------------------------------------------\n" +
                        "                 AIR INDIA BOARDING PASS          \n" +
                        "--------------------------------------------------\n\n" +
                        "PNR          : " + rs.getString("PNR") + "\n" +
                        "Passenger    : " + rs.getString("name") + "\n" +
                        "Aadhar       : " + rs.getString("aadhar") + "\n" +
                        "Nationality  : " + rs.getString("nationality") + "\n\n" +
                        "Flight Code  : " + rs.getString("flightcode") + "\n" +
                        "Flight Name  : " + rs.getString("flightname") + "\n" +
                        "From         : " + rs.getString("source") + "\n" +       // ✅ fixed
                        "To           : " + rs.getString("destination") + "\n" +  // ✅ fixed
                        "Date         : " + rs.getString("ddate") + "\n" +        // ✅ fixed
                        "Seat No      : " + rs.getString("seat_no") + "\n" +      // ✅ fixed
                        "Class        : " + rs.getString("ticket") + "\n" +       // ✅ fixed
                        "Price        : ₹" + rs.getInt("price") + "\n" +          // ✅ fixed
                        "Payment Mode : " + rs.getString("payment_mode") + "\n" + // ✅ fixed
                        "Ticket No    : " + rs.getString("ticket_no") + "\n" +
                        "--------------------------------------------------\n" +
                        "        Please arrive 2 hours before departure     \n" +
                        "--------------------------------------------------\n";

                textArea.setText(details);
            } else {
                JOptionPane.showMessageDialog(this, "No reservation found for PNR: " + pnr);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error fetching boarding pass: " + e.getMessage());
            e.printStackTrace();
        }

        setVisible(true);
    }

    public static void main(String[] args) {
        new BoardingPass("PNR-1755693745392");  // ✅ test with your real PNR
    }
}
