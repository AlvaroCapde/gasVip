import javax.swing.*;
import java.awt.*;

public class Cliente extends Thread {

    public enum Estado {
        LLEGADA, MANEJAR, ESPERANDO_DISPONIBILIDAD, ESPERANDO_DESPACHADOR, ESPERANDO_FACTURADOR, PAGANDO, CARGANDO, SALIR
    }

    private int id;
    private boolean quiereGasolina;
    private boolean quiereAceite;
    private Estado estado;
    private GasStation gasStation;
    private JFrame frame;
    private JLabel estadoLabel;
    private JLabel objetivoIconLabel;
    private GasStationDashboard dashboard;
    private boolean enEsperaDeFacturacion = false; // Controla si ya está en la cola

    private long billingTime = 1000;


    public Cliente(int id, GasStation gasStation,GasStationDashboard dashboard) {
        this.id = id;
        this.gasStation = gasStation;
        this.estado = Estado.LLEGADA;
        this.dashboard = dashboard;

        // Decidir si quiere gasolina o aceite (no ambos)
        this.quiereGasolina = Math.random() < 0.5;
        this.quiereAceite = !quiereGasolina;
        crearVentana();
    }

    private void crearVentana() {
        frame = new JFrame("Cliente " + id);
        frame.setSize(300, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Cliente " + id, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(34, 139, 34));
        frame.add(titleLabel, BorderLayout.NORTH);

        estadoLabel = new JLabel("Estado: " + estado, SwingConstants.CENTER);
        estadoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        frame.add(estadoLabel, BorderLayout.CENTER);

        // Mostrar objetivo con íconos
        objetivoIconLabel = new JLabel("", SwingConstants.CENTER);
        objetivoIconLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        frame.add(objetivoIconLabel, BorderLayout.SOUTH);

        if (quiereGasolina) {
            objetivoIconLabel.setText("Objetivo: Gasolina");
            objetivoIconLabel.setIcon(new ImageIcon("gas_icon.jpg")); // Coloca el path correcto del icono
        } else if (quiereAceite) {
            objetivoIconLabel.setText("Objetivo: Aceite");
            objetivoIconLabel.setIcon(new ImageIcon("oil_icon.png")); // Coloca el path correcto del icono
        }

        frame.setVisible(true);
    }

    public void actualizarEstado(Estado nuevoEstado) {
        Estado estadoAnterior = this.estado; // Guardar el estado anterior
        this.estado = nuevoEstado;

        // Actualizar el dashboard
        SwingUtilities.invokeLater(() -> {
            if (estadoAnterior != null) {
                dashboard.updateAgentState("Clientes", estadoAnterior.name(), -1); // Restar del estado anterior
            }
            dashboard.updateAgentState("Clientes", nuevoEstado.name(), 1); // Sumar al nuevo estado
            estadoLabel.setText("Estado: " + nuevoEstado); // Actualizar la UI del cliente
        });
    }

    @Override
    public void run() {
        try {
            while (estado != Estado.SALIR) {
                switch (estado) {
                    case LLEGADA:
                        actualizarEstado(Estado.LLEGADA);
                        Thread.sleep(2000);
                        actualizarEstado(Estado.MANEJAR);
                        break;

                    case MANEJAR:
                        Thread.sleep(1000);
                        actualizarEstado(Estado.ESPERANDO_DISPONIBILIDAD);
                        break;

                    case ESPERANDO_DISPONIBILIDAD:
                        if (quiereGasolina) {
                            verificarGasolina();
                        } else if (quiereAceite) {
                            verificarAceite();
                        }
                        break;

                    case ESPERANDO_DESPACHADOR:
                        // Cliente espera a ser atendido por un despachador
                        break;

                    case CARGANDO:
                        // Estado manejado dinámicamente por el despachador
                        break;

                    case ESPERANDO_FACTURADOR:
                        if (!enEsperaDeFacturacion) { // Solo agregar si no está en espera
                            gasStation.agregarClienteEsperandoFacturacion(this); // Agregar a la cola de facturación
                            enEsperaDeFacturacion = true; // Marcar como agregado
                            System.out.println("Cliente " + id + " está esperando un facturador.");
                        }
                        System.out.println("Cliente " + id + " está esperando un facturador.");
                        break;


                    case PAGANDO:
                        System.out.println("Cliente " + id + " está pagando.");
                        Thread.sleep(billingTime);
                        actualizarEstado(Estado.SALIR); // Transición explícita a "SALIR"
                        break;

                    case SALIR:
                        actualizarEstado(Estado.SALIR);
                        Thread.sleep(500);
                        frame.dispose();
                        break;
                }
                System.out.println("Cliente " + id + " - Estado actual después del switch: " + estado);
            }
        } catch (InterruptedException e) {
            actualizarEstado(Estado.SALIR);
            frame.dispose();
        }
    }

    public void finalizarFacturacion(long billingTime) {
            System.out.println("Cliente " + id + " - Facturación completada, cambiando a PAGANDO.");
            this.billingTime = billingTime;
            actualizarEstado(Estado.PAGANDO);

    }

    private void verificarGasolina() throws InterruptedException {
        if (gasStation.reserveGasPump()) {
            gasStation.agregarClienteEsperandoGasolina(this);
            actualizarEstado(Estado.ESPERANDO_DESPACHADOR);
        } else {
            actualizarEstado(Estado.ESPERANDO_DISPONIBILIDAD);
        }
    }

    private void verificarAceite() throws InterruptedException {
        if (gasStation.reserveOilStation()) {
            gasStation.agregarClienteEsperandoAceite(this);
            actualizarEstado(Estado.ESPERANDO_DESPACHADOR);
        } else {
            actualizarEstado(Estado.ESPERANDO_DISPONIBILIDAD);
        }
    }

    public void finalizarCarga() {
        System.out.println("Cliente " + id + " - finalizando carga, cambiando a PAGAR.");
        actualizarEstado(Estado.ESPERANDO_FACTURADOR);
    }

    public void actualizarUIDuranteCarga() {
        actualizarEstado(Estado.CARGANDO);
    }

    public long getId() {
        return id;
    }
}