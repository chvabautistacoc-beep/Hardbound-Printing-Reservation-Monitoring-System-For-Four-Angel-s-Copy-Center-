package printing_reservation_system;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellEditor;

public class ButtonEditor extends AbstractCellEditor
        implements TableCellEditor {

    private final JButton button;
    private final java.awt.Frame parentFrame;
    private JTable table;
    private int row;

    public ButtonEditor(java.awt.Frame parentFrame) {

        this.parentFrame = parentFrame;

        button = new JButton("View");

        button.setOpaque(true);
        button.setBackground(new Color(0, 51, 51));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMargin(new java.awt.Insets(5, 15, 5, 15));

        button.addActionListener((ActionEvent e) -> {

            if (table == null) {
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

                int userId = UserSession.getUserId();

                fireEditingStopped();

                details_management_user payment =
                        new details_management_user(
                                parentFrame,
                                userId,
                                reservationID
                        );

                payment.setVisible(true);

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

        button.setText(value == null ? "View" : value.toString());

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "View";
    }
}
