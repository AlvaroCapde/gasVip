import java.awt.*;
import javax.swing.*;

public class Facturador extends Thread {

    public enum Estado {
        ESPERAR_CLIENTE, FACTURANDO
    }

    private int id;
    private Estado estado;
    private GasStation gasStation;
    private GasStationDashboard dashboard;
    private long billingTime;

    // UI para el estado individual del agente
    private JFrame statusFrame;
    private JLabel stateLabel;
    private JLabel iconLabel;

    public Facturador(int id, GasStation gasStation, GasStationDashboard dashboard, int billingTime) {
        this.id = id;
        this.gasStation = gasStation;
        this.estado = Estado.ESPERAR_CLIENTE;
        this.dashboard = dashboard;
        this.billingTime = billingTime * 1000L;

        // Configurar la ventana de estado
        setupStatusFrame();
    }

    private void setupStatusFrame() {
        statusFrame = new JFrame("Facturador " + id);
        statusFrame.setSize(300, 250);
        statusFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statusFrame.setLayout(new BorderLayout());
        statusFrame.setLocationRelativeTo(null);

        // Título en la parte superior
        JLabel titleLabel = new JLabel("Facturador " + id, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(34, 139, 34)); // Verde oscuro
        statusFrame.add(titleLabel, BorderLayout.NORTH);

        // Estado del facturador
        stateLabel = new JLabel("Estado: " + estado, SwingConstants.CENTER);
        stateLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Configuración del ícono del facturador
        ImageIcon facturadorIcon = new ImageIcon("src/iconos/facturador_icon.png");
        Image resizedIcon = facturadorIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        iconLabel = new JLabel(new ImageIcon(resizedIcon), SwingConstants.CENTER);

        // Configuración del panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.add(stateLabel, BorderLayout.CENTER);
        mainPanel.add(iconLabel, BorderLayout.SOUTH); // Colocar el ícono en la parte inferior

        statusFrame.add(mainPanel, BorderLayout.CENTER);
        statusFrame.setVisible(true);
    }

    private void updateStatusFrame() {
        SwingUtilities.invokeLater(() -> {
            stateLabel.setText("Estado: " + estado);
        });
    }

    public void actualizarEstado(Estado nuevoEstado) {
        Estado estadoAnterior = this.estado;
        this.estado = nuevoEstado;

        // Actualizar el marco de estado
        updateStatusFrame();

        // Actualizar el dashboard
        SwingUtilities.invokeLater(() -> {
            if (estadoAnterior != null) {
                dashboard.updateAgentState("Facturadores", estadoAnterior.name(), -1);
            }
            dashboard.updateAgentState("Facturadores", nuevoEstado.name(), 1);
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                actualizarEstado(Estado.ESPERAR_CLIENTE);

                Cliente cliente = gasStation.obtenerClienteEsperandoFacturacion(); // Obtener cliente sincronizadamente
                if (cliente != null) {
                    facturarCliente(cliente);
                }
            } catch (InterruptedException e) {
                System.out.println("Facturador " + id + " interrumpido.");
                break;
            }
        }
    }

    private void facturarCliente(Cliente cliente) throws InterruptedException {
        actualizarEstado(Estado.FACTURANDO);
        dashboard.updateCriticalZone("BillingStation", 1); // Incrementar zona crítica

        System.out.println("Facturador " + id + " está procesando pago para Cliente " + cliente.getId());

        Thread.sleep(billingTime); // Simular tiempo de facturación
        dashboard.updateCriticalZone("BillingStation", -1); // Reducir zona crítica

        cliente.finalizarFacturacion(billingTime); // Notificar al cliente que la facturación terminó

        actualizarEstado(Estado.ESPERAR_CLIENTE);
    }
}
