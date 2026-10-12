public final class Main {
    private Main() { }

    public static void main(String[] args) {
        SistemaEnvios sistema = new SistemaEnvios();
        sistema.cargarDatosIniciales();
        Vista vista = new Vista();
        new Controlador(sistema, vista).iniciar();
    }
}
