import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GasStation {

    private Semaphore gasPumps;
    private Semaphore oilStations;
    private ConcurrentLinkedQueue<Cliente> clientesEsperandoGasolina;
    private ConcurrentLinkedQueue<Cliente> clientesEsperandoAceite;
    private final Queue<Cliente> clientesEsperandoFacturacion = new LinkedList<>();
    private final Lock facturacionLock = new ReentrantLock();
    private final Condition clienteDisponible = facturacionLock.newCondition();

    private GasStationDashboard dashboard;

    public GasStation(int numGasPumps, int numOilStations, GasStationDashboard dashboard) {
        this.gasPumps = new Semaphore(numGasPumps, true);
        this.oilStations = new Semaphore(numOilStations, true);
        this.clientesEsperandoGasolina = new ConcurrentLinkedQueue<>();
        this.clientesEsperandoAceite = new ConcurrentLinkedQueue<>();
        this.dashboard = dashboard;
    }

    // Métodos para trabajar con bombas de gasolina
    public boolean reserveGasPump() {
        try {
            gasPumps.acquire();
            dashboard.updateCriticalZone("GasPump", gasPumps.availablePermits());
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void releaseGasPump() {
        gasPumps.release();
        dashboard.updateCriticalZone("GasPump", gasPumps.availablePermits());
    }

    // Métodos para trabajar con estaciones de aceite
    public boolean reserveOilStation() {
        try {
            oilStations.acquire();
            dashboard.updateCriticalZone("OilStation", oilStations.availablePermits());
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void releaseOilStation() {
        oilStations.release();
        dashboard.updateCriticalZone("OilStation", oilStations.availablePermits());
    }

    // Agregar cliente esperando en gasolina
    public void agregarClienteEsperandoGasolina(Cliente cliente) {
        clientesEsperandoGasolina.add(cliente);
        dashboard.updateBuffer("GasPump", clientesEsperandoGasolina.size());
    }

    // Obtener cliente de la cola de gasolina
    public Cliente obtenerClienteEsperandoGasolina() {
        Cliente cliente = clientesEsperandoGasolina.poll();
        dashboard.updateBuffer("GasPump", clientesEsperandoGasolina.size());
        return cliente;
    }

    // Agregar cliente esperando en aceite
    public void agregarClienteEsperandoAceite(Cliente cliente) {
        clientesEsperandoAceite.add(cliente);
        dashboard.updateBuffer("OilStation", clientesEsperandoAceite.size());
    }

    // Obtener cliente de la cola de aceite
    public Cliente obtenerClienteEsperandoAceite() {
        Cliente cliente = clientesEsperandoAceite.poll();
        dashboard.updateBuffer("OilStation", clientesEsperandoAceite.size());
        return cliente;
    }

    // Agregar cliente esperando en facturación
    public void agregarClienteEsperandoFacturacion(Cliente cliente) {
        facturacionLock.lock();
        try {
            clientesEsperandoFacturacion.add(cliente);
            dashboard.updateBuffer("BillingStation", clientesEsperandoFacturacion.size());
            System.out.println("Cliente " + cliente.getId() + " agregado a la cola de facturación.");
            clienteDisponible.signal(); // Notificar a los facturadores
        } finally {
            facturacionLock.unlock();
        }
    }

    // Obtener cliente de la cola de facturación
    public Cliente obtenerClienteEsperandoFacturacion() throws InterruptedException {
        facturacionLock.lock();
        try {
            while (clientesEsperandoFacturacion.isEmpty()) {
                clienteDisponible.await(); // Esperar a un cliente
            }
            Cliente cliente = clientesEsperandoFacturacion.poll();
            dashboard.updateBuffer("BillingStation", clientesEsperandoFacturacion.size());
            return cliente;
        } finally {
            facturacionLock.unlock();
        }
    }
}