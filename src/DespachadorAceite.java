public class DespachadorAceite extends Thread {

    public enum Estado {
        ESPERAR_CLIENTE, SUMINISTRANDO_ACEITE
    }

    private int id;
    private Estado estado;
    private GasStation gasStation;

    public DespachadorAceite(int id, GasStation gasStation) {
        this.id = id;
        this.gasStation = gasStation;
        this.estado = Estado.ESPERAR_CLIENTE;
    }

    private void actualizarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("Despachador Aceite " + id + " - Estado: " + estado);
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
        actualizarEstado(Estado.SUMINISTRANDO_ACEITE);
        cliente.actualizarUIDuranteCarga("Cargando aceite");
        System.out.println("Despachador Aceite " + id + " está suministrando aceite para Cliente " + cliente.getId());
        Thread.sleep(1500);
        gasStation.releaseOilStation();
        cliente.finalizarCarga();
    }
}
