import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Validacion {
    private Validacion() { }

    public static String texto(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto no puede estar vacío.");
        }
        return valor.trim();
    }

    public static double positivo(double valor) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(
                    "El número debe ser positivo y finito.");
        }
        return valor;
    }

    public static double dinero(double valor) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException("Monto fuera de rango.");
        }
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
