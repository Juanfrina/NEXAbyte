package es.nexabyte.models;

/**
 * Clase de utilidades generales para la aplicación.
 * 
 * @author jfco1
 */
public class Utils {

    /** Porcentaje de IVA aplicado (21%). */
    public static final double IVA_PORCENTAJE = 21.0;

    /** Nombre de la cookie del carrito para usuarios anónimos. */
    public static final String COOKIE_CARRITO = "carrito_nexabyte";

    /** Duración de la cookie del carrito en segundos (2 días). */
    public static final int COOKIE_DURACION = 2 * 24 * 60 * 60;

    /**
     * Calcula la letra del NIF a partir de los 8 dígitos.
     * 
     * @param numeros los 8 dígitos del DNI.
     * @return la letra correspondiente al NIF.
     */
    public static String calcularLetraNIF(String numeros) {
        if (numeros == null || !numeros.matches("\\d{8}")) {
            return "";
        }
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int indice = Integer.parseInt(numeros) % 23;
        return String.valueOf(letras.charAt(indice));
    }

    /**
     * Calcula el importe del IVA sobre una base imponible.
     * 
     * @param baseImponible la base imponible (precio * cantidad de todos los productos).
     * @return el importe del IVA.
     */
    public static double calcularIVA(double baseImponible) {
        return Math.round(baseImponible * IVA_PORCENTAJE) / 100.0;
    }

    /**
     * Calcula el total con IVA incluido.
     * 
     * @param baseImponible la base imponible.
     * @return el total (base imponible + IVA).
     */
    public static double calcularTotal(double baseImponible) {
        return baseImponible + calcularIVA(baseImponible);
    }

    /**
     * Valida que un código postal tenga el formato correcto (5 dígitos).
     * 
     * @param cp el código postal a validar.
     * @return true si el formato es correcto.
     */
    public static boolean validarCodigoPostal(String cp) {
        return cp != null && cp.matches("\\d{5}");
    }

    /**
     * Comprueba si una cadena está vacía o es nula.
     * 
     * @param texto la cadena a comprobar.
     * @return true si es nula o está vacía.
     */
    public static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
