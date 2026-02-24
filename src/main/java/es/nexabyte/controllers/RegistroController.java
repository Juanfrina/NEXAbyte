package es.nexabyte.controllers;

import es.nexabyte.beans.Usuario;
import es.nexabyte.models.SecurityUtils;
import es.nexabyte.models.UsuarioService;
import es.nexabyte.models.Utils;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.beanutils.BeanUtils;

/**
 * Controlador para el registro de nuevos usuarios.
 * Gestiona la validación del formulario, la subida del avatar
 * y la inserción del usuario en la base de datos.
 * 
 * @author jfco1
 */
@WebServlet(name = "RegistroController", urlPatterns = {"/RegistroController"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 50,        // 50 KB
    maxFileSize       = 100 * 1024,      // 100 KB
    maxRequestSize    = 1024 * 1024       // 1 MB (formulario completo)
)
public class RegistroController extends HttpServlet {

    /** Servicio para operaciones de usuario. */
    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Muestra el formulario de registro (GET).
     *
     * @param request  la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException      si falla la E/S.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
    }

    /**
     * Procesa el formulario de registro (POST).
     * Valida campos obligatorios, comprueba email duplicado,
     * encripta la contraseña y sube el avatar.
     *
     * @param request  la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException      si falla la E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        // Recoger parámetros que necesitan validación especial
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        String nif = request.getParameter("nif");
        String nombre = request.getParameter("nombre");
        String apellidos = request.getParameter("apellidos");
        String direccion = request.getParameter("direccion");
        String codigoPostal = request.getParameter("codigoPostal");
        String localidad = request.getParameter("localidad");
        String provincia = request.getParameter("provincia");

        // Validaciones servidor
        // 1. Campos obligatorios (todos los NOT NULL de la BD)
        if (Utils.esVacio(email) || Utils.esVacio(password) || Utils.esVacio(password2)
                || Utils.esVacio(nombre) || Utils.esVacio(apellidos) || Utils.esVacio(nif)
                || Utils.esVacio(direccion) || Utils.esVacio(codigoPostal)
                || Utils.esVacio(localidad) || Utils.esVacio(provincia)) {
            String msg = "Los campos marcados con * son obligatorios.";
            if (esAjax) { responderAjax(response, "error:" + msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 2. Contraseñas coinciden
        if (!password.equals(password2)) {
            String msg = "Las contraseñas no coinciden.";
            if (esAjax) { responderAjax(response, "error:" + msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 3. Longitud mínima contraseña
        if (password.length() < 6) {
            String msg = "La contraseña debe tener al menos 6 caracteres.";
            if (esAjax) { responderAjax(response, "error:" + msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 4. NIF: 8 dígitos
        if (!nif.matches("[0-9]{8}")) {
            String msg = "El NIF debe contener exactamente 8 dígitos.";
            if (esAjax) { responderAjax(response, "error:" + msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 5. Código postal válido (obligatorio)
        if (!Utils.validarCodigoPostal(codigoPostal)) {
            String msg = "El código postal no es válido (5 dígitos, 01-52).";
            if (esAjax) { responderAjax(response, "error:" + msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 6. Email duplicado
        if (usuarioService.existeEmail(email.trim())) {
            String msg = "Ya existe una cuenta con ese correo electrónico.";
            if (esAjax) { responderAjax(response, "error:" + msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // Procesar avatar: prioridad al archivo subido, si no al predefinido
        String avatarNombre = procesarAvatar(request);
        // Si procesarAvatar puso un error (archivo inválido), volver al formulario
        if (avatarNombre == null && request.getAttribute("error") != null) {
            if (esAjax) { responderAjax(response, "error:" + request.getAttribute("error")); return; }
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }
        if (avatarNombre == null) {
            avatarNombre = "default.png";
        }

        // Cargar bean Usuario con BeanUtils (patrón requerido por el enunciado)
        Usuario usuario = new Usuario();
        try {
            BeanUtils.populate(usuario, request.getParameterMap());
        } catch (IllegalAccessException | InvocationTargetException e) {
            String msg = "Error al procesar el formulario.";
            if (esAjax) { responderAjax(response, "error:" + msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // Sobrescribir campos que requieren tratamiento especial
        usuario.setEmail(email.trim());
        usuario.setPassword(SecurityUtils.encriptarMD5(password));
        usuario.setNif(nif + Utils.calcularLetraNIF(nif));
        usuario.setAvatar(avatarNombre);

        // Insertar en BD a través del servicio
        boolean insertado = usuarioService.registrar(usuario);

        if (insertado) {
            if (esAjax) { responderAjax(response, "ok"); return; }
            request.setAttribute("mensaje", "Registro completado. Ya puedes acceder con tu cuenta.");
            request.getRequestDispatcher("/JSP/avisos/notificacion.jsp").forward(request, response);
        } else {
            String msg = "No se pudo completar el registro. Inténtalo de nuevo.";
            if (esAjax) { responderAjax(response, "error:" + msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
        }
    }

    /**
     * Envía una respuesta JSON para peticiones Ajax.
     * Usa com.google.gson.JsonObject para construir la respuesta.
     * Formato: {"resultado": "ok"} o {"resultado": "error", "mensaje": "..."}
     *
     * @param response la respuesta HTTP donde se escribe el JSON.
     * @param texto    texto con el resultado; si empieza por "error:" se marca como error.
     * @throws IOException si falla la escritura.
     */
    private void responderAjax(HttpServletResponse response, String texto) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        JsonObject json = new JsonObject();
        if (texto.startsWith("error:")) {
            json.addProperty("resultado", "error");
            json.addProperty("mensaje", texto.substring(6));
        } else {
            json.addProperty("resultado", texto);
        }
        response.getWriter().write(json.toString());
    }

    /**
     * Devuelve info breve del servlet.
     *
     * @return descripción corta.
     */
    @Override
    public String getServletInfo() {
        return "Controlador de registro de nuevos usuarios.";
    }

    /** Tamaño máximo permitido para el avatar (100 KB). */
    private static final long MAX_AVATAR_SIZE = 100 * 1024; // 100 KB
    /** Tipos MIME aceptados para el avatar. */
    private static final String[] TIPOS_PERMITIDOS = {"image/jpeg", "image/png"};

    /**
     * Procesa el avatar del formulario de registro.
     * Si el usuario subió un archivo, lo guarda en IMG/avatar/ con nombre único.
     * Si no, usa el avatar predefinido seleccionado.
     *
     * @param request la petición HTTP.
     * @return el nombre del archivo avatar a guardar en la BD.
     * @throws IOException      si falla la E/S.
     * @throws ServletException si ocurre un error del servlet.
     */

    private String procesarAvatar(HttpServletRequest request) throws IOException, ServletException {
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
            String rutaAvatar = getServletContext().getRealPath("/IMG/avatar");
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

        // 2. Si no hay archivo, usar avatar predefinido
        String predefinido = request.getParameter("avatarPredefinido");
        if ("Hombre.jpg".equals(predefinido) || "Mujer.jpg".equals(predefinido) || "Otro.jpg".equals(predefinido)) {
            return predefinido;
        }
        // Si no eligió ningún avatar, devolver null (se usará default.png)
        return null;
    }

    /**
     * Obtiene la extensión del archivo subido a partir del nombre original.
     *
     * @param part la parte del formulario multipart.
     * @return la extensión con punto (ej: ".jpg"), o ".jpg" por defecto.
     */
    private String obtenerExtension(Part part) {
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
