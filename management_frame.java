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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.JOptionPane;


/**
 *
 * @author Mario Guingab
 */
public class management_frame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(management_frame.class.getName());

    /**
     * Creates new form management_frame
     */
    
    private static final String ADMIN_NOTE_PLACEHOLDER =
        "Enter rejection reason (optional)...";

    private boolean isAdminNotePlaceholder = false; 
        private int reservation_ID;
        private String originalApproval;
        private String originalPayment;
        private String originalPrinting;
        private String originalAdminNote;



    private void showAdminNotePlaceholder() {
        admin_note.setText(ADMIN_NOTE_PLACEHOLDER);
        admin_note.setForeground(java.awt.Color.GRAY);
        isAdminNotePlaceholder = true;
    }



    private void setupAdminNotePlaceholder() {
        admin_note.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (isAdminNotePlaceholder) {
                    admin_note.setText("");
                    admin_note.setForeground(java.awt.Color.BLACK);
                    isAdminNotePlaceholder = false;
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (admin_note.getText().trim().isEmpty()) {
                    showAdminNotePlaceholder();
                }
            }
        });
    }

    private void validateAndUpdate() {
        String approval = (String) approval_status.getSelectedItem();
        String payment = (String) payment_status.getSelectedItem();
        String printing = (String) printing_status.getSelectedItem();

        if (approval == null || "----".equals(approval)) {
            JOptionPane.showMessageDialog(this,
                    "Please select an approval status.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            approval_status.requestFocus();
            return;
        }

        if (payment == null || "----".equals(payment)) {
            JOptionPane.showMessageDialog(this,
                    "Please select a payment status.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            payment_status.requestFocus();
            return;
        }

        if (printing == null || "----".equals(printing)) {
            JOptionPane.showMessageDialog(this,
                    "Please select a printing status.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            printing_status.requestFocus();
            return;
        }

        if ("Claimed".equalsIgnoreCase(approval)) {
            printing_status.setSelectedItem("Printed");
            printing_status.setEnabled(false);
        } else {
            printing_status.setSelectedItem("----");
            printing_status.setEnabled(true);
        }

        String note = admin_note.getText().trim();

        if (isAdminNotePlaceholder
                || note.equals(ADMIN_NOTE_PLACEHOLDER)
                || note.isEmpty()) {
            note = null;
        }

        if (!"Rejected".equalsIgnoreCase(approval)) {
            note = null;
        }

        String currentNote = note == null ? "" : note.trim();
        String savedNote = originalAdminNote == null
                ? ""
                : originalAdminNote.trim();

        boolean noChanges =
                java.util.Objects.equals(approval, originalApproval)
                && java.util.Objects.equals(payment, originalPayment)
                && java.util.Objects.equals(printing, originalPrinting)
                && java.util.Objects.equals(currentNote, savedNote);

        if (noChanges) {
            JOptionPane.showMessageDialog(this,
                    "No changes detected.",
                    "No Changes",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to save these changes?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "UPDATE reservations SET "
                + "status_reservation = ?, "
                + "payment_status = ?, "
                + "printing_status = ?, "
                + "admin_note_rejection = ?, "
                + "completed_date = CASE "
                + "WHEN ? = 'Claimed' THEN "
                + "COALESCE(completed_date, CURRENT_TIMESTAMP) "
                + "ELSE NULL END "
                + "WHERE reservation_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, approval);
            pst.setString(2, payment);
            pst.setString(3, printing);

            if (note == null) {
                pst.setNull(4, java.sql.Types.VARCHAR);
            } else {
                pst.setString(4, note);
            }

            pst.setString(5, approval);
            pst.setInt(6, reservation_ID);

            int updated = pst.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this,
                        "Reservation updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                if (parentFrame instanceof requests) {
                    ((requests) parentFrame).refreshReservations();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No reservation was updated. Please check the reservation ID.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to update reservation:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private String capitalize(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }

        name = name.trim();
        return name.substring(0, 1).toUpperCase()
                + name.substring(1).toLowerCase();
    }
    
    private void loadReservationDetails() {
        String sql = "SELECT "
                + "r.reservation_ID, r.user_id, r.file_name, "
                + "r.path_file_name, r.service, r.quality, "
                + "r.paper_size, r.paper_type, r.color, "
                + "r.binding, r.orientation, r.quantity, "
                + "r.selected_date, r.selected_time, "
                + "r.additional_info, r.total, "
                + "r.payment_method, r.referencee_number, "
                + "r.status_reservation, r.payment_status, "
                + "r.printing_status, r.admin_note_rejection, "
                + "u.f_name, u.l_name, u.email, u.phone_number "
                + "FROM reservations r "
                + "INNER JOIN tbl_users u ON r.user_id = u.user_id "
                + "WHERE r.reservation_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, reservation_ID);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    jLabel7.setText(
                            "Reservation ID: #" + rs.getInt("reservation_ID")
                    );

                    full_name.setText(
                            capitalize(rs.getString("f_name")) + " "
                            + capitalize(rs.getString("l_name"))
                    );

                    email.setText(safe(rs.getString("email")));
                    contact_number.setText(safe(rs.getString("phone_number")));

                    project_name.setText(safe(rs.getString("file_name")));
                    project_file.setText(safe(rs.getString("path_file_name")));

                    String service = rs.getString("service");
                    String binding = rs.getString("binding");

                    if ("Binding".equalsIgnoreCase(safe(service).trim())) {
                        service_type_with_binding_type.setText(
                                "Binding"
                                + (!safe(binding).trim().isEmpty()
                                && !"None".equalsIgnoreCase(safe(binding).trim())
                                ? " (" + binding + ")" : "")
                        );
                    } else {
                        service_type_with_binding_type.setText(safe(service));
                    }

                    specifications.setText(
                            safe(rs.getString("paper_size")) + ", "
                            + safe(rs.getString("paper_type")) + ", "
                            + safe(rs.getString("quality")) + ", "
                            + safe(rs.getString("color")) + ", "
                            + safe(rs.getString("orientation"))
                    );

                    quantity.setText(rs.getInt("quantity") + " pcs");

                    double amount = rs.getDouble("total");

                    total.setText(
                            NumberFormat.getCurrencyInstance(
                                    new Locale("en", "PH")
                            ).format(amount)
                    );

                    java.sql.Date selectedDate = rs.getDate("selected_date");

                    if (selectedDate != null) {
                        pick_up_date.setText(
                                new SimpleDateFormat("MMMM dd, yyyy")
                                        .format(selectedDate)
                        );
                    } else {
                        pick_up_date.setText("Not specified");
                    }

                    String selectedTime = rs.getString("selected_time");

                    time.setText(
                            selectedTime == null || selectedTime.trim().isEmpty()
                                    ? "Not specified"
                                    : selectedTime
                    );

                    payment_method.setText(
                            safe(rs.getString("payment_method"))
                    );

                    ref_num.setText(
                            safe(rs.getString("referencee_number"))
                    );

                    down_payment.setText(
                            NumberFormat.getCurrencyInstance(
                                    new Locale("en", "PH")
                            ).format(amount * 0.50)
                    );

                    additional_info.setText(
                            safe(rs.getString("additional_info"))
                    );

                    originalApproval = rs.getString("status_reservation");
                    originalPayment = rs.getString("payment_status");
                    originalPrinting = rs.getString("printing_status");

                    approval_status.setSelectedItem(originalApproval);
                    payment_status.setSelectedItem(originalPayment);
                    printing_status.setSelectedItem(originalPrinting);

                    String note = rs.getString("admin_note_rejection");

                    originalAdminNote =
                            "Rejected".equalsIgnoreCase(originalApproval)
                                    ? safe(note).trim()
                                    : "";

                    boolean rejected =
                            "Rejected".equalsIgnoreCase(originalApproval);

                    admin_note.setEnabled(rejected);
                    admin_note.setForeground(Color.BLACK);
                    isAdminNotePlaceholder = false;

                    if (rejected && safe(note).trim().isEmpty()) {
                        showAdminNotePlaceholder();
                    } else {
                        admin_note.setText(rejected ? safe(note) : "");
                    }

                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Reservation ID " + reservation_ID
                                    + " was not found.",
                            "Reservation Not Found",
                            JOptionPane.WARNING_MESSAGE
                    );

                    dispose();
                }

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load reservation details:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private java.awt.Frame parentFrame;

    public management_frame(java.awt.Frame parent, int reservation_ID) {
        setUndecorated(true);
        initComponents();
        setupAdminNotePlaceholder();
        
        this.parentFrame = parent; 
        this.reservation_ID = reservation_ID;

        additional_info.setEditable(false);
        additional_info.setForeground(Color.GRAY);
        additional_info.setLineWrap(true);
        additional_info.setWrapStyleWord(true);
        
        admin_note.setLineWrap(true);
        admin_note.setWrapStyleWord(true);
        admin_note.setEnabled(false);
        admin_note.setText("");

        loadReservationDetails();

        setDefaultCloseOperation(
            javax.swing.JFrame.DO_NOTHING_ON_CLOSE
        );

        setLocationRelativeTo(parent);
        
        if (parentFrame != null) {
            parentFrame.setEnabled(false);
        }
        
        approval_status.addActionListener(e -> {
            String approval = (String) approval_status.getSelectedItem();

            if ("Claimed".equalsIgnoreCase(approval)) {
                printing_status.setSelectedItem("Printed");
                printing_status.setEnabled(false);
            } else {
                printing_status.setSelectedItem("----");
                printing_status.setEnabled(true);
            }
        });

        approval_status.addActionListener(e -> {
        boolean isRejected =
                "Rejected".equals(approval_status.getSelectedItem());

        admin_note.setEnabled(isRejected);

        if (isRejected) {
            if (!isAdminNotePlaceholder
                    && admin_note.getText().trim().isEmpty()) {

                admin_note.setForeground(Color.GRAY);
                admin_note.setText(ADMIN_NOTE_PLACEHOLDER);
                isAdminNotePlaceholder = true;
            }
        } else {
            admin_note.setText("");
            admin_note.setForeground(Color.BLACK);
            isAdminNotePlaceholder = false;
        }
    });

    update_button.addActionListener(e -> validateAndUpdate());
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
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        full_name = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        email = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        contact_number = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        project_name = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        project_file = new javax.swing.JLabel();
        service_type_with_binding_type = new javax.swing.JLabel();
        specifications = new javax.swing.JLabel();
        quantity = new javax.swing.JLabel();
        total = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        payment_method = new javax.swing.JLabel();
        pick_up_date = new javax.swing.JLabel();
        time = new javax.swing.JLabel();
        down_payment = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        ref_num = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        additional_info = new javax.swing.JTextArea();
        jLabel35 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        approval_status = new javax.swing.JComboBox<>();
        payment_status = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        printing_status = new javax.swing.JComboBox<>();
        update_button = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        admin_note = new javax.swing.JTextArea();
        go_back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel8.setBackground(new java.awt.Color(0, 51, 51));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Manage Reservation");
        jPanel8.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Reservation ID; #102 ");
        jPanel8.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/printing.png"))); // NOI18N
        jPanel8.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Client Information");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText("Full Name");

        full_name.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        full_name.setForeground(new java.awt.Color(0, 51, 51));
        full_name.setText("Full Name");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(102, 102, 102));
        jLabel8.setText("Email Address");

        email.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        email.setForeground(new java.awt.Color(0, 51, 51));
        email.setText("mahirochan@gmail.com");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 102, 102));
        jLabel10.setText("Contact Number");

        contact_number.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        contact_number.setForeground(new java.awt.Color(0, 51, 51));
        contact_number.setText("09070756415");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contact_number)
                    .addComponent(jLabel10)
                    .addComponent(email)
                    .addComponent(jLabel8)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(full_name))
                .addContainerGap(119, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(0, 0, 0)
                .addComponent(full_name)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(0, 0, 0)
                .addComponent(email)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(0, 0, 0)
                .addComponent(contact_number)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Project Details");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(102, 102, 102));
        jLabel12.setText("Project:");

        project_name.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        project_name.setForeground(new java.awt.Color(0, 51, 51));
        project_name.setText("Thesis Printing");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("File:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setText("Service:");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 102, 102));
        jLabel18.setText("Specifications:");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(102, 102, 102));
        jLabel20.setText("Quantity:");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(102, 102, 102));
        jLabel21.setText("Total:");

        project_file.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        project_file.setForeground(new java.awt.Color(0, 51, 51));
        project_file.setText("Thesis_final.pdf");

        service_type_with_binding_type.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        service_type_with_binding_type.setForeground(new java.awt.Color(0, 51, 51));
        service_type_with_binding_type.setText("Printing (Hardbound)");

        specifications.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        specifications.setForeground(new java.awt.Color(0, 51, 51));
        specifications.setText("A4, Colored, Portrait");

        quantity.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        quantity.setForeground(new java.awt.Color(0, 51, 51));
        quantity.setText("5 pcs");

        total.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        total.setForeground(new java.awt.Color(0, 51, 51));
        total.setText("₱2,500.00");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(total))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(quantity))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel16))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(project_name, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(project_file, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(service_type_with_binding_type, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(specifications)))
                        .addGap(32, 32, 32))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(project_name))
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(project_file))
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(service_type_with_binding_type))
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(specifications))
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(quantity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(total))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setText("Pickup & Payment Information");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(102, 102, 102));
        jLabel25.setText("Client's Pickup Date");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(102, 102, 102));
        jLabel26.setText("Payment Method");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(102, 102, 102));
        jLabel27.setText("Client's Pickup Time");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(102, 102, 102));
        jLabel28.setText("Down Payment");

        payment_method.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        payment_method.setForeground(new java.awt.Color(0, 51, 51));
        payment_method.setText("GCash");

        pick_up_date.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        pick_up_date.setForeground(new java.awt.Color(0, 51, 51));
        pick_up_date.setText("October 15, 2026");

        time.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        time.setForeground(new java.awt.Color(0, 51, 51));
        time.setText("2:00 PM");

        down_payment.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        down_payment.setForeground(new java.awt.Color(0, 51, 51));
        down_payment.setText("₱10000");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(102, 102, 102));
        jLabel39.setText("Referencee Number");

        ref_num.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        ref_num.setForeground(new java.awt.Color(0, 51, 51));
        ref_num.setText("82383838383838383");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel26)
                            .addComponent(pick_up_date)
                            .addComponent(payment_method))
                        .addGap(70, 70, 70)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27)
                            .addComponent(time)
                            .addComponent(jLabel28)
                            .addComponent(down_payment))
                        .addGap(59, 59, 59)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39)
                            .addComponent(ref_num))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pick_up_date)
                    .addComponent(time)
                    .addComponent(ref_num))
                .addGap(3, 3, 3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(payment_method)
                    .addComponent(down_payment))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel33.setText("Additional Information from Client");

        additional_info.setColumns(20);
        additional_info.setRows(5);
        jScrollPane1.setViewportView(additional_info);

        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(102, 102, 102));
        jLabel35.setText("Submitted by the client when creating the reservation");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35)
                    .addComponent(jLabel33)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel33)
                .addGap(0, 0, 0)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel34.setText("Admin Controls");

        approval_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "----", "Pending", "Approved", "Rejected", "Claimed" }));

        payment_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "----", "Unpaid", "Partially Paid", "Fully Paid" }));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(102, 102, 102));
        jLabel36.setText("Approval Status");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(102, 102, 102));
        jLabel37.setText("Payment Status");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(102, 102, 102));
        jLabel38.setText("Printing Status");

        printing_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "----", "Not Started", "Printing", "Printed", "Ready to Claim" }));

        update_button.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        update_button.setForeground(new java.awt.Color(0, 51, 51));
        update_button.setText("UPDATE RESERVATION");
        update_button.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 51), 2, true));

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(102, 102, 102));
        jLabel41.setText("Admin Rejection Note");

        admin_note.setColumns(20);
        admin_note.setRows(5);
        jScrollPane2.setViewportView(admin_note);

        go_back.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        go_back.setForeground(new java.awt.Color(153, 153, 153));
        go_back.setText("GO BACK");
        go_back.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 2, true));
        go_back.addActionListener(this::go_backActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(update_button, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(printing_status, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addComponent(payment_status, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(approval_status, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(go_back, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(approval_status, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(payment_status, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(printing_status, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(update_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(go_back, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(44, 44, 44))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void go_backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_go_backActionPerformed
        this.dispose();
    }//GEN-LAST:event_go_backActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new management_frame(null, 0).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea additional_info;
    private javax.swing.JTextArea admin_note;
    private javax.swing.JComboBox<String> approval_status;
    private javax.swing.JLabel contact_number;
    private javax.swing.JLabel down_payment;
    private javax.swing.JLabel email;
    private javax.swing.JLabel full_name;
    private javax.swing.JButton go_back;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel payment_method;
    private javax.swing.JComboBox<String> payment_status;
    private javax.swing.JLabel pick_up_date;
    private javax.swing.JComboBox<String> printing_status;
    private javax.swing.JLabel project_file;
    private javax.swing.JLabel project_name;
    private javax.swing.JLabel quantity;
    private javax.swing.JLabel ref_num;
    private javax.swing.JLabel service_type_with_binding_type;
    private javax.swing.JLabel specifications;
    private javax.swing.JLabel time;
    private javax.swing.JLabel total;
    private javax.swing.JButton update_button;
    // End of variables declaration//GEN-END:variables
}
