import javax.swing.*;
import java.awt.*;

public class ComponentDemo extends JFrame {
    private final JTextField nameField;
    private final JComboBox<String> deptCombo;
    private final JCheckBox activeCheck;
    private final JRadioButton regulerRadio;
    private final JRadioButton ekstensiRadio;
    private final JTextArea outputArea;
    private final JButton showButton;

    public ComponentDemo() {
        setTitle("Swing Component Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 6, 6));

        formPanel.add(new JLabel("Nama:"));
        nameField = new JTextField(15);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Jurusan:"));
        deptCombo = new JComboBox<>(new String[] {"Informatika", "Elektronika", "Mesin"});
        formPanel.add(deptCombo);

        formPanel.add(new JLabel("Status:"));
        activeCheck = new JCheckBox("Mahasiswa aktif", true);
        formPanel.add(activeCheck);

        formPanel.add(new JLabel("Kelas:"));
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        regulerRadio = new JRadioButton("Reguler", true);
        ekstensiRadio = new JRadioButton("Ekstensi");
        ButtonGroup group = new ButtonGroup();
        group.add(regulerRadio);
        group.add(ekstensiRadio);
        radioPanel.add(regulerRadio);
        radioPanel.add(ekstensiRadio);
        formPanel.add(radioPanel);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        showButton = new JButton("Tampilkan");
        actionPanel.add(showButton);

        JPanel topPanel = new JPanel(new BorderLayout(6, 6));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(6, 25);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        showButton.addActionListener(e -> {
            if (nameField.getText().isBlank()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Nama wajib diisi.",
                    "Input tidak valid",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String nama = nameField.getText();
            String jurusan = (String) deptCombo.getSelectedItem();
            boolean aktif = activeCheck.isSelected();
            String kelas = regulerRadio.isSelected() ? "Reguler" : "Ekstensi";

            outputArea.setText(
                "Nama: " + nama +
                "\nJurusan: " + jurusan +
                "\nAktif: " + aktif +
                "\nKelas: " + kelas
            );
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ComponentDemo::new);
    }
}
