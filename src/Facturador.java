import java.util.concurrent.locks.Lock;

public class Facturador extends Thread {

    public enum Estado {
        ESPERANDO_CLIENTE, FACTURANDO
    }

    private int id;
    private Estado estado;
    private GasStation gasStation;
    private Lock billingLock;

    public Facturador(int id, GasStation gasStation, Lock billingLock) {
        this.id = id;
        this.gasStation = gasStation;
        this.billingLock = billingLock;
        this.estado = Estado.ESPERANDO_CLIENTE;
    }

    private void actualizarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("Facturador " + id + " - Estado: " + estado);
    }

    @Override
    public void run() {
        while (true) {
            try {
                actualizarEstado(Estado.ESPERANDO_CLIENTE);

                Cliente cliente = gasStation.obtenerClienteEsperandoFacturacion();
                if (cliente != null) {
                    System.out.println("FOUND 1");
                    facturarCliente(cliente);
                }
                Thread.sleep(500); // Esperar antes de buscar más clientes
            } catch (InterruptedException e) {
                System.out.println("Facturador " + id + " interrumpido.");
                break;
            }
        }
    }

    private void facturarCliente(Cliente cliente) throws InterruptedException {
        actualizarEstado(Estado.FACTURANDO);
        cliente.actualizarEstado(Cliente.Estado.PAGANDO);
        System.out.println("Facturador " + id + " está procesando pago para Cliente " + cliente.getId());
        Thread.sleep(1000); // Simular tiempo de facturación
        cliente.finalizarFacturacion(); // Notificar al cliente que el pago ha sido procesado
        actualizarEstado(Estado.ESPERANDO_CLIENTE);
    }
}
