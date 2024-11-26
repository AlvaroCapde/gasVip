public class DespachadorGasolina extends Thread {

    public enum Estado {
        ESPERAR_CLIENTE, CARGANDO_GASOLINA
    }

    private int id;
    private Estado estado;
    private GasStation gasStation;

    public DespachadorGasolina(int id, GasStation gasStation) {
        this.id = id;
        this.gasStation = gasStation;
        this.estado = Estado.ESPERAR_CLIENTE;
    }

    private void actualizarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("Despachador Gasolina " + id + " - Estado: " + estado);
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
        actualizarEstado(Estado.CARGANDO_GASOLINA);
        cliente.actualizarUIDuranteCarga("Cargando gasolina...");
        System.out.println("Despachador Gasolina " + id + " está cargando gasolina para Cliente " + cliente.getId());
        Thread.sleep(2000); // Simular tiempo de carga
        gasStation.releaseGasPump(); // Liberar la bomba
        System.out.println("Despachador Gasolina " + id + " ha terminado de cargar para Cliente " + cliente.getId());
        cliente.finalizarCarga(); // Notificar al cliente que el servicio ha terminado
    }
}