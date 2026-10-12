import java.util.NoSuchElementException;

public class Controlador {
    private final SistemaEnvios sistema;
    private final Vista vista;

    public Controlador(SistemaEnvios sistema, Vista vista) {
        if (sistema == null || vista == null) {
            throw new IllegalArgumentException("Modelo y vista obligatorios.");
        }
        this.sistema = sistema;
        this.vista = vista;
    }

    public void iniciar() {
        while (true) {
            try {
                int opcion = vista.menu();
                if (opcion == 0) {
                    vista.mostrar("Programa finalizado.");
                    return;
                }
                ejecutarOpcion(opcion);
            } catch (IllegalArgumentException error) {
                vista.mostrar("Error: " + error.getMessage());
            } catch (NoSuchElementException error) {
                vista.mostrar("Fin de la entrada. Programa finalizado.");
                return;
            }
        }
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1: {
                Envio envio = crearEnvio();
                vista.mostrar(sistema.cotizar(envio));
                if (vista.leerSiNo("¿Aceptar y registrar el envío?")) {
                    sistema.registrarEnvio(envio);
                    vista.mostrar("Envío registrado y cobrado.");
                } else {
                    vista.mostrar("Cotización sin registro ni cobro.");
                }
                break;
            }
            case 2: {
                String placa = vista.leerTexto("Placa:");
                double capacidad = vista.leerDecimal("Capacidad en kg:");
                boolean frio = vista.leerSiNo("¿Tiene refrigeración?");
                sistema.registrarVehiculo(new Vehiculo(placa, capacidad, frio));
                vista.mostrar("Vehículo registrado.");
                break;
            }
            case 3:
                vista.mostrar(sistema.crearDespacho(
                        vista.leerTexto("Placa del vehículo:")).toString());
                break;
            case 4:
                sistema.asignarEnvio(vista.leerEntero("ID del despacho:"),
                        vista.leerTexto("Código del envío:"));
                vista.mostrar("Envío asignado sin cobro.");
                break;
            case 5:
                sistema.retirarEnvio(vista.leerEntero("ID del despacho:"),
                        vista.leerTexto("Código del envío:"));
                vista.mostrar("Envío retirado y devuelto a bodega.");
                break;
            case 6:
                sistema.salirDespacho(vista.leerEntero("ID del despacho:"));
                vista.mostrar("Despacho en ruta; intentos actualizados.");
                break;
            case 7:
                sistema.registrarResultado(
                        vista.leerEntero("ID del despacho:"),
                        vista.leerTexto("Código del envío:"),
                        vista.leerSiNo("¿Fue entregado?"));
                vista.mostrar("Resultado registrado.");
                break;
            case 8:
                vista.mostrar(sistema.consultarEnvios());
                break;
            case 9:
                vista.mostrar(sistema.consultarVehiculos());
                break;
            case 10:
                vista.mostrar(sistema.generarReportes());
                break;
            default:
                throw new IllegalArgumentException("Opción fuera del menú.");
        }
    }

    private Envio crearEnvio() {
        vista.mostrar("1 Sobre | 2 Paquete | 3 Frágil | 4 Refrigerada");
        int categoria = vista.leerEntero("Categoría:");
        if (categoria < 1 || categoria > 4) {
            throw new IllegalArgumentException("Categoría inválida.");
        }
        String codigo = vista.leerTexto("Código:");
        String remitente = vista.leerTexto("Remitente:");
        String destinatario = vista.leerTexto("Destinatario:");
        double peso = vista.leerDecimal("Peso real en kg:");
        String destino = vista.leerTexto("Destino URBANO o DEPARTAMENTAL:");
        // La selección crea objetos; las reglas se resuelven por polimorfismo.
        switch (categoria) {
            case 1:
                return new Sobre(codigo, remitente, destinatario, peso, destino);
            case 2:
            case 3: {
                double largo = vista.leerDecimal("Largo en cm:");
                double ancho = vista.leerDecimal("Ancho en cm:");
                double alto = vista.leerDecimal("Alto en cm:");
                if (categoria == 2) {
                    return new Paquete(codigo, remitente, destinatario,
                            peso, destino, largo, ancho, alto);
                }
                double valor = vista.leerDecimal("Valor declarado en Q:");
                return new PaqueteFragil(codigo, remitente, destinatario,
                        peso, destino, largo, ancho, alto, valor);
            }
            case 4:
                return new CargaRefrigerada(codigo, remitente, destinatario,
                        peso, destino, vista.leerDecimal("Temperatura Celsius:"));
            default:
                throw new IllegalArgumentException("Categoría inválida.");
        }
    }
}
