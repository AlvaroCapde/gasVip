import java.awt.*;
import javax.swing.*;

public class DespachadorGasolina extends Thread {

    public enum Estado {
        ESPERAR_CLIENTE, CARGANDO_GASOLINA
    }

    private int id;
    private Estado estado;
    private GasStation gasStation;
    private long gasPumpTime;
    private GasStationDashboard dashboard;

    // Componentes de la ventana de estado visual
    private JFrame statusFrame;
    private JLabel estadoLabel;
    private JLabel gasIconLabel;

    public DespachadorGasolina(int id, GasStation gasStation, int gasPumpTime, GasStationDashboard dashboard) {
        this.id = id;
        this.gasStation = gasStation;
        this.gasPumpTime = gasPumpTime * 1000L;
        this.estado = Estado.ESPERAR_CLIENTE;
        this.dashboard = dashboard;

        setupStatusFrame();  // Inicializar la ventana visual
    }

    private void setupStatusFrame() {
        // Configurar la ventana para mostrar el estado del despachador de gasolina
        statusFrame = new JFrame("Despachador Gasolina " + id);
        statusFrame.setSize(300, 200);
        statusFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statusFrame.setLocationRelativeTo(null);
        statusFrame.setLayout(new BorderLayout());

        // Título de la ventana
        JLabel titleLabel = new JLabel("Despachador Gasolina " + id, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(34, 139, 34)); // Verde oscuro
        statusFrame.add(titleLabel, BorderLayout.NORTH);

        // Etiqueta para mostrar el estado actual
        estadoLabel = new JLabel("Estado: " + estado, SwingConstants.CENTER);
        estadoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusFrame.add(estadoLabel, BorderLayout.CENTER);

        // Agregar el ícono del surtidor de gasolina y redimensionarlo
        ImageIcon gasIcon = new ImageIcon("src/iconos/gas_icon.jpg");
        Image image = gasIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Redimensionar el ícono del surtidor de gasolina
        gasIconLabel = new JLabel(new ImageIcon(image), SwingConstants.CENTER);
        statusFrame.add(gasIconLabel, BorderLayout.SOUTH); // Colocar el ícono en la parte inferior

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
                dashboard.updateAgentState("Despachadores", estadoAnterior.name(), -1);
            }
            dashboard.updateAgentState("Despachadores", nuevoEstado.name(), 1);
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                actualizarEstado(Estado.ESPERAR_CLIENTE);

                // Verificar si hay clientes esperando
                Cliente cliente = gasStation.obtenerClienteEsperandoGasolina();
                if (cliente != null) {
                    atenderCliente(cliente);
                }

                Thread.sleep(500); // Esperar entre clientes
            } catch (InterruptedException e) {
                System.out.println("Despachador Gasolina " + id + " interrumpido.");
                break;
            }
        }
    }

    private void atenderCliente(Cliente cliente) throws InterruptedException {
        dashboard.updateCriticalZone("GasPump", 1); // Incrementar zona crítica

        actualizarEstado(Estado.CARGANDO_GASOLINA);
        cliente.actualizarUIDuranteCarga();
        System.out.println("Despachador Gasolina " + id + " está cargando gasolina para Cliente " + cliente.getId());
        Thread.sleep(gasPumpTime); // Simular tiempo de carga
        gasStation.releaseGasPump(); // Liberar la bomba
        dashboard.updateCriticalZone("GasPump", -1); // Reducir zona crítica
        System.out.println("Despachador Gasolina " + id + " ha terminado de cargar para Cliente " + cliente.getId());
        cliente.finalizarCarga(); // Notificar al cliente que el servicio ha terminado
    }
}
