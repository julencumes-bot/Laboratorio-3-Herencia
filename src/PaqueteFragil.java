public class PaqueteFragil extends Paquete {
    private final double valorDeclarado;

    public PaqueteFragil(String codigo, String remitente,
                        String destinatario, double pesoReal,
                        String destino, double largo, double ancho,
                        double alto, double valorDeclarado) {
        super(codigo, remitente, destinatario, pesoReal,
                destino, largo, ancho, alto);
        this.valorDeclarado = Validacion.positivo(valorDeclarado);
    }

    @Override
    public double calcularCosto() {
        return Validacion.dinero(
                super.calcularCosto() + 15 + 0.03 * valorDeclarado);
    }

    @Override
    public int estimarDias() { return super.estimarDias() + 1; }

    @Override
    public int maxIntentos() { return 2; }

    @Override
    public String getCategoria() { return "Paquete frágil"; }
}
