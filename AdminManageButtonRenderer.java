package printing_reservation_system;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class AdminManageButtonRenderer extends JButton
        implements TableCellRenderer {

    public AdminManageButtonRenderer() {
        setText("Manage");
        setOpaque(true);
        setBackground(new Color(0, 51, 51));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setMargin(new java.awt.Insets(5, 15, 5, 15));
        setCursor(new java.awt.Cursor(
                java.awt.Cursor.HAND_CURSOR
        ));
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        setText(value == null ? "Manage" : value.toString());

        return this;
    }
}