import java.util.Locale;

public abstract class Envio {
    private final String codigo;
    private final String remitente;
    private final String destinatario;
    private final double pesoReal;
    private final String destino;
    private String estado;
    private int intentos;
    private double ingresoRegistro;

    protected Envio(String codigo, String remitente,
                    String destinatario, double pesoReal, String destino) {
        this.codigo = Validacion.texto(codigo).toUpperCase(Locale.ROOT);
        this.remitente = Validacion.texto(remitente);
        this.destinatario = Validacion.texto(destinatario);
        this.pesoReal = Validacion.positivo(pesoReal);
        this.destino = Validacion.texto(destino).toUpperCase(Locale.ROOT);
        if (!this.destino.equals("URBANO")
                && !this.destino.equals("DEPARTAMENTAL")) {
            throw new IllegalArgumentException("Destino inválido.");
        }
        estado = "BODEGA";
        intentos = 0;
        ingresoRegistro = 0;
    }

    public String getCodigo() { return codigo; }
    public double getPesoReal() { return pesoReal; }
    public String getDestino() { return destino; }
    public String getEstado() { return estado; }
    public int getIntentos() { return intentos; }
    public double getIngresoRegistro() { return ingresoRegistro; }

    public double getIngresoReintentos() {
        return Math.max(0, intentos - 1) * 15.0;
    }

    protected double tarifaBase() {
        return destino.equals("URBANO") ? 25 : 40;
    }

    public abstract double calcularCosto();
    public abstract int estimarDias();
    public abstract int maxIntentos();
    public abstract String getCategoria();

    public boolean esCompatible(Vehiculo vehiculo) {
        return vehiculo != null;
    }

    void cobrarRegistro(double monto) {
        if (!estado.equals("BODEGA") || intentos != 0
                || ingresoRegistro != 0) {
            throw new IllegalArgumentException("Registro ya cobrado o inválido.");
        }
        ingresoRegistro = Validacion.dinero(Validacion.positivo(monto));
    }

    void asignar() {
        if (!estado.equals("BODEGA") || intentos >= maxIntentos()) {
            throw new IllegalArgumentException("Envío no disponible en bodega.");
        }
        estado = "ASIGNADO";
    }

    void retirar() {
        if (!estado.equals("ASIGNADO")) {
            throw new IllegalArgumentException("El envío no está asignado.");
        }
        estado = "BODEGA";
    }

    void iniciarIntento() {
        if (!estado.equals("ASIGNADO") || intentos >= maxIntentos()) {
            throw new IllegalArgumentException("No se puede iniciar el intento.");
        }
        intentos++;
        estado = "EN_RUTA";
    }

    void registrarResultado(boolean entregado) {
        if (!estado.equals("EN_RUTA")) {
            throw new IllegalArgumentException("El envío no está en ruta.");
        }
        if (entregado) {
            estado = "ENTREGADO";
        } else {
            estado = intentos < maxIntentos() ? "BODEGA" : "DEVUELTO";
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "%s | %s | %s -> %s | %.3f kg | %s | %s"
                + " | intentos %d/%d | registro Q%.2f | reintentos Q%.2f",
                codigo, getCategoria(), remitente, destinatario,
                pesoReal, destino, estado, intentos, maxIntentos(),
                ingresoRegistro, getIngresoReintentos());
    }
}
