package printing_reservation_system;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class AdminButtonEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener {

    private final JButton button;
    private final JFrame parent;

    public AdminButtonEditor(JFrame parent) {
        this.parent = parent;
        button = new JButton("View");
        button.addActionListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {

        button.setText("View");
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "View";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();

        requests page = new requests();
        page.setLocationRelativeTo(parent);
        page.setVisible(true);
    }
}
