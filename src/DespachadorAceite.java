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

    public DespachadorAceite(int id, GasStation gasStation, int oilPumpTime, GasStationDashboard dashboard) {
        this.id = id;
        this.gasStation = gasStation;
        this.oilPumpTime = oilPumpTime * 1000L;
        this.estado = Estado.ESPERAR_CLIENTE;
        this.dashboard = dashboard;
    }

    public void actualizarEstado(Estado nuevoEstado) {
        Estado estadoAnterior = this.estado;
        this.estado = nuevoEstado;

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
        dashboard.updateCriticalZone("OilStation", -1); // Incrementar zona crítica

        cliente.finalizarCarga();
    }
}
