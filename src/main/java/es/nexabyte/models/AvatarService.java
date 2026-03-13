package es.nexabyte.models;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

/**
 * Servicio para el procesamiento de avatares de usuario.
 * Gestiona la subida, validación y almacenamiento de imágenes de perfil.
 * Compartido por GestionUsuario y RegistroController.
 *
 * @author jfco1
 */
public class AvatarService {

    /** Tamaño máximo permitido para el avatar (100 KB). */
    private static final long MAX_AVATAR_SIZE = 100 * 1024;
    /** Tipos MIME aceptados para el avatar. */
    private static final String[] TIPOS_PERMITIDOS = {"image/jpeg", "image/png"};

    /**
     * Procesa el avatar del formulario.
     * Si el usuario subió un archivo, lo guarda en IMG/avatar/ con nombre único.
     * Si eligió un predefinido, devuelve ese nombre.
     * Si no se seleccionó nada, devuelve null.
     *
     * @param request la petición HTTP (para obtener el Part y parámetros).
     * @param servletContext el contexto del servlet (para obtener la ruta real).
     * @return el nombre del archivo avatar, o null si no se cambió/seleccionó.
     * @throws IOException si falla la E/S.
     * @throws ServletException si ocurre un error del servlet.
     */
    public String procesarAvatar(HttpServletRequest request, ServletContext servletContext)
            throws IOException, ServletException {
        // 1. Intentar obtener archivo subido
        Part filePart = request.getPart("avatarFile");
        if (filePart != null && filePart.getSize() > 0) {
            // Validar tamaño (máx 100 KB)
            if (filePart.getSize() > MAX_AVATAR_SIZE) {
                request.setAttribute("error", "La imagen no puede superar los 100 KB.");
                return null;
            }
            // Validar tipo MIME (solo JPG y PNG)
            String contentType = filePart.getContentType();
            boolean tipoValido = false;
            if (contentType != null) {
                for (String tipo : TIPOS_PERMITIDOS) {
                    if (tipo.equals(contentType)) {
                        tipoValido = true;
                        break;
                    }
                }
            }
            if (!tipoValido) {
                request.setAttribute("error", "Solo se permiten imágenes JPG o PNG.");
                return null;
            }
            // Validar extensión real del archivo
            String extension = obtenerExtension(filePart);
            if (!".jpg".equals(extension) && !".jpeg".equals(extension) && !".png".equals(extension)) {
                request.setAttribute("error", "Solo se permiten imágenes JPG o PNG.");
                return null;
            }
            // Generar nombre único
            String nombreArchivo = "user_" + System.currentTimeMillis() + extension;

            // Ruta real en el servidor
            String rutaAvatar = servletContext.getRealPath("/IMG/avatar");
            File carpetaAvatar = new File(rutaAvatar);
            if (!carpetaAvatar.exists()) {
                carpetaAvatar.mkdirs();
            }

            // Guardar el archivo
            File destino = new File(carpetaAvatar, nombreArchivo);
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return nombreArchivo;
        }

        // 2. Si no hay archivo, comprobar predefinido
        String predefinido = request.getParameter("avatarPredefinido");
        if ("Hombre.jpg".equals(predefinido) || "Mujer.jpg".equals(predefinido) || "Otro.jpg".equals(predefinido)) {
            return predefinido;
        }

        // Si no eligió ningún avatar, devolver null
        return null;
    }

    /**
     * Obtiene la extensión del archivo subido a partir del nombre original.
     *
     * @param part la parte del formulario multipart.
     * @return la extensión con punto (ej: ".jpg"), o ".jpg" por defecto.
     */
    public String obtenerExtension(Part part) {
        String header = part.getHeader("content-disposition");
        if (header != null) {
            for (String token : header.split(";")) {
                if (token.trim().startsWith("filename")) {
                    String nombreOriginal = token.substring(token.indexOf('=') + 1).trim()
                            .replace("\"", "");
                    int punto = nombreOriginal.lastIndexOf('.');
                    if (punto > 0) {
                        return nombreOriginal.substring(punto).toLowerCase();
                    }
                }
            }
        }
        return ".jpg";
    }
}
