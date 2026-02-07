package es.nexabyte.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase utilitaria para operaciones de seguridad.
 * @author jfco1
 */
public class SecurityUtils {
    
    /**
     * Encripta una contraseña usando MD5.
     * @param password La contraseña en texto plano
     * @return La contraseña encriptada en MD5 (32 caracteres hexadecimales)
     */
    public static String encriptarMD5(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña: " + e.getMessage(), e);
        }
    }
}
