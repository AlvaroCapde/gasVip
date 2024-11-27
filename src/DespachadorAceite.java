import java.awt.*;
import javax.swing.*;

public class DespachadorAceite extends Thread {

    public enum Estado {
        ESPERAR_CLIENTE, SUMINISTRANDO_ACEITE
    }

    private int id;
    private Estado estado;
    private GasStation gasStation;
    private long oilPumpTime;
    private GasStationDashboard dashboard;

    // Componentes de la ventana de estado visual
    private JFrame statusFrame;
    private JLabel estadoLabel;
    private JLabel oilIconLabel;

    public DespachadorAceite(int id, GasStation gasStation, int oilPumpTime, GasStationDashboard dashboard) {
        this.id = id;
        this.gasStation = gasStation;
        this.oilPumpTime = oilPumpTime * 1000L;
        this.estado = Estado.ESPERAR_CLIENTE;
        this.dashboard = dashboard;

        setupStatusFrame();  // Inicializar la ventana visual
    }

    private void setupStatusFrame() {
        // Configurar la ventana para mostrar el estado del despachador de aceite
        statusFrame = new JFrame("Despachador Aceite " + id);
        statusFrame.setSize(300, 200);
        statusFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statusFrame.setLocationRelativeTo(null);
        statusFrame.setLayout(new BorderLayout());

        // Título de la ventana
        JLabel titleLabel = new JLabel("Despachador Aceite " + id, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(34, 139, 34)); // Verde oscuro
        statusFrame.add(titleLabel, BorderLayout.NORTH);

        // Etiqueta para mostrar el estado actual
        estadoLabel = new JLabel("Estado: " + estado, SwingConstants.CENTER);
        estadoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusFrame.add(estadoLabel, BorderLayout.CENTER);

        // Agregar el ícono del aceite y redimensionarlo
        ImageIcon oilIcon = new ImageIcon("src/iconos/oil_icon.png");
        Image image = oilIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Redimensionar el ícono del aceite
        oilIconLabel = new JLabel(new ImageIcon(image), SwingConstants.CENTER);
        statusFrame.add(oilIconLabel, BorderLayout.SOUTH); // Colocar el ícono en la parte inferior

        statusFrame.setVisible(true);
    }

    public void actualizarEstado(Estado nuevoEstado) {
        Estado estadoAnterior = this.estado;
        this.estado = nuevoEstado;

        // Actualizar la ventana de estado visual
        SwingUtilities.invokeLater(() -> {
            estadoLabel.setText("Estado: " + nuevoEstado);
        });

        // Actualizar el dashboard general
        SwingUtilities.invokeLater(() -> {
            if (estadoAnterior != null) {
                dashboard.updateAgentState("Vendedores de Aceite", estadoAnterior.name(), -1);
            }
            dashboard.updateAgentState("Vendedores de Aceite", nuevoEstado.name(), 1);
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                actualizarEstado(Estado.ESPERAR_CLIENTE);

                // Verificar si hay clientes esperando
                Cliente cliente = gasStation.obtenerClienteEsperandoAceite();
                if (cliente != null) {
                    atenderCliente(cliente);
                }

                Thread.sleep(500); // Esperar entre clientes
            } catch (InterruptedException e) {
                System.out.println("Despachador Aceite " + id + " interrumpido.");
                break;
            }
        }
    }

    private void atenderCliente(Cliente cliente) throws InterruptedException {
        dashboard.updateCriticalZone("OilStation", 1); // Incrementar zona crítica

        actualizarEstado(Estado.SUMINISTRANDO_ACEITE);
        cliente.actualizarUIDuranteCarga();
        System.out.println("Despachador Aceite " + id + " está suministrando aceite para Cliente " + cliente.getId());
        Thread.sleep(oilPumpTime);
        gasStation.releaseOilStation();
        dashboard.updateCriticalZone("OilStation", -1); // Decrementar zona crítica

        cliente.finalizarCarga();
    }
}
