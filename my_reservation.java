/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package printing_reservation_system;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Mario Guingab
 */
public class my_reservation extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(my_reservation.class.getName());
    
    private void loadReservationDashboard() {
        java.util.Date selectedDate = dateChooser.getDate();

        System.out.println("Selected Date: " + selectedDate);

        if (selectedDate != null) {
            System.out.println(
                "SQL Date: " + new java.sql.Date(selectedDate.getTime())
            );
        }

        int userId = UserSession.getUserId();

        String countSQL =
                "SELECT "
                + "COUNT(*) AS total, "
                + "COALESCE(SUM(status_reservation = 'Pending'), 0) AS pending, "
                + "COALESCE(SUM(status_reservation = 'Approved'), 0) AS approved, "
                + "COALESCE(SUM(status_reservation = 'Rejected'), 0) AS rejected, "
                + "COALESCE(SUM(status_reservation = 'Claimed'), 0) AS completed "
                + "FROM reservations "
                + "WHERE user_id = ?";

        String tableSQL =
                "SELECT reservation_ID, selected_date, file_name, service, status_reservation "
                + "FROM reservations "
                + "WHERE user_id = ? "
                + "ORDER BY selected_date DESC";

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement pst = conn.prepareStatement(countSQL)) {

                pst.setInt(1, userId);

                try (ResultSet rs = pst.executeQuery()) {

                    if (rs.next()) {

                        int total = rs.getInt("total");
                        int pending = rs.getInt("pending");
                        int approved = rs.getInt("approved");
                        int rejected = rs.getInt("rejected");
                        int completed = rs.getInt("completed");

                        total_count.setText(String.valueOf(total));
                        approved_count.setText(String.valueOf(approved));
                        pending_count.setText(String.valueOf(pending));
                        rejected_count.setText(String.valueOf(rejected));
                        completed_count.setText(String.valueOf(completed));
                    }
                }
            }
            try (PreparedStatement pst = conn.prepareStatement(tableSQL)) {

                pst.setInt(1, userId);

                try (ResultSet rs = pst.executeQuery()) {

                    DefaultTableModel model =
                            (DefaultTableModel) table.getModel();

                    model.setRowCount(0);

                    while (rs.next()) {

                        Object[] row = {
                            rs.getInt("reservation_ID"),
                            rs.getDate("selected_date"),
                            rs.getString("file_name"),
                            rs.getString("service"),
                            rs.getString("status_reservation"),
                            "View"
                        };

                        model.addRow(row);
                    }
                }

                table.getColumnModel()
                        .getColumn(5)
                        .setCellRenderer(new ButtonRenderer());

                table.getColumnModel()
                        .getColumn(5)
                        .setCellEditor(new ButtonEditor(this));
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load reservations: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
  
    private void searchReservations() {

        int userId = UserSession.getUserId();

        String keyword = search.getText().trim();

        if (keyword.equals("Enter by file name...")) {
            keyword = "";
        }

        java.util.Date selectedDate = dateChooser.getDate();

        String reservationStatus =
            jComboBox1.getSelectedItem().toString();

        String paymentStatus =
            jComboBox2.getSelectedItem().toString();

        String printingStatus =
            jComboBox3.getSelectedItem().toString();

        String dateOrder =
            jComboBox5.getSelectedItem().toString();

        String priceOrder =
            jComboBox6.getSelectedItem().toString();

        StringBuilder sql = new StringBuilder(
            "SELECT reservation_ID, selected_date, file_name, " +
            "service, status_reservation " +
            "FROM reservations WHERE user_id = ? "
        );

        if (!keyword.isEmpty()) {
            sql.append("AND file_name LIKE ? ");
        }

        if (selectedDate != null) {
            sql.append("AND selected_date = ? ");
        }

        if (!reservationStatus.equals("All Reservations")) {
            sql.append("AND status_reservation = ? ");
        }

        if (!paymentStatus.equals("All Payments")) {
            sql.append("AND payment_status = ? ");
        }

        if (!printingStatus.equals("All Printing Status")) {
            sql.append("AND printing_status = ? ");
        }
        
        String dateDirection =
            dateOrder.equals("Oldest Date First") ? "ASC" : "DESC";

        String priceDirection =
            priceOrder.equals("Lowest Total First") ? "ASC" : "DESC";

        sql.append("ORDER BY selected_date ")
           .append(dateDirection)
           .append(", total ")
           .append(priceDirection);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst =
                 conn.prepareStatement(sql.toString())) {

            int index = 1;

            pst.setInt(index++, userId);

            if (!keyword.isEmpty()) {
                pst.setString(index++, "%" + keyword + "%");
            }

            if (selectedDate != null) {
                pst.setDate(
                    index++,
                    new java.sql.Date(selectedDate.getTime())
                );
            }

            if (!reservationStatus.equals("All Reservations")) {
                pst.setString(index++, reservationStatus);
            }

            if (!paymentStatus.equals("All Payments")) {
                pst.setString(index++, paymentStatus);
            }

            if (!printingStatus.equals("All Printing Status")) {
                pst.setString(index++, printingStatus);
            }

            DefaultTableModel model =
                (DefaultTableModel) table.getModel();

            java.util.List<Object[]> rows =
                new java.util.ArrayList<>();

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[] {
                        rs.getInt("reservation_ID"),
                        rs.getDate("selected_date"),
                        rs.getString("file_name"),
                        rs.getString("service"),
                        rs.getString("status_reservation"),
                        "View"
                    });
                }
            }

            model.setRowCount(0);

            for (Object[] row : rows) {
                model.addRow(row);
            }

            table.getColumnModel().getColumn(5)
                .setCellRenderer(new ButtonRenderer());

            table.getColumnModel().getColumn(5)
                .setCellEditor(new ButtonEditor(this));

        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                this,
                "Failed to filter reservations: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refreshReservations() {
        searchReservations();
    }


    public my_reservation() {
        initComponents();
        String firstName = UserSession.getFirstName();

        if (firstName != null && !firstName.isEmpty()) {
            String userName = firstName.substring(0, 1).toUpperCase()
                    + firstName.substring(1).toLowerCase();

            name.setText("Hi, " + userName);
        }

        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        
        search.setText("Enter by file name...");
        search.setForeground(Color.GRAY);

        loadReservationDashboard();
        searchReservations();
        
        jComboBox1.addActionListener(e -> searchReservations());
        jComboBox2.addActionListener(e -> searchReservations());
        jComboBox3.addActionListener(e -> searchReservations());
        jComboBox5.addActionListener(e -> searchReservations());
        jComboBox6.addActionListener(e -> searchReservations());

        search.getDocument().addDocumentListener(
            new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    refreshReservations();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    refreshReservations();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    refreshReservations();
                }
            }
        );

        dateChooser.getDateEditor().addPropertyChangeListener(
            "date",
            evt -> refreshReservations()
        );
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        name = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        completed_count = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        pending_count4 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        total_count = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        pending_count = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        approved_count = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        rejected_count = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        pending_count6 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        search = new javax.swing.JTextField();
        dateChooser = new com.toedter.calendar.JDateChooser();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jComboBox6 = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 51));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/home.png"))); // NOI18N
        jButton1.setText("My Dashboard");
        jButton1.setContentAreaFilled(false);
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.setMargin(new java.awt.Insets(2, 35, 3, 14));
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/reservation.png"))); // NOI18N
        jButton2.setText("My Reservation");
        jButton2.setContentAreaFilled(false);
        jButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton2.setMargin(new java.awt.Insets(2, 35, 3, 14));
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/plus.png"))); // NOI18N
        jButton3.setText("New Reservation");
        jButton3.setContentAreaFilled(false);
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.setMargin(new java.awt.Insets(2, 35, 3, 14));
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/user.png"))); // NOI18N
        jButton4.setText("Profile");
        jButton4.setContentAreaFilled(false);
        jButton4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton4.setMargin(new java.awt.Insets(2, 35, 3, 14));
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/angels_4_logo_156x156.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Fast . Reliable . Quality");
        jLabel3.setToolTipText("");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Your Trusted Quality");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Printing Services");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jLabel4)))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 238, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(25, 25, 25))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        name.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        name.setForeground(new java.awt.Color(0, 51, 51));
        name.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/user-blank.png"))); // NOI18N
        name.setText("Hi, User");

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton6.setForeground(new java.awt.Color(0, 51, 51));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/logout.png"))); // NOI18N
        jButton6.setText("LOGOUT");
        jButton6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 51)));
        jButton6.setContentAreaFilled(false);
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton6.addActionListener(this::jButton6ActionPerformed);

        jPanel8.setBackground(new java.awt.Color(0, 51, 51));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("My Reservation");
        jPanel8.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("View and manage your printing reservations. ");
        jPanel8.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/booking.png"))); // NOI18N
        jPanel8.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(0, 51, 51));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Completed");
        jPanel5.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        completed_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        completed_count.setForeground(new java.awt.Color(255, 255, 255));
        completed_count.setText("0");
        jPanel5.add(completed_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/file.png"))); // NOI18N
        jPanel5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel10.setBackground(new java.awt.Color(0, 51, 51));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Rejected");
        jPanel10.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        pending_count4.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        pending_count4.setForeground(new java.awt.Color(255, 255, 255));
        pending_count4.setText("0");
        jPanel10.add(pending_count4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/file.png"))); // NOI18N
        jPanel10.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel5.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, 180, 100));

        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 0, 180, 100));

        jPanel9.setBackground(new java.awt.Color(0, 51, 51));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Total Reservation");
        jPanel9.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        total_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        total_count.setForeground(new java.awt.Color(255, 255, 255));
        total_count.setText("0");
        jPanel9.add(total_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/reserved.png"))); // NOI18N
        jPanel9.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel3.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 230, 100));

        jPanel6.setBackground(new java.awt.Color(0, 51, 51));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Pending");
        jPanel6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        pending_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        pending_count.setForeground(new java.awt.Color(255, 255, 255));
        pending_count.setText("0");
        jPanel6.add(pending_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/clock.png"))); // NOI18N
        jPanel6.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel3.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 190, 100));

        jPanel7.setBackground(new java.awt.Color(0, 51, 51));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Approved");
        jPanel7.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        approved_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        approved_count.setForeground(new java.awt.Color(255, 255, 255));
        approved_count.setText("0");
        jPanel7.add(approved_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/checked.png"))); // NOI18N
        jPanel7.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel3.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 0, 190, 100));

        jPanel11.setBackground(new java.awt.Color(0, 51, 51));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Rejected");
        jPanel11.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        rejected_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        rejected_count.setForeground(new java.awt.Color(255, 255, 255));
        rejected_count.setText("0");
        jPanel11.add(rejected_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/cross.png"))); // NOI18N
        jPanel11.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel12.setBackground(new java.awt.Color(0, 51, 51));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Rejected");
        jPanel12.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        pending_count6.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        pending_count6.setForeground(new java.awt.Color(255, 255, 255));
        pending_count6.setText("0");
        jPanel12.add(pending_count6, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/file.png"))); // NOI18N
        jPanel12.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel11.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, 180, 100));

        jPanel3.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, 180, 100));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/calendar.png"))); // NOI18N
        jLabel23.setText("YOUR RESERVATIONS");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel37.setText("Here are the details of your submitted reservations.");

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        jPanel13.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/magnifying-glass.png"))); // NOI18N
        jLabel24.setToolTipText("");

        search.setBorder(null);
        search.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchFocusLost(evt);
            }
        });
        search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(search))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dateChooser.setMaxSelectableDate(new java.util.Date(253370739682000L));
        dateChooser.setMinSelectableDate(new java.util.Date(-62135794718000L));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Reservations", "Pending", "Approved", "Rejected", "Claimed" }));
        jComboBox1.setBorder(null);
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Reservation ID", "Date", "Project Name", "Type", "Status", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setResizable(false);
            table.getColumnModel().getColumn(1).setResizable(false);
            table.getColumnModel().getColumn(2).setResizable(false);
            table.getColumnModel().getColumn(3).setResizable(false);
            table.getColumnModel().getColumn(4).setResizable(false);
            table.getColumnModel().getColumn(5).setResizable(false);
        }

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Payments", "Unpaid", "Partially Paid", "Fully Paid" }));
        jComboBox2.setBorder(null);
        jComboBox2.addActionListener(this::jComboBox2ActionPerformed);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Printing Status", "Not Started", "Printing", "Printed", "Ready to Claim" }));
        jComboBox3.setBorder(null);
        jComboBox3.addActionListener(this::jComboBox3ActionPerformed);

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Newest Date First", "Oldest Date First" }));
        jComboBox5.setBorder(null);
        jComboBox5.addActionListener(this::jComboBox5ActionPerformed);

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Highest Total First", "Lowest Total First" }));
        jComboBox6.setBorder(null);
        jComboBox6.addActionListener(this::jComboBox6ActionPerformed);

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(102, 0, 0));
        jButton5.setText("CANCEL RESERVATION");
        jButton5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 0, 0), 2, true));
        jButton5.setContentAreaFilled(false);
        jButton5.addActionListener(this::jButton5ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(128, 128, 128)
                .addComponent(name)
                .addGap(72, 72, 72)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel37)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(217, 217, 217)
                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))))
                .addGap(63, 63, 63))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(name)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(33, 33, 33)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel37))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(dateChooser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1)
                    .addComponent(jComboBox2)
                    .addComponent(jComboBox3)
                    .addComponent(jComboBox5)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {

            UserSession.clearSession();

            signin signinFrame = new signin();
            signinFrame.setVisible(true);
            signinFrame.setLocationRelativeTo(null);

            this.dispose();
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void searchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchFocusGained
        if (search.getText().equals("Enter by file name...")) {
            search.setText("");
            search.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_searchFocusGained

    private void searchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchFocusLost
        if (search.getText().isEmpty()) {
            search.setText("Enter by file name...");
            search.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_searchFocusLost

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int state = this.getExtendedState();
        new_reservation new_reservation = new new_reservation();
        new_reservation.setExtendedState(state);
        new_reservation.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int state = this.getExtendedState();
        student_dashboard student_dashboard = new student_dashboard();
        student_dashboard.setExtendedState(state);
        student_dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int state = this.getExtendedState();
        user_profile profile = new user_profile();
        profile.setExtendedState(state);
        profile.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox5ActionPerformed

    private void searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_searchKeyReleased

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a reservation first.",
                "No Reservation Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        int reservationId = Integer.parseInt(
            table.getModel().getValueAt(modelRow, 0).toString()
        );

        String fileName = table.getModel().getValueAt(modelRow, 2).toString();

        int confirmPayment = JOptionPane.showConfirmDialog(
            this,
            "RESERVATION: " + fileName + "\n\n"
            + "NON-REFUNDABLE INITIAL DOWN PAYMENT\n\n"
            + "If you have already paid the initial down payment,\n"
            + "please be informed that it is non-refundable.\n\n"
            + "This policy is implemented to prevent misuse of\n"
            + "the system and to cover the administrative work\n"
            + "and resources required to process each request.\n\n"
            + "It also ensures that the administration's time\n"
            + "and effort in reviewing every reservation are\n"
            + "respected.\n\n"
            + "Thank you for your understanding.\n\n"
            + "Do you understand and want to continue?",
            "Down Payment Policy",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmPayment != JOptionPane.YES_OPTION) {
            return;
        }

        int confirmDelete = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this reservation?\n\n"
            + "File Name: " + fileName + "\n"
            + "Reservation ID: " + reservationId + "\n\n"
            + "Your initial down payment will not be refunded.\n\n"
            + "This action cannot be undone.\n\n"
            + "Please confirm your choice.",
            "Confirm Reservation Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmDelete != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM reservations "
                   + "WHERE reservation_ID = ? AND user_id = ?";

        try (
            java.sql.Connection con = DatabaseConnection.getConnection();
            java.sql.PreparedStatement pst = con.prepareStatement(sql)
        ) {
            pst.setInt(1, reservationId);
            pst.setInt(2, UserSession.getUserId());

            int deleted = pst.executeUpdate();

            if (deleted > 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Reservation deleted successfully!\n\n"
                    + "File Name: " + fileName,
                    "Cancellation Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );

                loadReservationDashboard();
                searchReservations();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Reservation not found or you are not authorized to delete it.",
                    "Cancellation Failed",
                    JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Error deleting reservation: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new my_reservation().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel approved_count;
    private javax.swing.JLabel completed_count;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel name;
    private javax.swing.JLabel pending_count;
    private javax.swing.JLabel pending_count4;
    private javax.swing.JLabel pending_count6;
    private javax.swing.JLabel rejected_count;
    private javax.swing.JTextField search;
    private javax.swing.JTable table;
    private javax.swing.JLabel total_count;
    // End of variables declaration//GEN-END:variables
}
