/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package printing_reservation_system;

import java.awt.Color;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Mario Guingab
 */

public class new_reservation extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(new_reservation.class.getName());
    
    String fileName = null;
    String file_name;
    String quantityText;
    String service;
    String quality;
    String paperSize;
    String paperType;
    String color;
    String binding;
    String orientation;
    String selectedTime;
    String selectedDate;
    String additionalInfo;
    String paymentMethod;

    String reservationStatus = "Pending";
    String paymentStatus = "Unpaid";
    String printingStatus = "Printing";

    int quantity;

    boolean colored;

    double sizePrice;
    double qualityFee;
    double paperPrice;
    double orientationFee;
    double bindingFee;

    double paperTotal;
    double sizeTotal;
    double qualityTotal;
    double orientationTotal;
    double printingTotal;
    double totalBinding;
    double total;
    boolean is_lock = true;
    
    public void resetFrame() {
        v_file_name.setText("");
        v_quantity.setText("");

        service_type.setSelectedIndex(0);
        v_quality.setSelectedIndex(0);
        v_paper_size.setSelectedIndex(0);
        v_paper_type.setSelectedIndex(0);
        v_color_mode.setSelectedIndex(0);
        v_bidding_type.setSelectedIndex(0);
        v_orientation.setSelectedIndex(0);
        timePicker.setSelectedIndex(0);

        pick_date.setDate(null);

        additional_info.setText("");

        cash.setSelected(false);
        gcash.setSelected(false);
        maya.setSelected(false);

        estimated_price.setText("0.00");
        file_name_text.setText("");
        spec.setText("");
        schedule_date.setText("");

        fileName = null;
        file_name = "";
        quantityText = "";
        service = "";
        quality = "";
        paperSize = "";
        paperType = "";
        color = "";
        binding = "";
        orientation = "";
        selectedTime = "";
        selectedDate = "";
        additionalInfo = "";
        paymentMethod = "";

        quantity = 0;
        colored = false;

        sizePrice = 0.0;
        qualityFee = 0.0;
        paperPrice = 0.0;
        orientationFee = 0.0;
        bindingFee = 0.0;

        paperTotal = 0.0;
        sizeTotal = 0.0;
        qualityTotal = 0.0;
        orientationTotal = 0.0;
        printingTotal = 0.0;
        totalBinding = 0.0;
        total = 0.0;

        reservationStatus = "Pending";
        paymentStatus = "Unpaid";
        printingStatus = "Not Started";

        is_lock = true;
    }
    
    public boolean saveReservation(
            String paymentMethod,
            String referenceNumber,
            String message,
            String status_reservation,
            String payment_status,
            String printing_status) {

        String sql = "INSERT INTO reservations ("
                + "user_id, file_name, path_file_name, service, quality, "
                + "paper_size, paper_type, color, binding, orientation, "
                + "quantity, selected_date, selected_time, additional_info, "
                + "total, payment_method, referencee_number, "
                + "status_reservation, payment_status, printing_status, "
                + "admin_note_rejection"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?)";

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a payment method.",
                    "Invalid Payment Method",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (status_reservation == null || status_reservation.trim().isEmpty()
                || payment_status == null || payment_status.trim().isEmpty()
                || printing_status == null || printing_status.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Reservation, payment, and printing statuses are required.",
                    "Missing Status",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (paymentMethod.equalsIgnoreCase("Cash")) {
            referenceNumber = "N/A";
        } else if (referenceNumber == null
                || referenceNumber.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the transaction reference number.",
                    "Missing Reference Number",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (pick_date.getDate() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a schedule date.",
                    "Missing Schedule",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        try (
                java.sql.Connection conn =
                        DatabaseConnection.getConnection();
                java.sql.PreparedStatement pst =
                        conn.prepareStatement(sql)
        ) {

            pst.setInt(1, UserSession.getUserId());
            pst.setString(2, file_name);
            pst.setString(3, fileName);
            pst.setString(4, service);
            pst.setString(5, quality);
            pst.setString(6, paperSize);
            pst.setString(7, paperType);
            pst.setString(8, color);
            pst.setString(9, binding);
            pst.setString(10, orientation);
            pst.setInt(11, quantity);
            pst.setDate(
                    12,
                    new java.sql.Date(pick_date.getDate().getTime())
            );

            pst.setString(
                    13,
                    selectedTime.equals("Select Time") ? null : selectedTime
            );
            pst.setString(14, additionalInfo);
            pst.setBigDecimal(
                    15,
                    java.math.BigDecimal.valueOf(total)
            );
            pst.setString(16, paymentMethod);
            pst.setString(17, referenceNumber);
            pst.setString(18, status_reservation);
            pst.setString(19, payment_status);
            pst.setString(20, printing_status);
            pst.setNull(21, java.sql.Types.VARCHAR);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save reservation: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
            return false;
        }
    }
    
    public new_reservation() {
        initComponents();
        
        String user_name = UserSession.getFirstName().substring(0, 1).toUpperCase() + UserSession.getFirstName().substring(1).toLowerCase();
        name.setText("Hi, " + user_name);
        additional_info.setText("Special instructions, additional notes, or requests... (500 characters limit)");
        additional_info.setForeground(Color.GRAY);
        additional_info.setLineWrap(true);
        additional_info.setWrapStyleWord(true);
        v_bidding_type.setEnabled(false);
        estimated_price.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pick_date.setMinSelectableDate(new java.util.Date());
        ((javax.swing.text.AbstractDocument) additional_info.getDocument())
                .setDocumentFilter(new javax.swing.text.DocumentFilter() {

            @Override
            public void insertString(javax.swing.text.DocumentFilter.FilterBypass fb,
                                     int offset, String string,
                                     javax.swing.text.AttributeSet attr)
                    throws javax.swing.text.BadLocationException {

                if (fb.getDocument().getLength() + string.length() <= 500) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(javax.swing.text.DocumentFilter.FilterBypass fb,
                                int offset, int length, String text,
                                javax.swing.text.AttributeSet attrs)
                    throws javax.swing.text.BadLocationException {

                int newLength = fb.getDocument().getLength() - length + text.length();

                if (newLength <= 500) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MOP = new javax.swing.ButtonGroup();
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
        jPanel9 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        v_file_name = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        service_type = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        v_paper_size = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        v_paper_type = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        v_bidding_type = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        v_quantity = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        pick_date = new com.toedter.calendar.JDateChooser();
        jLabel25 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        text_file = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        additional_info = new javax.swing.JTextArea();
        jLabel44 = new javax.swing.JLabel();
        v_orientation = new javax.swing.JComboBox<>();
        jButton8 = new javax.swing.JButton();
        jLabel52 = new javax.swing.JLabel();
        v_quality = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        v_color_mode = new javax.swing.JComboBox<>();
        timePicker = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        estimated_price = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        file_name_text = new javax.swing.JLabel();
        spec = new javax.swing.JLabel();
        schedule_date = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        maya = new javax.swing.JRadioButton();
        cash = new javax.swing.JRadioButton();
        gcash = new javax.swing.JRadioButton();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();

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
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 277, Short.MAX_VALUE)
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
        jLabel6.setText("Make a Reservation");
        jPanel8.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Fill out the details below to reserve your printing service.");
        jPanel8.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/reservation_white_large.png"))); // NOI18N
        jPanel8.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel9.setBackground(new java.awt.Color(0, 51, 51));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 51, 51));
        jLabel9.setText("1. File & Service Details");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setText("File Name");

        v_file_name.addActionListener(this::v_file_nameActionPerformed);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setText("Service Type");

        service_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "----", "Printing", "Photocopy", "Photo Printing", "Document Printing", "Binding" }));
        service_type.addActionListener(this::service_typeActionPerformed);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setText("Paper Size");

        v_paper_size.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "----", "A4", "A5", "Letter", "Legal", "Other" }));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel21.setText("Paper type");

        v_paper_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "----", "Bond Paper", "Glossy Paper", "Matte Paper", "Photo Paper", "Sticker Paper" }));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel22.setText("Bidding Type");

        v_bidding_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Staple", "Ring Binding", "Spiral Binding", "Comb Binding", "Thermal Binding", "Hardbound" }));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setText("Quantity");

        jLabel10.setBackground(new java.awt.Color(0, 51, 51));
        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 51, 51));
        jLabel10.setText("2. Schedule & Time");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel24.setText("Pick-up Date");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setText("Time (Optional)");

        jLabel11.setBackground(new java.awt.Color(0, 51, 51));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 51, 51));
        jLabel11.setText("3. Additional Notes");

        jLabel12.setBackground(new java.awt.Color(0, 51, 51));
        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 51, 51));
        jLabel12.setText("4. Upload File");

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        text_file.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        text_file.setForeground(new java.awt.Color(0, 51, 51));
        text_file.setText("Drag and drop your file here, or");

        jButton5.setBackground(new java.awt.Color(0, 51, 51));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Choose File");
        jButton5.setBorderPainted(false);
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/cloud-computing.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(text_file)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(text_file)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("Supported formats:");

        jLabel15.setText("PDF, DOC, DOCX, JPEG, JPG, PNG");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("Max Size: ");

        jLabel17.setText("10MB");

        additional_info.setColumns(20);
        additional_info.setRows(5);
        additional_info.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                additional_infoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                additional_infoFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(additional_info);

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel44.setText("Orientation");

        v_orientation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "----", "Landscape", "Portrait" }));

        jButton8.setBackground(new java.awt.Color(0, 51, 51));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("CALCULATE ESTIMATED PRICE");
        jButton8.setBorderPainted(false);
        jButton8.addActionListener(this::jButton8ActionPerformed);

        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel52.setText("Quality");

        v_quality.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "----", "Eco", "Normal", "High", "Best" }));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel31.setText("Color Mode");

        v_color_mode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "----", "Colored", "Mono" }));

        timePicker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Time", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM", "06:00 PM" }));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11)
                    .addComponent(jLabel9)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(117, 117, 117))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(v_file_name)
                                .addGap(15, 15, 15)))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(service_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(v_orientation, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel52)
                            .addComponent(jLabel23)
                            .addComponent(v_quality, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(v_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(v_paper_size, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel21))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(v_paper_type, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabel22)
                                .addGap(55, 55, 55)
                                .addComponent(jLabel31))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(v_bidding_type, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(v_color_mode, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addGap(349, 349, 349))
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(pick_date, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24))
                            .addGap(35, 35, 35)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel25)
                                .addComponent(timePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel44)
                    .addComponent(jLabel52))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(v_file_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(service_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(v_orientation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(v_quality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(v_paper_size, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(v_paper_type, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(v_bidding_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(v_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(v_color_mode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pick_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        jPanel4.setBackground(new java.awt.Color(0, 51, 51));
        jPanel4.setForeground(new java.awt.Color(0, 51, 51));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/calculator.png"))); // NOI18N
        jLabel26.setText("Estimated Price");
        jPanel4.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, -1));

        jPanel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        estimated_price.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        estimated_price.setIcon(new javax.swing.ImageIcon(getClass().getResource("/printing_reservation_system/img/peso.png"))); // NOI18N
        estimated_price.setText("0.00");
        estimated_price.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel10.add(estimated_price, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 260, -1));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel28.setText("(will be updated after file review)");
        jPanel10.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 200, -1));

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel29.setText("Final price may vary depending on the");
        jPanel5.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 7, -1, -1));

        jLabel30.setText("actual file and specifications.");
        jPanel5.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, -1, 20));

        jPanel10.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 250, 50));

        jLabel32.setText("Enter the transaction reference");
        jPanel10.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 430, -1, 10));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel33.setText("Specifications");
        jPanel10.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, -1));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(204, 0, 0));
        jLabel34.setText("will be canceled.");
        jLabel34.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel10.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 520, 90, -1));

        file_name_text.setText("-");
        jPanel10.add(file_name_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 260, -1));

        spec.setText("-");
        jPanel10.add(spec, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 260, -1));

        schedule_date.setText("-");
        jPanel10.add(schedule_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 260, -1));

        jButton7.setBackground(new java.awt.Color(0, 51, 51));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("PAY NOW");
        jButton7.setBorderPainted(false);
        jButton7.addActionListener(this::jButton7ActionPerformed);
        jPanel10.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 460, 250, -1));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(0, 51, 51));
        jLabel45.setText("Reservation Summary ");
        jPanel10.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 150, -1, -1));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 51, 51));
        jLabel46.setText("Payment Method");
        jPanel10.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 300, -1, 20));

        MOP.add(maya);
        maya.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        maya.setText("MAYA");
        jPanel10.add(maya, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, 70, 20));

        MOP.add(cash);
        cash.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cash.setText("CASH");
        jPanel10.add(cash, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 330, 70, 20));

        MOP.add(gcash);
        gcash.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        gcash.setText("GCASH");
        jPanel10.add(gcash, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 370, 80, 20));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel47.setText("File / Service");
        jPanel10.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, -1, -1));

        jLabel48.setText("Pay at the printing counter");
        jPanel10.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 350, -1, 10));

        jLabel49.setText("Enter the transaction reference");
        jPanel10.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 390, -1, 10));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel35.setText("Pick-up Schedule");
        jPanel10.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, -1, -1));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(204, 0, 0));
        jLabel36.setText("Pay 50% within 30 minutes or your reservation");
        jLabel36.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel10.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 500, 250, -1));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(name)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(253, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(name)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(67, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void v_file_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_v_file_nameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_v_file_nameActionPerformed

    private void additional_infoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_additional_infoFocusGained
        if (additional_info.getText().equals("Special instructions, additional notes, or requests... (500 characters limit)")) {
            additional_info.setText("");
            additional_info.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_additional_infoFocusGained

    private void additional_infoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_additional_infoFocusLost
        if (additional_info.getText().isEmpty()) {
            additional_info.setText("Special instructions, additional notes, or requests... (500 characters limit)");
            additional_info.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_additional_infoFocusLost

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int state = this.getExtendedState();
        my_reservation my_reservation = new my_reservation();
        my_reservation.setExtendedState(state);
        my_reservation.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int state = this.getExtendedState();
        student_dashboard student_dashboard = new student_dashboard();
        student_dashboard.setExtendedState(state);
        student_dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int state = this.getExtendedState();
        user_profile profile = new user_profile();
        profile.setExtendedState(state);
        profile.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Supported Files (PDF, DOC, DOCX, JPEG, JPG, PNG)",
            "pdf", "doc", "docx", "jpeg", "jpg", "png"
        );

        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();

            fileName = file.getName();

            String extension = "";

            int dotIndex = fileName.lastIndexOf(".");

            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex + 1);
            }

            System.out.println("File name: " + fileName);
            System.out.println("Extension: " + extension);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void service_typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_service_typeActionPerformed
        String service = service_type.getSelectedItem().toString();
        
        if (service.equals("Binding")) {
            v_bidding_type.setEnabled(true);
        } else {
            v_bidding_type.setEnabled(false);
            v_bidding_type.setSelectedIndex(0);
        }
    }//GEN-LAST:event_service_typeActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        try {
            file_name = v_file_name.getText().trim();
            quantityText = v_quantity.getText().trim();

            service = service_type.getSelectedItem() == null ? "" : service_type.getSelectedItem().toString();
            quality = v_quality.getSelectedItem() == null ? "" : v_quality.getSelectedItem().toString();
            paperSize = v_paper_size.getSelectedItem() == null ? "" : v_paper_size.getSelectedItem().toString();
            paperType = v_paper_type.getSelectedItem() == null ? "" : v_paper_type.getSelectedItem().toString();
            color = v_color_mode.getSelectedItem() == null ? "" : v_color_mode.getSelectedItem().toString();
            binding = v_bidding_type.getSelectedItem() == null ? "" : v_bidding_type.getSelectedItem().toString();
            orientation = v_orientation.getSelectedItem() == null ? "" : v_orientation.getSelectedItem().toString();
            selectedTime = timePicker.getSelectedItem() == null ? "" : timePicker.getSelectedItem().toString();

            selectedDate = pick_date.getDate() == null
                    ? ""
                    : new java.text.SimpleDateFormat("MMMM dd, yyyy").format(pick_date.getDate());

            additionalInfo = additional_info.getText().trim();
            
            if (additionalInfo.isEmpty()
                    || additionalInfo.equals("Special instructions, additional notes, or requests... (500 characters limit)")) {
                additionalInfo = "No additional information";
            }

            if (file_name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the file name.");
                v_file_name.requestFocus();
                return;
            }

            if (file_name.length() > 15) {
                JOptionPane.showMessageDialog(
                        this,
                        "File name must not exceed 15 characters.",
                        "Invalid File Name!",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (service.isEmpty() || quality.isEmpty() || paperSize.isEmpty()
                    || paperType.isEmpty() || color.isEmpty() || orientation.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please complete all required fields.");
                return;
            }
            
            if (quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the quantity.");
                v_quantity.requestFocus();
                return;
            }

            if (selectedDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a schedule date.");
                pick_date.requestFocus();
                return;
            }

            if (selectedTime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a schedule time.");
                timePicker.requestFocus();
                return;
            }
            
            if (selectedTime.isEmpty()
                || selectedTime.equals("Select Time")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a schedule time.",
                        "Required Field",
                        JOptionPane.WARNING_MESSAGE
                );
                timePicker.requestFocus();
                return;
            }

            quantity = Integer.parseInt(quantityText);

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
                v_quantity.requestFocus();
                return;
            }

            if (!color.equals("Mono") && !color.equals("Colored")) {
                JOptionPane.showMessageDialog(this, "Please select a valid color mode.");
                return;
            }

            if (service.equals("Binding") && binding.equals("None")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a binding type other than None.",
                        "Invalid Binding Type",
                        JOptionPane.WARNING_MESSAGE
                );
                v_bidding_type.requestFocus();
                return;
            }

            if (fileName == null) {
                JOptionPane.showMessageDialog(this, "Please upload the file first.");
                v_file_name.requestFocus();
                return;
            }

            colored = color.equals("Colored");
            sizePrice = colored ? 2.00 : 1.00;

            switch (quality) {
                case "Eco":
                case "Normal":
                    qualityFee = 0.00;
                    break;
                case "High":
                    qualityFee = 1.00;
                    break;
                case "Best":
                    qualityFee = 2.00;
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Please select a valid quality.");
                    return;
            }

            switch (paperType) {
                case "Bond Paper":
                    paperPrice = colored ? 2.00 : 1.00;
                    break;
                case "Glossy Paper":
                    paperPrice = colored ? 15.00 : 10.00;
                    break;
                case "Matte Paper":
                    paperPrice = colored ? 12.00 : 8.00;
                    break;
                case "Photo Paper":
                    paperPrice = colored ? 15.00 : 10.00;
                    break;
                case "Sticker Paper":
                    paperPrice = colored ? 11.00 : 9.00;
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Please select a valid paper type.");
                    return;
            }

            if (orientation.equals("Portrait")) {
                orientationFee = 0.00;
            } else if (orientation.equals("Landscape")) {
                orientationFee = 2.00;
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid orientation.");
                return;
            }

            bindingFee = 0.00;

            if (service.equals("Binding")) {
                switch (binding) {
                    case "Staple":
                        bindingFee = 1.00;
                        break;
                    case "Ring Binding":
                        bindingFee = 30.00;
                        break;
                    case "Spiral Binding":
                        bindingFee = 25.00;
                        break;
                    case "Comb Binding":
                        bindingFee = 20.00;
                        break;
                    case "Thermal Binding":
                        bindingFee = 50.00;
                        break;
                    case "Hardbound":
                        bindingFee = 159.00;
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Please select a valid binding type.");
                        return;
                }

                if (colored) {
                    bindingFee *= 2;
                }
            }

            paperTotal = paperPrice * quantity;
            sizeTotal = sizePrice * quantity;
            qualityTotal = qualityFee * quantity;
            orientationTotal = orientationFee * quantity;

            printingTotal = paperTotal + sizeTotal + qualityTotal + orientationTotal;
            totalBinding = bindingFee * quantity;
            total = printingTotal + totalBinding;

            estimated_price.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            estimated_price.setText(String.format("%.2f", total));
            file_name_text.setText(file_name + " • " + service);
            spec.setText(paperSize + " • " + color + " • " + quantity + " copies");
            schedule_date.setText(selectedDate + " • " + selectedTime);
            additional_info.setText(additionalInfo);

            is_lock = false;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
            v_quantity.requestFocus();
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (is_lock) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please calculate the estimated price first.",
                    "Missing Estimated Price",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!gcash.isSelected() && !maya.isSelected() && !cash.isSelected()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a payment method.",
                    "Payment Method Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (cash.isSelected()) {

            int confirmCash = JOptionPane.showConfirmDialog(
                    this,
                    "You selected CASH payment.\n\n"
                    + "I acknowledge that I must pay the required "
                    + "downpayment for my reservation within 30 minutes "
                    + "of submitting my request.\n\n"
                    + "Otherwise, my reservation will be automatically canceled.\n\n"
                    + "I confirm that all my reservation details are correct "
                    + "and have been double-checked.\n\n"
                    + "Do you acknowledge these conditions and want to proceed?",
                    "Confirm Cash Reservation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirmCash != JOptionPane.YES_OPTION) {
                return;
            }

            if (saveReservation(
                "Cash",
                "N/A",
                "Waiting for payment",
                "Pending",
                "Unpaid",
                "Not Started")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cash reservation saved successfully!\n\n"
                        + "Please go to the counter and pay your required downpayment within 30 minutes.",
                        "Reservation Submitted",
                        JOptionPane.INFORMATION_MESSAGE
                );
                resetFrame();
            }

            return;
        }

        if (gcash.isSelected() || maya.isSelected()) {

            paymentMethod = gcash.isSelected() ? "GCash" : "Maya";

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "You selected " + paymentMethod + " payment.\n\n"
                    + "Please confirm that all your reservation details "
                    + "are correct and have been double-checked.\n\n"
                    + "Do you want to proceed?",
                    "Confirm Reservation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            mode_of_payment payment = new mode_of_payment(
                    this,
                    total,
                    this,
                    paymentMethod
            );
            payment.setVisible(true);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new new_reservation().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup MOP;
    private javax.swing.JTextArea additional_info;
    private javax.swing.JRadioButton cash;
    private javax.swing.JLabel estimated_price;
    private javax.swing.JLabel file_name_text;
    private javax.swing.JRadioButton gcash;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
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
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton maya;
    private javax.swing.JLabel name;
    private com.toedter.calendar.JDateChooser pick_date;
    private javax.swing.JLabel schedule_date;
    private javax.swing.JComboBox<String> service_type;
    private javax.swing.JLabel spec;
    private javax.swing.JLabel text_file;
    private javax.swing.JComboBox<String> timePicker;
    private javax.swing.JComboBox<String> v_bidding_type;
    private javax.swing.JComboBox<String> v_color_mode;
    private javax.swing.JTextField v_file_name;
    private javax.swing.JComboBox<String> v_orientation;
    private javax.swing.JComboBox<String> v_paper_size;
    private javax.swing.JComboBox<String> v_paper_type;
    private javax.swing.JComboBox<String> v_quality;
    private javax.swing.JTextField v_quantity;
    // End of variables declaration//GEN-END:variables
}
