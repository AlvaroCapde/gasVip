import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class GasStation {

    private Semaphore gasPumps;
    private Semaphore oilStations;
    private ConcurrentLinkedQueue<Cliente> clientesEsperandoGasolina;
    private ConcurrentLinkedQueue<Cliente> clientesEsperandoAceite;
    private ConcurrentLinkedQueue<Cliente> clientesEsperandoFacturacion;


    public GasStation(int numGasPumps, int numOilStations) {
        this.gasPumps = new Semaphore(numGasPumps, true);
        this.oilStations = new Semaphore(numOilStations, true);
        this.clientesEsperandoGasolina = new ConcurrentLinkedQueue<>();
        this.clientesEsperandoAceite = new ConcurrentLinkedQueue<>();
        this.clientesEsperandoFacturacion = new ConcurrentLinkedQueue<>();
    }

    // Métodos para trabajar con bombas de gasolina
    public boolean reserveGasPump() {
        try {
            gasPumps.acquire();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void releaseGasPump() {
        gasPumps.release();
    }

    // Métodos para trabajar con estaciones de aceite
    public boolean reserveOilStation() {
        try {
            oilStations.acquire();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void releaseOilStation() {
        oilStations.release();
    }

    // Métodos para clientes esperando
    public void agregarClienteEsperandoGasolina(Cliente cliente) {
        clientesEsperandoGasolina.add(cliente);
    }

    public Cliente obtenerClienteEsperandoGasolina() {
        return clientesEsperandoGasolina.poll();
    }

    public void agregarClienteEsperandoAceite(Cliente cliente) {
        clientesEsperandoAceite.add(cliente);
    }

    public Cliente obtenerClienteEsperandoAceite() {
        return clientesEsperandoAceite.poll();
    }
    public void agregarClienteEsperandoFacturacion(Cliente cliente) {
        clientesEsperandoFacturacion.add(cliente);
    }

    public Cliente obtenerClienteEsperandoFacturacion() {
        return clientesEsperandoFacturacion.poll();
    }
}