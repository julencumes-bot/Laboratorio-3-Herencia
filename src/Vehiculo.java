import java.util.Locale;

public class Vehiculo {
    private final String placa;
    private final double capacidad;
    private final boolean refrigerado;

    public Vehiculo(String placa, double capacidad, boolean refrigerado) {
        this.placa = Validacion.texto(placa).toUpperCase(Locale.ROOT);
        this.capacidad = Validacion.positivo(capacidad);
        this.refrigerado = refrigerado;
    }

    public String getPlaca() { return placa; }
    public double getCapacidad() { return capacidad; }
    public boolean isRefrigerado() { return refrigerado; }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "%s | capacidad %.3f kg | refrigerado: %s",
                placa, capacidad, refrigerado ? "sí" : "no");
    }
}
