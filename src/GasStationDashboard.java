import javax.swing.*;
import java.awt.*;

public class GasStationDashboard extends JFrame {

    // Labels para los estados de threads
    private JLabel clientsRunnableLabel, clientsTimedWaitingLabel, clientsBlockedLabel, clientsTerminatedLabel;
    private JLabel dispatchersRunnableLabel, dispatchersTimedWaitingLabel, dispatchersBlockedLabel, dispatchersTerminatedLabel;
    private JLabel oilSellersRunnableLabel, oilSellersTimedWaitingLabel, oilSellersBlockedLabel, oilSellersTerminatedLabel;
    private JLabel billersRunnableLabel, billersTimedWaitingLabel, billersBlockedLabel, billersTerminatedLabel;

    // Buffers
    private JLabel gasPumpBufferLabel, oilStationBufferLabel, billingStationBufferLabel;

    // Zonas críticas
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

        // Crear las secciones para cada tipo de agente
        agentsPanel.add(createAgentSection("Clientes", "Llegada, Manejar, Cargar, Pagar, Salir",
                clientsRunnableLabel = new JLabel(),
                clientsTimedWaitingLabel = new JLabel(),
                clientsBlockedLabel = new JLabel(),
                clientsTerminatedLabel = new JLabel()));

        agentsPanel.add(createAgentSection("Despachadores", "Esperar Cliente, Cargar Gasolina",
                dispatchersRunnableLabel = new JLabel(),
                dispatchersTimedWaitingLabel = new JLabel(),
                dispatchersBlockedLabel = new JLabel(),
                dispatchersTerminatedLabel = new JLabel()));

        agentsPanel.add(createAgentSection("Vendedores de Aceite", "Esperar Cliente, Suministrar Aceite",
                oilSellersRunnableLabel = new JLabel(),
                oilSellersTimedWaitingLabel = new JLabel(),
                oilSellersBlockedLabel = new JLabel(),
                oilSellersTerminatedLabel = new JLabel()));

        agentsPanel.add(createAgentSection("Facturadores", "Esperar Cliente, Procesar Factura",
                billersRunnableLabel = new JLabel(),
                billersTimedWaitingLabel = new JLabel(),
                billersBlockedLabel = new JLabel(),
                billersTerminatedLabel = new JLabel()));

        // Panel inferior para Buffers y Zonas Críticas
        JPanel buffersAndCriticalZonesPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buffersAndCriticalZonesPanel.setBorder(BorderFactory.createTitledBorder("Buffers y Zonas Críticas"));
        buffersAndCriticalZonesPanel.setBackground(new Color(245, 245, 245));

        // Buffers
        buffersAndCriticalZonesPanel.add(gasPumpBufferLabel = new JLabel("Buffer Bombas de Gasolina: 0 clientes"));
        buffersAndCriticalZonesPanel.add(oilStationBufferLabel = new JLabel("Buffer Estaciones de Aceite: 0 clientes"));
        buffersAndCriticalZonesPanel.add(billingStationBufferLabel = new JLabel("Buffer Estación de Facturación: 0 clientes"));

        // Zonas críticas
        buffersAndCriticalZonesPanel.add(gasPumpCriticalZoneLabel = new JLabel("Zona Crítica Bombas de Gasolina: 0 agentes"));
        buffersAndCriticalZonesPanel.add(oilStationCriticalZoneLabel = new JLabel("Zona Crítica Estaciones de Aceite: 0 agentes"));
        buffersAndCriticalZonesPanel.add(billingStationCriticalZoneLabel = new JLabel("Zona Crítica Estación de Facturación: 0 agentes"));

        // Agregar secciones al tablero
        add(agentsPanel, BorderLayout.CENTER);
        add(buffersAndCriticalZonesPanel, BorderLayout.SOUTH);
    }

    private JPanel createAgentSection(String title, String generalStates, JLabel runnable, JLabel timedWaiting, JLabel blocked, JLabel terminated) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);

        // Estados de threads
        JPanel threadStatePanel = new JPanel(new GridLayout(4, 1));
        threadStatePanel.setBorder(BorderFactory.createTitledBorder("Estados de Threads"));
        threadStatePanel.setBackground(Color.WHITE);

        threadStatePanel.add(runnable = new JLabel("Runnable: 0"));
        threadStatePanel.add(timedWaiting = new JLabel("Timed Waiting: 0"));
        threadStatePanel.add(blocked = new JLabel("Blocked: 0"));
        threadStatePanel.add(terminated = new JLabel("Terminated: 0"));

        // Estados generales
        JPanel generalStatePanel = new JPanel(new BorderLayout());
        generalStatePanel.setBorder(BorderFactory.createTitledBorder("Estados Agente"));
        generalStatePanel.setBackground(Color.WHITE);

        generalStatePanel.add(new JLabel("<html>" + generalStates.replace(", ", "<br>") + "</html>"), BorderLayout.CENTER);

        panel.add(threadStatePanel, BorderLayout.WEST);
        panel.add(generalStatePanel, BorderLayout.CENTER);

        return panel;
    }

    // Métodos para actualizar los valores
    public void updateRunnable(String agentType, int count) {
        switch (agentType) {
            case "Clientes" -> clientsRunnableLabel.setText("Runnable: " + count);
            case "Despachadores" -> dispatchersRunnableLabel.setText("Runnable: " + count);
            case "Vendedores de Aceite" -> oilSellersRunnableLabel.setText("Runnable: " + count);
            case "Facturadores" -> billersRunnableLabel.setText("Runnable: " + count);
        }
    }
}