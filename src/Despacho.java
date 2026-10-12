import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

public class Despacho {
    private final int id;
    private final Vehiculo vehiculo;
    private String estado;
    private final ArrayList<Envio> envios;
    private final ArrayList<Envio> pendientes;

    Despacho(int id, Vehiculo vehiculo) {
        if (id <= 0 || vehiculo == null) {
            throw new IllegalArgumentException("Datos del despacho inválidos.");
        }
        this.id = id;
        this.vehiculo = vehiculo;
        estado = "PREPARACION";
        envios = new ArrayList<>();
        pendientes = new ArrayList<>();
    }

    public int getId() { return id; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public String getEstado() { return estado; }

    public double pesoCargado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Envio envio : envios) {
            total = total.add(BigDecimal.valueOf(envio.getPesoReal()));
        }
        return total.doubleValue();
    }

    public double capacidadDisponible() {
        return BigDecimal.valueOf(vehiculo.getCapacidad())
                .subtract(BigDecimal.valueOf(pesoCargado())).doubleValue();
    }

    void asignar(Envio envio) {
        if (!estado.equals("PREPARACION")) {
            throw new IllegalArgumentException("Despacho fuera de preparación.");
        }
        if (envio == null || !envio.getEstado().equals("BODEGA")
                || envio.getIntentos() >= envio.maxIntentos()) {
            throw new IllegalArgumentException("Envío no disponible.");
        }
        if (!envio.esCompatible(vehiculo)) {
            throw new IllegalArgumentException("Vehículo incompatible.");
        }
        // Comparación decimal: 0.1 + 0.2 debe caber en 0.3 kg.
        BigDecimal total = BigDecimal.valueOf(envio.getPesoReal());
        for (Envio actual : envios) {
            total = total.add(BigDecimal.valueOf(actual.getPesoReal()));
        }
        if (total.compareTo(BigDecimal.valueOf(vehiculo.getCapacidad())) > 0) {
            throw new IllegalArgumentException("Capacidad insuficiente.");
        }
        envio.asignar();
        envios.add(envio);
    }

    void retirar(Envio envio) {
        if (!estado.equals("PREPARACION") || !envios.contains(envio)) {
            throw new IllegalArgumentException("No se puede retirar ese envío.");
        }
        envio.retirar();
        envios.remove(envio);
    }

    void salir() {
        if (!estado.equals("PREPARACION") || envios.isEmpty()) {
            throw new IllegalArgumentException(
                    "Solo puede salir un despacho en preparación y no vacío.");
        }
        // Primera pasada: validar todo antes de modificar un solo envío.
        for (Envio envio : envios) {
            if (!envio.getEstado().equals("ASIGNADO")
                    || envio.getIntentos() >= envio.maxIntentos()
                    || !envio.esCompatible(vehiculo)) {
                throw new IllegalArgumentException("Carga no válida para salir.");
            }
        }
        for (Envio envio : envios) {
            envio.iniciarIntento();
        }
        pendientes.addAll(envios);
        estado = "EN_RUTA";
    }

    void registrarResultado(Envio envio, boolean entregado) {
        if (!estado.equals("EN_RUTA") || !pendientes.contains(envio)) {
            throw new IllegalArgumentException(
                    "El envío no tiene resultado pendiente en este despacho.");
        }
        envio.registrarResultado(entregado);
        pendientes.remove(envio);
        if (pendientes.isEmpty()) {
            estado = "CERRADO";
        }
    }

    @Override
    public String toString() {
        StringBuilder codigos = new StringBuilder();
        for (Envio envio : envios) {
            if (codigos.length() > 0) { codigos.append(", "); }
            codigos.append(envio.getCodigo());
        }
        return String.format(Locale.US,
                "Despacho %d | %s | %s | cargado %.3f kg"
                + " | disponible %.3f kg | envíos [%s] | pendientes %d",
                id, vehiculo.getPlaca(), estado, pesoCargado(),
                capacidadDisponible(), codigos, pendientes.size());
    }
}
