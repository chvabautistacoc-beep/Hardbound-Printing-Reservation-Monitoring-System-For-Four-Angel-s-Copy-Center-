/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package printing_reservation_system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class admin_dashboard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(admin_dashboard.class.getName());

    /**
     * Creates new form admin_dashboard
     */

    private void loadReservationDashboard() {
        String countSQL =
                "SELECT "
                + "COUNT(*) AS total, "
                + "COALESCE(SUM(status_reservation = 'Pending'), 0) AS pending, "
                + "COALESCE(SUM(status_reservation = 'Approved'), 0) AS approved, "
                + "COALESCE(SUM(status_reservation = 'Rejected'), 0) AS rejected, "
                + "COALESCE(SUM(status_reservation = 'Claimed'), 0) AS completed "
                + "FROM reservations";

        String tableSQL =
            "SELECT "
            + "r.reservation_ID, "
            + "r.selected_date, "
            + "r.file_name, "
            + "CONCAT("
            + "UPPER(LEFT(u.f_name, 1)), LOWER(SUBSTRING(u.f_name, 2)), ' ', "
            + "UPPER(LEFT(u.l_name, 1)), LOWER(SUBSTRING(u.l_name, 2))"
            + ") AS customer_name, "
            + "r.service, "
            + "r.status_reservation "
            + "FROM reservations r "
            + "INNER JOIN tbl_users u ON r.user_id = u.user_id "
            + "ORDER BY r.selected_date DESC, r.reservation_ID DESC";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement pst = conn.prepareStatement(countSQL);
                 ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    total_count.setText(rs.getString("total"));
                    pending_count.setText(rs.getString("pending"));
                    approved_count.setText(rs.getString("approved"));
                    rejected_count.setText(rs.getString("rejected"));
                    completed_count.setText(rs.getString("completed"));
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(tableSQL);
                 ResultSet rs = pst.executeQuery()) {

                DefaultTableModel model =
                        (DefaultTableModel) table.getModel();

                model.setRowCount(0);

                while (rs.next()) {

                    Object[] row = {
                        rs.getInt("reservation_ID"),
                        rs.getDate("selected_date"),
                        rs.getString("file_name"),
                        rs.getString("customer_name"),
                        rs.getString("service"),
                        rs.getString("status_reservation"),
                        "View"
                    };

                    model.addRow(row);
                    table.getColumnModel().getColumn(6)
                            .setCellRenderer(new ButtonRenderer());

                    table.getColumnModel().getColumn(6)
                            .setCellEditor(new AdminButtonEditor(this));
                }
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
    
    private void checkExpiredCashReservations() {

        String sql =
            "UPDATE reservations " +
            "SET status_reservation = 'Rejected', " +
            "admin_note_rejection = ? " +
            "WHERE payment_method = 'Cash' " +
            "AND payment_status = 'Unpaid' " +
            "AND status_reservation NOT IN ('Rejected', 'Claimed') " +
            "AND created_at <= NOW() - INTERVAL 30 MINUTE";

        String reason = "You haven't paid your initial down payment.";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, reason);

            int updated = pst.executeUpdate();

            if (updated > 0) {
                loadReservationDashboard();
                System.out.println(
                    updated + " expired cash reservation(s) rejected."
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Automatic rejection failed:\n" + e.getMessage(),
                "SQL Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    public admin_dashboard() {
        initComponents();
        table.setRowHeight(30);
        loadReservationDashboard();
        
        checkExpiredCashReservations();
        javax.swing.Timer reservationTimer =
                new javax.swing.Timer(60_000, e -> {
                    checkExpiredCashReservations();
                });

        reservationTimer.start();
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
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        total_count = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel82 = new javax.swing.JLabel();
        pending_count = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel84 = new javax.swing.JLabel();
        rejected_count = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel86 = new javax.swing.JLabel();
        completed_count = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        approved_count = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        search1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel23 = new javax.swing.JPanel();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1507, 711));

        jPanel1.setBackground(new java.awt.Color(0, 51, 51));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/home.png"))); // NOI18N
        jButton1.setText("Admin Dashboard");
        jButton1.setContentAreaFilled(false);
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.setMargin(new java.awt.Insets(2, 35, 3, 14));
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 272, -1));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/reservation.png"))); // NOI18N
        jButton2.setText(" Manage Requests");
        jButton2.setContentAreaFilled(false);
        jButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton2.setMargin(new java.awt.Insets(2, 35, 3, 14));
        jButton2.addActionListener(this::jButton2ActionPerformed);
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 272, -1));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/plus.png"))); // NOI18N
        jButton3.setText(" Reservation Log");
        jButton3.setContentAreaFilled(false);
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.setMargin(new java.awt.Insets(2, 35, 3, 14));
        jButton3.addActionListener(this::jButton3ActionPerformed);
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 297, 272, -1));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/user.png"))); // NOI18N
        jButton4.setText("Customers");
        jButton4.setContentAreaFilled(false);
        jButton4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton4.setMargin(new java.awt.Insets(2, 35, 3, 14));
        jButton4.addActionListener(this::jButton4ActionPerformed);
        jPanel1.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 334, 272, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/angels_4_logo_156x156.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Fast . Reliable . Quality");
        jLabel3.setToolTipText("");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 630, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Your Trusted Quality");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 660, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Printing Services");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 690, -1, -1));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        jPanel17.setBackground(new java.awt.Color(0, 51, 51));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel80.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel80.setForeground(new java.awt.Color(255, 255, 255));
        jLabel80.setText("Total Request's");
        jPanel17.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        total_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        total_count.setForeground(new java.awt.Color(255, 255, 255));
        total_count.setText("0");
        jPanel17.add(total_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel81.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/reserved.png"))); // NOI18N
        jPanel17.add(jLabel81, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel18.setBackground(new java.awt.Color(0, 51, 51));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel82.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel82.setForeground(new java.awt.Color(255, 255, 255));
        jLabel82.setText("Pending");
        jPanel18.add(jLabel82, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        pending_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        pending_count.setForeground(new java.awt.Color(255, 255, 255));
        pending_count.setText("0");
        jPanel18.add(pending_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel83.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/clock.png"))); // NOI18N
        jPanel18.add(jLabel83, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel19.setBackground(new java.awt.Color(0, 51, 51));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel84.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel84.setForeground(new java.awt.Color(255, 255, 255));
        jLabel84.setText("Rejected");
        jPanel19.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        rejected_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        rejected_count.setForeground(new java.awt.Color(255, 255, 255));
        rejected_count.setText("0");
        jPanel19.add(rejected_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel85.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/cross.png"))); // NOI18N
        jPanel19.add(jLabel85, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel20.setBackground(new java.awt.Color(0, 51, 51));
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel86.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel86.setForeground(new java.awt.Color(255, 255, 255));
        jLabel86.setText("Completed Projects");
        jPanel20.add(jLabel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        completed_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        completed_count.setForeground(new java.awt.Color(255, 255, 255));
        completed_count.setText("0");
        jPanel20.add(completed_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel87.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/file.png"))); // NOI18N
        jPanel20.add(jLabel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jPanel21.setBackground(new java.awt.Color(0, 51, 51));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel88.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(255, 255, 255));
        jLabel88.setText("Approved");
        jPanel21.add(jLabel88, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        approved_count.setFont(new java.awt.Font("Segoe UI Black", 0, 30)); // NOI18N
        approved_count.setForeground(new java.awt.Color(255, 255, 255));
        approved_count.setText("0");
        jPanel21.add(approved_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, 40));

        jLabel89.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/checked.png"))); // NOI18N
        jPanel21.add(jLabel89, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        jLabel90.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel90.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/calendar.png"))); // NOI18N
        jLabel90.setText("ALL REQUESTS");

        jLabel91.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel91.setText("View and manage all reservation requests from clients and projects.");

        search1.setBorder(null);
        search1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                search1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                search1FocusLost(evt);
            }
        });

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Reservation ID", "Date", "Project Name", "Customer Name", "Type", "Status", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
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
            table.getColumnModel().getColumn(6).setResizable(false);
        }

        jPanel23.setBackground(new java.awt.Color(0, 51, 51));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel93.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel93.setForeground(new java.awt.Color(255, 255, 255));
        jLabel93.setText("Admin Dashboard");
        jPanel23.add(jLabel93, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, -1, -1));

        jLabel94.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/booking.png"))); // NOI18N
        jPanel23.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel79.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(255, 255, 255));
        jLabel79.setText("Manage reservations and monitor all printing services.");
        jPanel23.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, -1, -1));

        jLabel77.setFont(new java.awt.Font("Segoe UI Emoji", 2, 18)); // NOI18N
        jLabel77.setForeground(new java.awt.Color(255, 255, 255));
        jLabel77.setText("Welcome to");
        jPanel23.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 110, 35));

        jButton9.setBackground(new java.awt.Color(0, 51, 51));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("LOGOUT");
        jButton9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        jButton9.setContentAreaFilled(false);
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton9.setOpaque(true);
        jButton9.addActionListener(this::jButton9ActionPerformed);
        jPanel23.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 50, 150, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Hello, Admin");
        jPanel23.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 50, -1, -1));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(359, 359, 359)
                        .addComponent(search1)
                        .addGap(687, 687, 687))
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel91)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1022, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jLabel90)
                .addGap(0, 0, 0)
                .addComponent(jLabel91)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(search1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 1069, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int state = this.getExtendedState();
        requests manage_request = new requests();
        manage_request.setExtendedState(state);
        manage_request.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int state = this.getExtendedState();
        reservation_log reservation_log = new reservation_log();
        reservation_log.setExtendedState(state);
        reservation_log.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int state = this.getExtendedState();
        customer_list customer = new customer_list();
        customer.setExtendedState(state);
        customer.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void search1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_search1FocusGained

    }//GEN-LAST:event_search1FocusGained

    private void search1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_search1FocusLost
        
    }//GEN-LAST:event_search1FocusLost

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
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
    }//GEN-LAST:event_jButton9ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new admin_dashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel approved_count;
    private javax.swing.JLabel completed_count;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel pending_count;
    private javax.swing.JLabel rejected_count;
    private javax.swing.JTextField search1;
    private javax.swing.JTable table;
    private javax.swing.JLabel total_count;
    // End of variables declaration//GEN-END:variables
}
