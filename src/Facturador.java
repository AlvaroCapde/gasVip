import javax.swing.*;
import java.util.concurrent.locks.Lock;

public class Facturador extends Thread {

    public enum Estado {
        ESPERAR_CLIENTE, FACTURANDO
    }

    private int id;
    private Estado estado;
    private GasStation gasStation;
    private GasStationDashboard dashboard;
    private long billingTime;

    public Facturador(int id, GasStation gasStation, GasStationDashboard dashboard, int billingTime) {
        this.id = id;
        this.gasStation = gasStation;
        this.estado = Estado.ESPERAR_CLIENTE;
        this.dashboard = dashboard;
        this.billingTime = billingTime * 1000L;
    }

    public void actualizarEstado(Estado nuevoEstado) {
        Estado estadoAnterior = this.estado;
        this.estado = nuevoEstado;

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

        Thread.sleep(1000); // Simular tiempo de facturación
        dashboard.updateCriticalZone("BillingStation", -1); // Reducir zona crítica

        cliente.finalizarFacturacion(billingTime); // Notificar al cliente que la facturación terminó

        actualizarEstado(Estado.ESPERAR_CLIENTE);
    }
}
