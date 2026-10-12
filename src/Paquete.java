public class Paquete extends Envio {
    private final double largo;
    private final double ancho;
    private final double alto;

    public Paquete(String codigo, String remitente, String destinatario,
                   double pesoReal, String destino,
                   double largo, double ancho, double alto) {
        super(codigo, remitente, destinatario, pesoReal, destino);
        this.largo = Validacion.positivo(largo);
        this.ancho = Validacion.positivo(ancho);
        this.alto = Validacion.positivo(alto);
        // También rechaza desbordamiento o subdesbordamiento del volumen.
        Validacion.positivo(largo * ancho * alto / 5000.0);
    }

    public double pesoVolumetrico() {
        return largo * ancho * alto / 5000.0;
    }

    public double pesoFacturable() {
        return Math.ceil(Math.max(getPesoReal(), pesoVolumetrico()));
    }

    @Override
    public double calcularCosto() {
        return Validacion.dinero(tarifaBase() + 8 * pesoFacturable());
    }

    @Override
    public int estimarDias() {
        return getDestino().equals("URBANO") ? 2 : 4;
    }

    @Override
    public int maxIntentos() { return 3; }

    @Override
    public String getCategoria() { return "Paquete"; }
}
