import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GasStationConfigWindow extends JFrame {
    private JTextField clientsField;
    private JTextField pumpsField;
    private JTextField oilMachinesField;
    private JTextField gasDispatchersField;
    private JTextField oilDispatchersField;
    private JTextField billersField;

    private JTextField gasLoadingTimeField;
    private JTextField oilServiceTimeField;
    private JTextField billingTimeField;

    private int numClients;
    private int numPumps;
    private int numOilMachines;
    private int gasDispatchers;
    private int oilDispatchers;
    private int numBillers;

    private int gasLoadingTime;
    private int oilServiceTime;
    private int billingTime;

    private boolean isConfigSaved = false; // Indica si se guardó la configuración

    public GasStationConfigWindow() {
        setTitle("Gasolinerías VIP Configuración");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Título
        JLabel titleLabel = new JLabel("Gasolinerías VIP Configuración", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(34, 139, 34)); // Verde oscuro
        add(titleLabel, BorderLayout.NORTH);

        // Panel para los campos
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(10, 1, 10, 10)); // Una sola columna
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(245, 245, 245));

        // Agregar campos de texto con etiquetas
        clientsField = createLabeledField(inputPanel, "Número de Clientes Permitidos:");
        pumpsField = createLabeledField(inputPanel, "Número de Bombas de Gasolina Disponibles (Pumps):");
        oilMachinesField = createLabeledField(inputPanel, "Número de Máquinas de Aceite Disponibles:");
        gasDispatchersField = createLabeledField(inputPanel, "Número de Despachadores de Gasolina:");
        oilDispatchersField = createLabeledField(inputPanel, "Número de Despachadores de Aceite:");
        billersField = createLabeledField(inputPanel, "Número de Facturadores Disponibles (1-2):");
        gasLoadingTimeField = createLabeledField(inputPanel, "Zona de Carga de Gasolina (Tiempo en segundos):");
        oilServiceTimeField = createLabeledField(inputPanel, "Zona de Aceite (Tiempo en segundos):");
        billingTimeField = createLabeledField(inputPanel, "Facturación (Tiempo en segundos):");

        add(inputPanel, BorderLayout.CENTER);

        // Botones
        JButton saveButton = new JButton("Guardar");
        JButton cancelButton = new JButton("Cancelar");

        saveButton.setBackground(new Color(0, 128, 255)); // Azul
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);

        cancelButton.setBackground(new Color(255, 69, 0)); // Rojo
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);

        saveButton.addActionListener(this::saveConfig);
        cancelButton.addActionListener(e -> System.exit(0));

        // Panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextField createLabeledField(JPanel panel, String labelText) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField textField = new JTextField();
        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(textField, BorderLayout.CENTER);
        panel.add(fieldPanel);
        return textField;
    }

    private void saveConfig(ActionEvent e) {
        try {
            numClients = Integer.parseInt(clientsField.getText());
            numPumps = Integer.parseInt(pumpsField.getText());
            numOilMachines = Integer.parseInt(oilMachinesField.getText());
            numBillers = Integer.parseInt(billersField.getText());
            gasDispatchers = Integer.parseInt(gasDispatchersField.getText());
            oilDispatchers = Integer.parseInt(oilDispatchersField.getText());

            if (numBillers < 1 || numBillers > 2) {
                throw new IllegalArgumentException("El número de facturadores debe estar entre 1 y 2.");
            }

            gasLoadingTime = Integer.parseInt(gasLoadingTimeField.getText());
            oilServiceTime = Integer.parseInt(oilServiceTimeField.getText());
            billingTime = Integer.parseInt(billingTimeField.getText());

            isConfigSaved = true;

            JOptionPane.showMessageDialog(this, "Configuración guardada exitosamente.");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfigSaved() {
        return isConfigSaved;
    }

    // Métodos getter
    public int getNumClients() {
        return numClients;
    }

    public int getNumPumps() {
        return numPumps;
    }
    public int getGasDispatchers(){
        return gasDispatchers;
    }

    public int getOilDispatchers(){
        return oilDispatchers;
    }

    public int getNumOilMachines() {
        return numOilMachines;
    }

    public int getNumBillers() {
        return numBillers;
    }

    public int getGasLoadingTime() {
        return gasLoadingTime;
    }

    public int getOilServiceTime() {
        return oilServiceTime;
    }

    public int getBillingTime() {
        return billingTime;
    }

}