import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class InventoryApp extends JFrame {
    private InventoryManager manager;
    private InventoryTableModel tableModel;
    private JTable productTable;
    
    private JTextField txtBarcode;
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtQuantity;
    private JTextField txtSearch;
    private JLabel lblTotalValue;

    public InventoryApp() {
        manager = InventoryManager.getInstance();
        setTitle("Inventory Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Search ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Fast Lookup (Barcode)"));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        searchPanel.add(new JLabel("Barcode:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        
        // Tier 2: Barcode Mockup - Instant Find
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });
        btnSearch.addActionListener(e -> performSearch());
        add(searchPanel, BorderLayout.NORTH);

        // --- Center Panel: Table ---
        tableModel = new InventoryTableModel(manager.getProducts());
        productTable = new JTable(tableModel);
        
        // Tier 1: Low Stock Alerts
        productTable.setDefaultRenderer(Object.class, new LowStockRenderer());
        
        // Selection listener to populate form
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                int row = productTable.getSelectedRow();
                Product p = manager.getProducts().get(row);
                populateForm(p);
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inventory Items"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Form & Actions ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        
        formPanel.add(new JLabel("Barcode:"));
        txtBarcode = new JTextField();
        formPanel.add(txtBarcode);
        
        formPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);
        
        formPanel.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        formPanel.add(txtPrice);
        
        formPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        formPanel.add(txtQuantity);
        
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAddUpdate = new JButton("Save / Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear Form");
        lblTotalValue = new JLabel("Total Inventory Value: $0.00");
        lblTotalValue.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalValue.setForeground(new Color(0, 100, 0));
        
        btnAddUpdate.addActionListener(e -> saveOrUpdateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
        
        actionPanel.add(btnAddUpdate);
        actionPanel.add(btnDelete);
        actionPanel.add(btnClear);
        actionPanel.add(Box.createHorizontalStrut(20));
        actionPanel.add(lblTotalValue);
        
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        refreshData();
    }

    private void performSearch() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) return;
        
        // O(1) Search via Manager
        Product p = manager.getProductByBarcode(query);
        if (p != null) {
            populateForm(p);
            // Select in table
            int index = manager.getProducts().indexOf(p);
            if (index != -1) {
                productTable.setRowSelectionInterval(index, index);
                productTable.scrollRectToVisible(productTable.getCellRect(index, 0, true));
            }
        } else {
            productTable.clearSelection();
        }
    }

    private void saveOrUpdateProduct() {
        try {
            String barcode = txtBarcode.getText().trim();
            String name = txtName.getText().trim();
            
            if (barcode.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Barcode and Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double price = Double.parseDouble(txtPrice.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            
            // Validation: Negative Stock
            if (price < 0 || quantity < 0) {
                JOptionPane.showMessageDialog(this, "Price and Quantity cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Product p = new Product(barcode, name, price, quantity);
            manager.addOrUpdateProduct(p);
            
            refreshData();
            clearForm();
            
            JOptionPane.showMessageDialog(this, "Product saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Price and Quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        String barcode = txtBarcode.getText().trim();
        if (!barcode.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.deleteProduct(barcode);
                refreshData();
                clearForm();
            }
        }
    }

    private void populateForm(Product p) {
        txtBarcode.setText(p.getBarcode());
        txtName.setText(p.getName());
        txtPrice.setText(String.format("%.2f", p.getPrice()));
        txtQuantity.setText(String.valueOf(p.getQuantity()));
    }

    private void clearForm() {
        txtBarcode.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        productTable.clearSelection();
    }

    private void refreshData() {
        tableModel.fireTableDataChanged();
        lblTotalValue.setText(String.format("Total Inventory Value: $%.2f", manager.calculateTotalInventoryValue()));
    }

    // --- Table Model ---
    class InventoryTableModel extends AbstractTableModel {
        private String[] columnNames = {"Barcode", "Name", "Price", "Quantity", "Total Value"};
        private List<Product> data;

        public InventoryTableModel(List<Product> data) {
            this.data = data;
        }

        @Override
        public int getRowCount() { return data.size(); }

        @Override
        public int getColumnCount() { return columnNames.length; }

        @Override
        public String getColumnName(int column) { return columnNames[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product p = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return p.getBarcode();
                case 1: return p.getName();
                case 2: return String.format("$%.2f", p.getPrice());
                case 3: return p.getQuantity();
                case 4: return String.format("$%.2f", p.getTotalValue());
                default: return null;
            }
        }
    }

    // --- Tier 1: Custom Renderer to highlight Low Stock (< 5) ---
    class LowStockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            int modelRow = table.convertRowIndexToModel(row);
            Product p = manager.getProducts().get(modelRow);
            
            if (p.getQuantity() < 5) {
                c.setBackground(new Color(255, 200, 200)); // Light Red
                c.setForeground(Color.RED);
            } else {
                c.setBackground(table.getBackground());
                c.setForeground(table.getForeground());
            }

            // Keep selection color apparent
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            
            return c;
        }
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new InventoryApp().setVisible(true);
        });
    }
}
