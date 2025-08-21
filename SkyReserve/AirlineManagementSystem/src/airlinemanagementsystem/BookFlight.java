package airlinemanagementsystem;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * BookFlight
 * - Loads flights
 * - Shows Source/Destination
 * - Filters available seats per flight
 * - Seat class (Economy/Business) -> price
 * - JDateChooser for journey date
 * - Seat reference image (seat.png)
 * - Inserts into reservation and auto-opens BoardingPass for the new PNR
 */
public class BookFlight extends JFrame implements ActionListener {

    // UI
    private Choice flightChoice, seatChoice, classChoice;
    private JTextField tfName, tfAadhar, tfNationality;
    private JLabel lblSrc, lblDes, lblPrice;
    private JButton bookBtn;
    private JDateChooser dateChooser;

    // Data
    private final Map<String, String[]> flightMap = new HashMap<>(); // code -> [src, des]

    public BookFlight() {
        setTitle("Book Flight");
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // Heading
        JLabel heading = new JLabel("Book Flight");
        heading.setBounds(420, 20, 300, 35);
        heading.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 26));
        heading.setForeground(Color.BLACK);
        add(heading);

        int L = 300, X = 450, W = 220, H = 26, GAP = 38, y = 80;

        addLabel("Name:", L, y); tfName = addTextField(X, y); y += GAP;
        addLabel("Aadhar:", L, y); tfAadhar = addTextField(X, y); y += GAP;
        addLabel("Nationality:", L, y); tfNationality = addTextField(X, y); y += GAP;

        addLabel("Flight:", L, y);
        flightChoice = new Choice();
        flightChoice.setBounds(X, y, W, H);
        add(flightChoice);
        y += GAP;

        lblSrc = addInfoLabel("Source: —", L, y); y += GAP;
        lblDes = addInfoLabel("Destination: —", L, y); y += GAP;

        addLabel("Seat:", L, y);
        seatChoice = new Choice();
        seatChoice.setBounds(X, y, W, H);
        add(seatChoice);
        y += GAP;

        addLabel("Class:", L, y);
        classChoice = new Choice();
        classChoice.add("Economy");
        classChoice.add("Business");
        classChoice.setBounds(X, y, W, H);
        add(classChoice);
        y += GAP;

        addLabel("Date:", L, y);
        dateChooser = new JDateChooser();
        dateChooser.setDate(new java.util.Date());
        dateChooser.setBounds(X, y, W, H);
        add(dateChooser);
        y += GAP;

        lblPrice = addInfoLabel("Price: ₹0", L, y);
        y += GAP + 10;

        bookBtn = new JButton("Book Flight");
        bookBtn.setBounds(X, y, 160, 32);
        bookBtn.addActionListener(this);
        add(bookBtn);

        // Seat reference image on the right
        JLabel imgLabel = new JLabel();
        imgLabel.setBounds(720, 120, 400, 300);
        ImageIcon icon = null;

        // 1) Try classpath (recommended path: src/airlinemanagementsystem/icons/seat.png)
        java.net.URL url = ClassLoader.getSystemResource("airlinemanagementsystem/icons/seat.png");
        if (url != null) {
            icon = new ImageIcon(url);
        } else {
            // 2) Fallback: project root or working dir
            java.io.File f1 = new java.io.File("src/airlinemanagementsystem/icons/seat.png");
            java.io.File f2 = new java.io.File("seat.png");
            if (f1.exists()) icon = new ImageIcon(f1.getAbsolutePath());
            else if (f2.exists()) icon = new ImageIcon(f2.getAbsolutePath());
        }
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } else {
            imgLabel.setText("Seat map: place seat.png under src/airlinemanagementsystem/icons/");
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        add(imgLabel);

        // Load flights and wire events
        loadFlights();

        flightChoice.addItemListener(e -> {
            String code = getSelectedFlightCode();
            if (code == null) return;
            String[] sd = flightMap.get(code);
            lblSrc.setText("Source: " + sd[0]);
            lblDes.setText("Destination: " + sd[1]);
            loadAvailableSeats(code);
            updatePrice();
        });

        seatChoice.addItemListener(e -> updatePrice());
        classChoice.addItemListener(e -> updatePrice());

        // Size/pos
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialize dependent controls
        if (flightChoice.getItemCount() > 0) {
            String code = getSelectedFlightCode();
            String[] sd = flightMap.get(code);
            lblSrc.setText("Source: " + sd[0]);
            lblDes.setText("Destination: " + sd[1]);
            loadAvailableSeats(code);
            updatePrice();
        }
    }

    // ---------- UI helpers ----------
    private void addLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
        l.setBounds(x, y, 140, 24);
        add(l);
    }

    private JTextField addTextField(int x, int y) {
        JTextField t = new JTextField();
        t.setBounds(x, y, 220, 26);
        add(t);
        return t;
    }

    private JLabel addInfoLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
        l.setBounds(x, y, 320, 24);
        add(l);
        return l;
    }

    // ---------- Data loaders ----------
    private void loadFlights() {
        flightChoice.removeAll();
        flightMap.clear();
        try {
            Conn c = new Conn();
            String query = "SELECT f_code, f_name, source, destination FROM flight";
            ResultSet rs = c.s.executeQuery(query);
            while (rs.next()) {
                String code = rs.getString("f_code");
                String name = rs.getString("f_name");
                String src = rs.getString("source");
                String des = rs.getString("destination");
                flightMap.put(code, new String[]{src, des});
                flightChoice.add(code + " - " + name);
            }
            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load flights: " + ex.getMessage());
        }
    }

    private void loadAvailableSeats(String flightCode) {
        seatChoice.removeAll();

        // Generate seat list A1..F10
        java.util.List<String> allSeats = new ArrayList<>();
        for (char row = 'A'; row <= 'F'; row++) {
            for (int num = 1; num <= 10; num++) {
                allSeats.add(row + String.valueOf(num));
            }
        }

        // Remove already booked seats for this flight
        try {
            Conn c = new Conn();
            String sql = "SELECT seat_no FROM reservation WHERE flightcode = ?";
            PreparedStatement ps = c.c.prepareStatement(sql);
            ps.setString(1, flightCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String booked = rs.getString("seat_no");
                allSeats.remove(booked);
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (String s : allSeats) {
            seatChoice.add(s);
        }
    }

    // ---------- Pricing ----------
    private void updatePrice() {
        String seat = seatChoice.getItemCount() > 0 ? seatChoice.getSelectedItem() : null;
        String cls = classChoice.getSelectedItem();
        int price = computePrice(seat, cls);
        lblPrice.setText("Price: ₹" + price);
    }

    private int computePrice(String seat, String seatClass) {
        int base = "Business".equals(seatClass) ? 8000 : 4000;
        if (seat == null) return base;

        // small seat-position premiums
        int premium = 0;
        if (seat.endsWith("1") || seat.endsWith("10")) premium += 500; // window-like ends
        else if (seat.endsWith("2") || seat.endsWith("9")) premium += 300;

        return base + premium;
    }

    private String getSelectedFlightCode() {
        if (flightChoice.getItemCount() == 0) return null;
        String item = flightChoice.getSelectedItem();
        if (item == null || !item.contains(" - ")) return null;
        return item.substring(0, item.indexOf(" - ")).trim();
    }

    // ---------- Booking ----------
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != bookBtn) return;

        String name = tfName.getText().trim();
        String aadhar = tfAadhar.getText().trim();
        String nationality = tfNationality.getText().trim();
        String flightCode = getSelectedFlightCode();
        if (flightCode == null) {
            JOptionPane.showMessageDialog(this, "Please select a flight.");
            return;
        }
        String flightName = flightChoice.getSelectedItem().split(" - ")[1].trim();
        String[] sd = flightMap.get(flightCode);
        String src = sd[0];
        String des = sd[1];
        String seatNo = seatChoice.getItemCount() > 0 ? seatChoice.getSelectedItem() : null;
        String seatClass = classChoice.getSelectedItem();
        String ddate = new SimpleDateFormat("dd-MMM-yyyy").format(dateChooser.getDate());
        int price = computePrice(seatNo, seatClass);

        if (name.isEmpty() || aadhar.isEmpty() || nationality.isEmpty() || seatNo == null || ddate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select a seat.");
            return;
        }

        String pnr = "PNR-" + System.currentTimeMillis();

        try {
            Conn c = new Conn();

            // Prepared insert with fallback for src/des vs source/destination
            String tpl = "INSERT INTO reservation "
                       + "(PNR, aadhar, name, nationality, flightname, flightcode, %s, %s, ddate, seat_no, price, payment_mode) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            boolean inserted = false;
            SQLException lastEx = null;

            String fromCol = "src";
            String toCol   = "des";

            for (int attempt = 0; attempt < 2 && !inserted; attempt++) {
                String sql = String.format(tpl, fromCol, toCol);
                try (PreparedStatement ps = c.c.prepareStatement(sql)) {
                    int i = 1;
                    ps.setString(i++, pnr);
                    ps.setString(i++, aadhar);
                    ps.setString(i++, name);
                    ps.setString(i++, nationality);
                    ps.setString(i++, flightName);
                    ps.setString(i++, flightCode);
                    ps.setString(i++, src);
                    ps.setString(i++, des);
                    ps.setString(i++, ddate);
                    ps.setString(i++, seatNo);
                    ps.setInt(i++, price);
                    ps.setString(i++, "Online");

                    ps.executeUpdate();
                    inserted = true;
                } catch (SQLException ex) {
                    lastEx = ex;
                    // 42S22 = Column not found (MySQL)
                    if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unknown column")) {
                        fromCol = "source";
                        toCol = "destination";
                        continue; // retry with alternative column names
                    }
                    throw ex; // different SQL error -> rethrow
                }
            }

            if (!inserted) {
                throw lastEx != null ? lastEx : new SQLException("Insert failed for unknown reason");
            }

            JOptionPane.showMessageDialog(this, "Seat booked successfully!\nPNR: " + pnr);
            // Remove booked seat immediately from dropdown
            loadAvailableSeats(flightCode);
            updatePrice();

            // Auto-open BoardingPass for this PNR
            try {
                new BoardingPass(pnr);
            } catch (Throwable t) {
                // fallback if only no-arg constructor exists
                // Auto-open BoardingPass for this PNR
                new BoardingPass(pnr);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while booking: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new BookFlight();
    }
}
