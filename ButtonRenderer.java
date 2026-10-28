package printing_reservation_system;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setText("View");
        setOpaque(true);
        setBackground(new Color(0, 51, 51));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setMargin(new java.awt.Insets(5, 15, 5, 15));
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        setText(value == null ? "View" : value.toString());

        return this;
    }
}
