import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GasStationDashboard extends JFrame {

    // Estados específicos por agente
    private final Map<String, Map<String, JLabel>> agentStateLabels;

    // Buffers y Zonas Críticas
    private JLabel gasPumpBufferLabel, oilStationBufferLabel, billingStationBufferLabel;
    private JLabel gasPumpCriticalZoneLabel, oilStationCriticalZoneLabel, billingStationCriticalZoneLabel;

    public GasStationDashboard() {
        setTitle("Tablero General - Simulador de Gasolinería");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Título principal
        JLabel titleLabel = new JLabel("Tablero General - Gasolinería VIP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(34, 139, 34)); // Verde oscuro
        add(titleLabel, BorderLayout.NORTH);

        // Panel principal con las secciones de agentes
        JPanel agentsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        agentsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        agentsPanel.setBackground(new Color(245, 245, 245)); // Gris claro

        agentStateLabels = new HashMap<>();

        // Crear las secciones para cada tipo de agente
        agentsPanel.add(createAgentSection("Clientes",
                new String[]{"LLEGADA", "MANEJAR", "ESPERANDO_DISPONIBILIDAD", "ESPERANDO_DESPACHADOR", "CARGANDO", "ESPERANDO_FACTURADOR", "PAGANDO", "SALIR"}));

        agentsPanel.add(createAgentSection("Despachadores",
                new String[]{"ESPERAR_CLIENTE", "CARGANDO_GASOLINA"}));

        agentsPanel.add(createAgentSection("Vendedores de Aceite",
                new String[]{"ESPERAR_CLIENTE", "SUMINISTRAR_ACEITE"}));

        agentsPanel.add(createAgentSection("Facturadores",
                new String[]{"ESPERAR_CLIENTE", "FACTURANDO"}));

        // Panel inferior para Buffers y Zonas Críticas
        JPanel buffersAndCriticalZonesPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buffersAndCriticalZonesPanel.setBorder(BorderFactory.createTitledBorder("Buffers y Zonas Críticas"));
        buffersAndCriticalZonesPanel.setBackground(new Color(245, 245, 245));

        // Buffers
        buffersAndCriticalZonesPanel.add(gasPumpBufferLabel = new JLabel("Buffer Bombas de Gasolina: 0 clientes"));
        buffersAndCriticalZonesPanel.add(oilStationBufferLabel = new JLabel("Buffer Estaciones de Aceite: 0 clientes"));
        buffersAndCriticalZonesPanel.add(billingStationBufferLabel = new JLabel("Buffer Estación de Facturación: 0 clientes"));

        // Zonas críticas
        buffersAndCriticalZonesPanel.add(gasPumpCriticalZoneLabel = new JLabel("Zona Crítica Gas Pumps: 0 agentes"));
        buffersAndCriticalZonesPanel.add(oilStationCriticalZoneLabel = new JLabel("Zona Crítica Oil Stations: 0 agentes"));
        buffersAndCriticalZonesPanel.add(billingStationCriticalZoneLabel = new JLabel("Zona Crítica Billing Stations: 0 agentes"));

        // Agregar secciones al tablero
        add(agentsPanel, BorderLayout.CENTER);
        add(buffersAndCriticalZonesPanel, BorderLayout.SOUTH);
    }

    private JPanel createAgentSection(String title, String[] agentStates) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);

        // Estados específicos
        JPanel agentStatePanel = new JPanel(new GridLayout(agentStates.length, 1));
        agentStatePanel.setBorder(BorderFactory.createTitledBorder("Estados Específicos"));
        agentStatePanel.setBackground(Color.WHITE);

        Map<String, JLabel> stateLabels = new HashMap<>();
        for (String state : agentStates) {
            JLabel label = new JLabel(state + ": 0");
            stateLabels.put(state, label);
            agentStatePanel.add(label);
        }
        agentStateLabels.put(title, stateLabels);

        panel.add(agentStatePanel, BorderLayout.CENTER);
        return panel;
    }

    // Método para actualizar los estados específicos de los agentes
    public synchronized void updateAgentState(String agentType, String state, int delta) {
        Map<String, JLabel> stateLabels = agentStateLabels.get(agentType);
        if (stateLabels != null) {
            JLabel label = stateLabels.get(state);
            if (label != null) {
                String text = label.getText();
                int currentCount = Integer.parseInt(text.split(": ")[1]);

                // Validar antes de actualizar el contador
                if (delta < 0 && currentCount <= 0) {
                    System.out.println("Warning: Intento de reducir contador de " + state + " por debajo de 0.");
                    return; // Evitar valores negativos
                }
                currentCount += delta;
                label.setText(state + ": " + currentCount);
            }
        }
    }

    // Método para actualizar los buffers
    public synchronized void updateBuffer(String bufferType, int count) {
        switch (bufferType) {
            case "GasPump" -> gasPumpBufferLabel.setText("Buffer Bombas de Gasolina: " + count + " clientes");
            case "OilStation" -> oilStationBufferLabel.setText("Buffer Estaciones de Aceite: " + count + " clientes");
            case "BillingStation" -> billingStationBufferLabel.setText("Buffer Estación de Facturación: " + count + " clientes");
        }
    }

    // Método para actualizar las zonas críticas
    public synchronized void updateCriticalZone(String zoneType, int delta) {
        JLabel label = switch (zoneType) {
            case "GasPump" -> gasPumpCriticalZoneLabel;
            case "OilStation" -> oilStationCriticalZoneLabel;
            case "BillingStation" -> billingStationCriticalZoneLabel;
            default -> null;
        };

        if (label != null) {
            String text = label.getText();
            int currentCount = Integer.parseInt(text.replaceAll("[^0-9]", ""));
            int newCount = currentCount + delta;

            if (newCount < 0) {
                System.out.println("Warning: Intento de reducir el contador de " + zoneType + " por debajo de 0. Operación ignorada.");
                return;
            }
            label.setText(zoneType + ": " + newCount + " agentes");
        }
    }
}