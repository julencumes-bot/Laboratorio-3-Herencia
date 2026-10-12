import java.util.NoSuchElementException;
import java.util.Scanner;

public class Vista {
    private final Scanner entrada;

    public Vista() { entrada = new Scanner(System.in, "UTF-8"); }

    public void mostrar(String texto) { System.out.println(texto); }

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje + " ");
            if (!entrada.hasNextLine()) {
                throw new NoSuchElementException("Fin de la entrada.");
            }
            String texto = entrada.nextLine().trim();
            if (!texto.isEmpty()) { return texto; }
            mostrar("Error: no se permite texto vacío.");
        }
    }

    public int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException error) {
                mostrar("Error: ingrese un número entero válido.");
            }
        }
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            try {
                double valor = Double.parseDouble(
                        leerTexto(mensaje).replace(',', '.'));
                if (Double.isFinite(valor)) { return valor; }
            } catch (NumberFormatException error) {
                // El siguiente mensaje permite repetir la misma entrada.
            }
            mostrar("Error: ingrese un número decimal finito.");
        }
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje + " (s/n)");
            if (valor.equalsIgnoreCase("s") || valor.equalsIgnoreCase("si")
                    || valor.equalsIgnoreCase("sí")) { return true; }
            if (valor.equalsIgnoreCase("n")
                    || valor.equalsIgnoreCase("no")) { return false; }
            mostrar("Error: responda s o n.");
        }
    }

    public int menu() {
        mostrar("\nENVÍAGT\n"
                + "1. Cotizar y registrar envío\n"
                + "2. Registrar vehículo\n"
                + "3. Crear despacho\n"
                + "4. Asignar envío\n"
                + "5. Retirar envío\n"
                + "6. Hacer salir despacho\n"
                + "7. Registrar resultado de entrega\n"
                + "8. Consultar envíos\n"
                + "9. Consultar vehículos\n"
                + "10. Generar reportes\n"
                + "0. Salir");
        return leerEntero("Opción:");
    }
}
