/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package printing_reservation_system;

import java.awt.Color;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Mario Guingab
 */
public class details_management_user extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(details_management_user.class.getName());

    /**
     * Creates new form details_management_user
     */
    
    private int user_id;
    private int reservation_ID;
    
    private void loadReservationDetails(int userId, int reservationID) {

        String sql = "SELECT * FROM reservations "
                   + "WHERE user_id = ? AND reservation_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, userId);
            pst.setInt(2, reservationID);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    printing_status.setText(
                        rs.getString("printing_status")
                    );

                    reservation_status.setText(
                        rs.getString("status_reservation").toUpperCase()
                    );

                    reservation_id.setText("#" +
                        String.valueOf(rs.getInt("reservation_ID"))
                    );

                    file_name.setText(rs.getString("file_name"));
                    service.setText(rs.getString("service"));

                    quality_color.setText(
                        rs.getString("quality") + " • " + rs.getString("color")
                    );

                    paper_size_type.setText(
                        rs.getString("paper_size") + " • " + rs.getString("paper_type")
                    );

                    orientation.setText(rs.getString("orientation"));

                    quantity.setText(
                        String.valueOf(rs.getInt("quantity"))
                    );

                    selected_date.setText(
                        String.valueOf(rs.getDate("selected_date"))
                    );

                    selected_time.setText(rs.getString("selected_time"));

                    additional_info.setText(
                        rs.getString("additional_info")
                    );

                    payment_method.setText(rs.getString("payment_method"));

                    referencee_number.setText(
                        rs.getString("referencee_number")
                    );

                    downpayment.setText(
                        String.format("%.2f", rs.getDouble("total") * 0.50)
                    );

                    payment_status.setText(rs.getString("payment_status"));

                    String adminNote = rs.getString("admin_note_rejection");

                    admin_rejection_note.setText(
                        adminNote == null || adminNote.trim().isEmpty()
                            ? "Not available yet."
                            : adminNote
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Reservation not found.",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load reservation details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private java.awt.Frame parentFrame;
    
    public details_management_user(
            java.awt.Frame parent,
            int user_id,
            int reservation_ID) {

        this.parentFrame = parent;
        this.user_id = user_id;
        this.reservation_ID = reservation_ID;

        setUndecorated(true);

        initComponents();
        
        additional_info.setEditable(false);
        additional_info.setForeground(Color.GRAY);
        additional_info.setLineWrap(true);
        additional_info.setWrapStyleWord(true);
        
        admin_rejection_note.setEditable(false);
        admin_rejection_note.setForeground(Color.GRAY);
        admin_rejection_note.setLineWrap(true);
        admin_rejection_note.setWrapStyleWord(true);
        
        
        setDefaultCloseOperation(
                javax.swing.JFrame.DO_NOTHING_ON_CLOSE
        );

        setLocationRelativeTo(parent);

        if (parentFrame != null) {
            parentFrame.setEnabled(false);
        }

        loadReservationDetails(user_id, reservation_ID);
    }
    
    @Override
    public void dispose() {

        if (parentFrame != null) {
            parentFrame.setEnabled(true);
            parentFrame.toFront();
            parentFrame.requestFocus();
        }

        super.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        reservation_id = new javax.swing.JLabel();
        printing_status = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel49 = new javax.swing.JLabel();
        file_name = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        service = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        paper_size_type = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        orientation = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        quality_color = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        quantity = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        selected_date = new javax.swing.JLabel();
        selected_time = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        payment_method = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        referencee_number = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        downpayment = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        payment_status = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        additional_info = new javax.swing.JTextArea();
        jButton8 = new javax.swing.JButton();
        reservation_status = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        admin_rejection_note = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel8.setBackground(new java.awt.Color(0, 51, 51));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Reservation Details");
        jPanel8.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Four Angel Copy Printing System");
        jPanel8.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/printing.png"))); // NOI18N
        jPanel8.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(102, 102, 102));
        jLabel45.setText("RESERVATION ID");

        reservation_id.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        reservation_id.setForeground(new java.awt.Color(0, 51, 51));
        reservation_id.setText("RESERVATION ID");

        printing_status.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        printing_status.setForeground(new java.awt.Color(51, 51, 0));
        printing_status.setText("Not Started");

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(0, 51, 51));
        jLabel48.setText("1. File & Service Details");

        jSeparator1.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator1.setForeground(new java.awt.Color(153, 153, 153));

        jLabel49.setForeground(new java.awt.Color(102, 102, 102));
        jLabel49.setText("File Name");

        file_name.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        file_name.setForeground(new java.awt.Color(0, 51, 51));
        file_name.setText("File_Name");

        jLabel51.setForeground(new java.awt.Color(102, 102, 102));
        jLabel51.setText("Service Type");

        service.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        service.setForeground(new java.awt.Color(0, 51, 51));
        service.setText("Document Printing");

        jLabel53.setForeground(new java.awt.Color(102, 102, 102));
        jLabel53.setText("Paper Size / Type");

        paper_size_type.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        paper_size_type.setForeground(new java.awt.Color(0, 51, 51));
        paper_size_type.setText("A4");

        jLabel55.setForeground(new java.awt.Color(102, 102, 102));
        jLabel55.setText("Orientation");

        orientation.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        orientation.setForeground(new java.awt.Color(0, 51, 51));
        orientation.setText("Portrait");

        jLabel57.setForeground(new java.awt.Color(102, 102, 102));
        jLabel57.setText("Quality / Color");

        quality_color.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        quality_color.setForeground(new java.awt.Color(0, 51, 51));
        quality_color.setText("Standard / B&W");

        jLabel59.setForeground(new java.awt.Color(102, 102, 102));
        jLabel59.setText("Quantity");

        quantity.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        quantity.setForeground(new java.awt.Color(0, 51, 51));
        quantity.setText("50 copies");

        jSeparator2.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator2.setForeground(new java.awt.Color(153, 153, 153));

        jLabel61.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(0, 51, 51));
        jLabel61.setText("2. Schedule & Pickup");

        jLabel62.setForeground(new java.awt.Color(102, 102, 102));
        jLabel62.setText("Pickup Date");

        jLabel63.setForeground(new java.awt.Color(102, 102, 102));
        jLabel63.setText("Pickup Time");

        selected_date.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        selected_date.setForeground(new java.awt.Color(0, 51, 51));
        selected_date.setText("October 15, 2026");

        selected_time.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        selected_time.setForeground(new java.awt.Color(0, 51, 51));
        selected_time.setText("2:00 PM");

        jSeparator3.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator3.setForeground(new java.awt.Color(153, 153, 153));

        jLabel66.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(0, 51, 51));
        jLabel66.setText("3. Payment Information");

        jLabel67.setForeground(new java.awt.Color(102, 102, 102));
        jLabel67.setText("Payment Method");

        payment_method.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        payment_method.setForeground(new java.awt.Color(0, 51, 51));
        payment_method.setText("Gcash");

        jLabel69.setForeground(new java.awt.Color(102, 102, 102));
        jLabel69.setText("Reference Number");

        referencee_number.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        referencee_number.setForeground(new java.awt.Color(0, 51, 51));
        referencee_number.setText("123456789012");

        jLabel71.setForeground(new java.awt.Color(102, 102, 102));
        jLabel71.setText("Down Payment");

        downpayment.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        downpayment.setForeground(new java.awt.Color(0, 51, 51));
        downpayment.setText("₱125.00");

        jLabel73.setForeground(new java.awt.Color(102, 102, 102));
        jLabel73.setText("Payment Status");

        payment_status.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        payment_status.setForeground(new java.awt.Color(0, 51, 51));
        payment_status.setText("N/A");

        jLabel75.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(0, 51, 51));
        jLabel75.setText("4. Additional Information from Client");

        additional_info.setColumns(20);
        additional_info.setRows(5);
        jScrollPane1.setViewportView(additional_info);

        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(0, 51, 51));
        jButton8.setText("GO BACK");
        jButton8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 51), 2, true));
        jButton8.setContentAreaFilled(false);
        jButton8.addActionListener(this::jButton8ActionPerformed);

        reservation_status.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reservation_status.setForeground(new java.awt.Color(51, 51, 0));
        reservation_status.setText("APPROVED");

        jLabel76.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(0, 51, 51));
        jLabel76.setText("5. Admin Rejection Note");

        admin_rejection_note.setColumns(20);
        admin_rejection_note.setRows(5);
        jScrollPane2.setViewportView(admin_rejection_note);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel61)
                            .addComponent(jLabel62)
                            .addComponent(selected_date)
                            .addComponent(jLabel63)
                            .addComponent(selected_time))
                        .addGap(197, 197, 197))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(printing_status))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(reservation_id)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(reservation_status))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator2)
                                    .addComponent(jSeparator3)
                                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1)
                                    .addComponent(jLabel76)
                                    .addComponent(jLabel66)
                                    .addComponent(jLabel75)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(file_name)
                                            .addComponent(jLabel53)
                                            .addComponent(paper_size_type)
                                            .addComponent(jLabel49)
                                            .addComponent(jLabel57)
                                            .addComponent(quality_color))
                                        .addGap(80, 80, 80)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel51)
                                            .addComponent(orientation)
                                            .addComponent(jLabel55)
                                            .addComponent(service)
                                            .addComponent(jLabel59)
                                            .addComponent(quantity)))
                                    .addComponent(jScrollPane2)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel67)
                                            .addComponent(payment_method))
                                        .addGap(78, 78, 78)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel71)
                                            .addComponent(downpayment))
                                        .addGap(46, 46, 46)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(referencee_number)
                                            .addComponent(jLabel69))
                                        .addGap(87, 87, 87)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(payment_status)
                                            .addComponent(jLabel73))))))
                        .addGap(22, 22, 22))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(printing_status))
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(reservation_id)
                    .addComponent(reservation_status))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jLabel61))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel51)
                            .addComponent(jLabel49))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(file_name)
                            .addComponent(service)
                            .addComponent(selected_date)))
                    .addComponent(jLabel62))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel53)
                            .addComponent(jLabel55))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(paper_size_type)
                            .addComponent(orientation))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel59)
                            .addComponent(jLabel57))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(quality_color)
                            .addComponent(quantity))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel63)
                        .addGap(0, 0, 0)
                        .addComponent(selected_time)
                        .addGap(44, 44, 44)))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(jLabel71)
                    .addComponent(jLabel69)
                    .addComponent(jLabel73))
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(payment_method)
                    .addComponent(downpayment)
                    .addComponent(referencee_number)
                    .addComponent(payment_status))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel75)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton8ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new details_management_user(null, 0, 0).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea additional_info;
    private javax.swing.JTextArea admin_rejection_note;
    private javax.swing.JLabel downpayment;
    private javax.swing.JLabel file_name;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel orientation;
    private javax.swing.JLabel paper_size_type;
    private javax.swing.JLabel payment_method;
    private javax.swing.JLabel payment_status;
    private javax.swing.JLabel printing_status;
    private javax.swing.JLabel quality_color;
    private javax.swing.JLabel quantity;
    private javax.swing.JLabel referencee_number;
    private javax.swing.JLabel reservation_id;
    private javax.swing.JLabel reservation_status;
    private javax.swing.JLabel selected_date;
    private javax.swing.JLabel selected_time;
    private javax.swing.JLabel service;
    // End of variables declaration//GEN-END:variables
}
