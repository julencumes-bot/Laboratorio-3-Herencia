public class Sobre extends Envio {
    public Sobre(String codigo, String remitente, String destinatario,
                 double pesoReal, String destino) {
        super(codigo, remitente, destinatario, pesoReal, destino);
        if (pesoReal > 0.5) {
            throw new IllegalArgumentException("El sobre supera 0.5 kg.");
        }
    }

    @Override
    public double calcularCosto() {
        return getDestino().equals("URBANO") ? 20 : 35;
    }

    @Override
    public int estimarDias() {
        return getDestino().equals("URBANO") ? 1 : 2;
    }

    @Override
    public int maxIntentos() { return 3; }

    @Override
    public String getCategoria() { return "Sobre"; }
}
