package printing_reservation_system;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellEditor;

public class AdminManageEditor extends AbstractCellEditor
        implements TableCellEditor {

    private final JButton button;
    private final java.awt.Frame parentFrame;
    private JTable table;
    private int row;

    public AdminManageEditor(java.awt.Frame parentFrame) {

        this.parentFrame = parentFrame;

        button = new JButton("Manage");

        button.setOpaque(true);
        button.setBackground(new Color(0, 102, 102));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMargin(new java.awt.Insets(5, 15, 5, 15));

        button.addActionListener((ActionEvent e) -> {

            if (table == null) {
                fireEditingStopped();
                return;
            }

            int modelRow = table.convertRowIndexToModel(row);

            Object reservation_ID = table.getModel()
                    .getValueAt(modelRow, 0);

            if (reservation_ID == null) {
                fireEditingStopped();
                return;
            }

            try {
                int reservationID = Integer.parseInt(
                        reservation_ID.toString()
                );

                fireEditingStopped();

                management_frame frame =
                        new management_frame(
                                parentFrame,
                                reservationID
                        );

                frame.setVisible(true);

            } catch (NumberFormatException ex) {

                fireEditingStopped();

                JOptionPane.showMessageDialog(
                        table,
                        "Invalid Reservation ID.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {

        this.table = table;
        this.row = row;

        button.setText(value == null ? "Manage" : value.toString());

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Manage";
    }
}