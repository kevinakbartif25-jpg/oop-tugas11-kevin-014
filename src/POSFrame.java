import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

public class POSFrame extends JFrame {
    private final List<Product> products = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();

    private JTable productTable;
    private JTable cartTable;
    private JTextField qtyField;
    private JLabel selectedLabel;
    private JLabel totalLabel;
    private JLabel pointsLabel;
    private JTextArea receiptArea;
    private JButton addButton;
    private JButton checkoutButton;
    private JButton printButton;

    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;

    public POSFrame() {
        setTitle("POIN Off-Sales - Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initProductData();
        initComponents();
        bindEvents();

        setSize(1000, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initProductData() {
        products.add(new Product("P001", "Air Mineral 600ml", 3000));
        products.add(new Product("P002", "Kopi Sachet", 5000));
        products.add(new Product("P003", "Roti isi", 8000));
        products.add(new Product("P004", "Snack Keripik", 6000));
        products.add(new Product("P005", "Minuman Botol", 12000));
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        exitItem.addActionListener(e -> dispose());
        aboutItem.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "POIN Off-Sales - Praktikum Java Swing"
            )
        );

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JPanel productPanel = new JPanel(new BorderLayout(4, 4));
        productPanel.setBorder(BorderFactory.createTitledBorder("Produk"));

        productModel = new DefaultTableModel(
            new Object[] {"ID", "Nama Produk", "Harga (Rp)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Product p : products) {
            productModel.addRow(new Object[] {
                p.getId(), p.getName(), p.getPrice()
            });
        }

        productTable = new JTable(productModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel productSouthPanel = new JPanel();
        productSouthPanel.setLayout(new BoxLayout(productSouthPanel, BoxLayout.Y_AXIS));

        selectedLabel = new JLabel("Pilih produk dari tabel di kiri");
        selectedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        productSouthPanel.add(selectedLabel);
        productSouthPanel.add(Box.createVerticalStrut(6));

        JPanel qtyActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        qtyActionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qtyActionPanel.add(new JLabel("Qty:"));
        qtyField = new JTextField("1", 5);
        qtyActionPanel.add(qtyField);
        addButton = new JButton("Add to Cart");
        qtyActionPanel.add(addButton);
        productSouthPanel.add(qtyActionPanel);

        productPanel.add(productSouthPanel, BorderLayout.SOUTH);

        JPanel cartPanel = new JPanel(new BorderLayout(4, 4));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Keranjang"));

        cartModel = new DefaultTableModel(
            new Object[] {"ID", "Nama Produk", "Qty", "Harga", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel cartSouthPanel = new JPanel(new BorderLayout(4, 4));

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));

        totalLabel = new JLabel("Total: Rp0,00");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        pointsLabel = new JLabel("Points: 0");
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        checkoutButton = new JButton("Checkout");
        printButton = new JButton("Cetak");
        btnPanel.add(checkoutButton);
        btnPanel.add(printButton);

        summaryPanel.add(totalLabel);
        summaryPanel.add(Box.createVerticalStrut(4));
        summaryPanel.add(pointsLabel);
        summaryPanel.add(Box.createVerticalStrut(4));
        summaryPanel.add(btnPanel);

        cartSouthPanel.add(summaryPanel, BorderLayout.NORTH);

        JPanel receiptPanel = new JPanel(new BorderLayout(2, 2));
        receiptPanel.add(new JLabel("Struk:"), BorderLayout.NORTH);
        receiptArea = new JTextArea(8, 25);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        receiptPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        cartSouthPanel.add(receiptPanel, BorderLayout.CENTER);

        cartPanel.add(cartSouthPanel, BorderLayout.SOUTH);

        mainPanel.add(productPanel);
        mainPanel.add(cartPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void bindEvents() {
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = productTable.getSelectedRow();
                if (row >= 0 && row < products.size()) {
                    Product selected = products.get(row);
                    selectedLabel.setText(
                        "Dipilih: " + selected.getId() +
                        " - " + selected.getName() +
                        " (Rp" + selected.getPrice() + ")"
                    );
                }
            }
        });

        addButton.addActionListener(e -> addSelectedProductToCart());
        checkoutButton.addActionListener(e -> checkout());
        printButton.addActionListener(e -> printReceipt());
    }

    private void addSelectedProductToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0 || row >= products.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih produk dari tabel terlebih dahulu.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Qty harus berupa integer lebih dari 0.",
                "Input tidak valid",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Product selected = products.get(row);
        CartItem item = new CartItem(selected, qty);
        cart.add(item);

        refreshCartTable();
        updateSummary();
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        for (CartItem item : cart) {
            cartModel.addRow(new Object[] {
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQty(),
                item.getProduct().getPrice(),
                item.getSubtotal()
            });
        }
    }

    private void updateSummary() {
        int total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }
        int points = total / 1000;

        totalLabel.setText(String.format("Total: Rp%,d,00", total).replace(',', '.'));
        pointsLabel.setText("Points: " + points);
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Keranjang belanja masih kosong.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("===== POIN OFF-SALES =====\n");
        receipt.append("Toko: Demo Toko\n");
        receipt.append("==========================\n");

        int total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
            receipt.append(String.format(
                "%-4s %-15s %2d  Rp%,d,00\n",
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQty(),
                item.getSubtotal()
            ).replace(',', '.'));
        }

        int points = total / 1000;
        receipt.append("--------------------------\n");
        receipt.append(String.format("TOTAL:          Rp%,d,00\n", total).replace(',', '.'));
        receipt.append("POINTS DIDAPAT: " + points + " (1 point per Rp 1000)\n");
        receipt.append("==========================\n");
        receipt.append("Terima kasih! Silakan kunjungi kembali.\n");

        receiptArea.setText(receipt.toString());

        cart.clear();
        refreshCartTable();
        totalLabel.setText("Total: Rp0,00");
        pointsLabel.setText("Points: 0");
    }

    private void printReceipt() {
        if (receiptArea.getText().isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Belum ada struk yang dapat dicetak.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            receiptArea.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal mencetak: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
