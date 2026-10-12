import java.util.ArrayList;
import java.util.Locale;

public class SistemaEnvios {
    private final ArrayList<Envio> envios;
    private final ArrayList<Vehiculo> vehiculos;
    private final ArrayList<Despacho> despachos;
    private int siguienteId;

    public SistemaEnvios() {
        envios = new ArrayList<>();
        vehiculos = new ArrayList<>();
        despachos = new ArrayList<>();
        siguienteId = 1;
    }

    public String cotizar(Envio envio) {
        if (envio == null) {
            throw new IllegalArgumentException("Debe indicar un envío.");
        }
        double costo = Validacion.dinero(envio.calcularCosto());
        return String.format(Locale.US,
                "%s | costo Q%.2f | entrega %d día(s) hábil(es)",
                envio.getCategoria(), costo, envio.estimarDias());
    }

    public void registrarEnvio(Envio envio) {
        agregarEnvio(envio, true);
    }

    private void agregarEnvio(Envio envio, boolean cobrar) {
        if (envio == null || !envio.getEstado().equals("BODEGA")
                || envio.getIntentos() != 0 || envio.getIngresoRegistro() != 0) {
            throw new IllegalArgumentException("Envío inválido para registrar.");
        }
        for (Envio actual : envios) {
            if (actual.getCodigo().equals(envio.getCodigo())) {
                throw new IllegalArgumentException("Código de envío repetido.");
            }
        }
        double monto = Validacion.dinero(envio.calcularCosto());
        Validacion.positivo(monto);
        Validacion.dinero(ingresoRegistro() + monto + ingresoReintentos());
        if (cobrar) { envio.cobrarRegistro(monto); }
        envios.add(envio);
    }

    public Envio buscarEnvio(String codigo) {
        String clave = Validacion.texto(codigo).toUpperCase(Locale.ROOT);
        for (Envio envio : envios) {
            if (envio.getCodigo().equals(clave)) { return envio; }
        }
        throw new IllegalArgumentException("No existe el envío " + clave);
    }

    public void registrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("Debe indicar un vehículo.");
        }
        for (Vehiculo actual : vehiculos) {
            if (actual.getPlaca().equals(vehiculo.getPlaca())) {
                throw new IllegalArgumentException("Placa repetida.");
            }
        }
        vehiculos.add(vehiculo);
    }

    public Vehiculo buscarVehiculo(String placa) {
        String clave = Validacion.texto(placa).toUpperCase(Locale.ROOT);
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getPlaca().equals(clave)) { return vehiculo; }
        }
        throw new IllegalArgumentException("No existe el vehículo " + clave);
    }

    public Despacho crearDespacho(String placa) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        for (Despacho despacho : despachos) {
            if (despacho.getVehiculo() == vehiculo
                    && !despacho.getEstado().equals("CERRADO")) {
                throw new IllegalArgumentException("El vehículo está ocupado.");
            }
        }
        if (siguienteId == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Se agotaron los identificadores.");
        }
        Despacho despacho = new Despacho(siguienteId, vehiculo);
        despachos.add(despacho);
        siguienteId++;
        return despacho;
    }

    public Despacho buscarDespacho(int id) {
        for (Despacho despacho : despachos) {
            if (despacho.getId() == id) { return despacho; }
        }
        throw new IllegalArgumentException("No existe el despacho " + id);
    }

    public void asignarEnvio(int id, String codigo) {
        buscarDespacho(id).asignar(buscarEnvio(codigo));
    }

    public void retirarEnvio(int id, String codigo) {
        buscarDespacho(id).retirar(buscarEnvio(codigo));
    }

    public void salirDespacho(int id) {
        buscarDespacho(id).salir();
    }

    public void registrarResultado(int id, String codigo, boolean entregado) {
        buscarDespacho(id).registrarResultado(buscarEnvio(codigo), entregado);
    }

    public double ingresoRegistro() {
        double total = 0;
        for (Envio envio : envios) { total += envio.getIngresoRegistro(); }
        return Validacion.dinero(total);
    }

    public double ingresoReintentos() {
        double total = 0;
        for (Envio envio : envios) { total += envio.getIngresoReintentos(); }
        return Validacion.dinero(total);
    }

    public String consultarEnvios() {
        StringBuilder texto = new StringBuilder("ENVÍOS\n");
        for (Envio envio : envios) { texto.append(envio).append('\n'); }
        return texto.toString();
    }

    public String consultarVehiculos() {
        StringBuilder texto = new StringBuilder("VEHÍCULOS\n");
        for (Vehiculo vehiculo : vehiculos) {
            boolean ocupado = false;
            for (Despacho despacho : despachos) {
                if (despacho.getVehiculo() == vehiculo
                        && !despacho.getEstado().equals("CERRADO")) {
                    ocupado = true;
                }
            }
            texto.append(vehiculo).append(ocupado ? " | OCUPADO" : " | LIBRE")
                    .append('\n');
        }
        return texto.toString();
    }

    public String generarReportes() {
        String[] estados = {"BODEGA", "ASIGNADO", "EN_RUTA",
                            "ENTREGADO", "DEVUELTO"};
        ArrayList<String> categorias = new ArrayList<>();
        for (Envio envio : envios) {
            if (!categorias.contains(envio.getCategoria())) {
                categorias.add(envio.getCategoria());
            }
        }
        StringBuilder texto = new StringBuilder("CANTIDADES POR CATEGORÍA\n");
        int[] totales = new int[estados.length];
        for (String categoria : categorias) {
            texto.append(categoria);
            double registro = 0;
            double reintentos = 0;
            int[] cantidades = new int[estados.length];
            for (Envio envio : envios) {
                if (envio.getCategoria().equals(categoria)) {
                    registro += envio.getIngresoRegistro();
                    reintentos += envio.getIngresoReintentos();
                    for (int i = 0; i < estados.length; i++) {
                        if (envio.getEstado().equals(estados[i])) {
                            cantidades[i]++;
                            totales[i]++;
                        }
                    }
                }
            }
            for (int i = 0; i < estados.length; i++) {
                texto.append(" | ").append(estados[i]).append('=')
                        .append(cantidades[i]);
            }
            texto.append(String.format(Locale.US,
                    " | registro Q%.2f | reintentos Q%.2f | total Q%.2f%n",
                    registro, reintentos, registro + reintentos));
        }
        texto.append("TOTALES POR ESTADO");
        for (int i = 0; i < estados.length; i++) {
            texto.append(" | ").append(estados[i]).append('=')
                    .append(totales[i]);
        }
        texto.append(String.format(Locale.US,
                "%nINGRESOS | registro Q%.2f | reintentos Q%.2f"
                + " | total Q%.2f%nDESPACHOS ABIERTOS%n",
                ingresoRegistro(), ingresoReintentos(),
                ingresoRegistro() + ingresoReintentos()));
        boolean hayAbiertos = false;
        for (Despacho despacho : despachos) {
            if (!despacho.getEstado().equals("CERRADO")) {
                texto.append(despacho).append('\n');
                hayAbiertos = true;
            }
        }
        if (!hayAbiertos) { texto.append("Ninguno.\n"); }
        return texto.toString();
    }

    public void cargarDatosIniciales() {
        if (!envios.isEmpty() || !vehiculos.isEmpty() || !despachos.isEmpty()) {
            throw new IllegalArgumentException(
                    "La precarga exige un sistema completamente vacío.");
        }
        agregarEnvio(new Sobre("S01", "Ana", "Luis", 0.5, "URBANO"), false);
        agregarEnvio(new Sobre("S02", "Eva", "José", 0.3,
                "DEPARTAMENTAL"), false);
        agregarEnvio(new Paquete("P01", "Ana", "Luis", 3.2,
                "URBANO", 40, 30, 20), false);
        agregarEnvio(new Paquete("P02", "Eva", "José", 2.1,
                "DEPARTAMENTAL", 10, 10, 10), false);
        agregarEnvio(new PaqueteFragil("F01", "Ana", "Luis", 3.2,
                "URBANO", 40, 30, 20, 1200), false);
        agregarEnvio(new PaqueteFragil("F02", "Eva", "José", 2,
                "DEPARTAMENTAL", 10, 10, 10, 500), false);
        agregarEnvio(new CargaRefrigerada("R01", "Ana", "Luis", 2.5,
                "URBANO", -25), false);
        agregarEnvio(new CargaRefrigerada("R02", "Eva", "José", 4,
                "URBANO", 8), false);
        registrarVehiculo(new Vehiculo("C001AAA", 5, false));
        registrarVehiculo(new Vehiculo("C002BBB", 20, true));
        registrarVehiculo(new Vehiculo("C003CCC", 10, false));
    }
}
