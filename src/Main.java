import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        GasStationConfigWindow configWindow = new GasStationConfigWindow();
        configWindow.setVisible(true);

        while (configWindow.isDisplayable()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (configWindow.isConfigSaved()) {
            startSimulation(configWindow);
        } else {
            System.out.println("La configuración no se guardó. Saliendo...");
        }
    }

    private static void startSimulation(GasStationConfigWindow configWindow) {
        int numClients = configWindow.getNumClients();
        int numPumps = configWindow.getNumPumps();
        int numOilMachines = configWindow.getNumOilMachines();
        int numGasDispatchers = configWindow.getGasDispatchers();
        int numOilDispatchers = configWindow.getOilDispatchers();
        int numFacturadores = configWindow.getNumBillers();
        System.out.println("Simulación iniciada con los datos configurados.");
        GasStationDashboard dashboard = new GasStationDashboard();
        dashboard.setVisible(true);

        GasStation gasStation = new GasStation(numPumps, numOilMachines);

        for (int i = 1; i <= numGasDispatchers; i++) {
            DespachadorGasolina despachador = new DespachadorGasolina(i, gasStation);
            despachador.start();
        }

        for (int i = 1; i <= numOilDispatchers; i++) {
            DespachadorAceite despachadorAceite = new DespachadorAceite(i, gasStation);
            despachadorAceite.start();
        }

        Lock billingLock = new ReentrantLock();

        for (int i = 1; i <= numFacturadores; i++) {
            Facturador facturador = new Facturador(i, gasStation, billingLock);
            facturador.start();
        }

        for (int i = 1; i <= numClients; i++) {
            Cliente cliente = new Cliente(i, gasStation);
            cliente.start();
        }
    }
}