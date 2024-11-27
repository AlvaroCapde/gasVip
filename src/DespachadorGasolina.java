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

    public DespachadorGasolina(int id, GasStation gasStation, int gasPumpTime, GasStationDashboard dashboard) {
        this.id = id;
        this.gasStation = gasStation;
        this.gasPumpTime = gasPumpTime * 1000L;
        this.estado = Estado.ESPERAR_CLIENTE;
        this.dashboard = dashboard;
    }

    public void actualizarEstado(Estado nuevoEstado) {
        Estado estadoAnterior = this.estado;
        this.estado = nuevoEstado;

        // Actualizar el dashboard
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