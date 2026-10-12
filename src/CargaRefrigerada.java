public class CargaRefrigerada extends Envio {
    private final double temperatura;

    public CargaRefrigerada(String codigo, String remitente,
                           String destinatario, double pesoReal,
                           String destino, double temperatura) {
        super(codigo, remitente, destinatario, pesoReal, destino);
        if (!getDestino().equals("URBANO")) {
            throw new IllegalArgumentException(
                    "La carga refrigerada solo acepta destino urbano.");
        }
        if (!Double.isFinite(temperatura)
                || temperatura < -25 || temperatura > 8) {
            throw new IllegalArgumentException(
                    "Temperatura permitida: de -25 a 8 grados Celsius.");
        }
        this.temperatura = temperatura;
    }

    @Override
    public double calcularCosto() {
        return Validacion.dinero(tarifaBase() + 12 * getPesoReal() + 60);
    }

    @Override
    public int estimarDias() { return 1; }

    @Override
    public int maxIntentos() { return 1; }

    @Override
    public String getCategoria() { return "Carga refrigerada"; }

    @Override
    public boolean esCompatible(Vehiculo vehiculo) {
        return vehiculo != null && vehiculo.isRefrigerado();
    }
}
