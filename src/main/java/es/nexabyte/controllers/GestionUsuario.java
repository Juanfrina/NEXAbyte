package es.nexabyte.controllers;

import es.nexabyte.beans.Usuario;
import es.nexabyte.models.CarritoService;
import es.nexabyte.models.SecurityUtils;
import es.nexabyte.models.UsuarioService;
import es.nexabyte.models.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import org.json.JSONObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.beanutils.BeanUtils;

/**
 * Controlador para la gestión de usuarios: acceso, salir y edición de perfil.
 *
 * doGet: formAcceder, comprobarEmail (lectura).
 * doPost: acceder, salir, editarPerfil (escritura).
 *
 * @author jfco1
 */
@WebServlet(name = "GestionUsuario", urlPatterns = {"/GestionUsuario"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 50,        // 50 KB
    maxFileSize       = 100 * 1024,      // 100 KB
    maxRequestSize    = 1024 * 1024       // 1 MB (formulario completo)
)
public class GestionUsuario extends HttpServlet {

    /** Servicio para operaciones de usuario (login, registro, perfil). */
    private final UsuarioService usuarioService = new UsuarioService();
    /** Servicio para operaciones del carrito (transferencia cookie a BD). */
    private final CarritoService carritoService = new CarritoService();

    /**
     * GET: operaciones de lectura (formulario de acceso, comprobar email).
     *
     * @param request la petición HTTP con el parámetro "op".
     * @param response la respuesta HTTP.
     * @throws ServletException si hay error en el servlet.
     * @throws IOException si hay error de E/S.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String operacion = request.getParameter("op");
        if (operacion == null) {
            operacion = "formAcceder";
        }

        switch (operacion) {
            case "formAcceder":
                formAcceder(request, response);
                break;
            case "comprobarEmail":
                comprobarEmail(request, response);
                break;
            default:
                formAcceder(request, response);
                break;
        }
    }

    /**
     * POST: operaciones de escritura (acceder, salir, editarPerfil).
     *
     * @param request la petición HTTP con el parámetro "op".
     * @param response la respuesta HTTP.
     * @throws ServletException si hay error en el servlet.
     * @throws IOException si hay error de E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String operacion = request.getParameter("op");
        if (operacion == null) {
            response.sendRedirect(request.getContextPath() + "/GestionUsuario?op=formAcceder");
            return;
        }

        switch (operacion) {
            case "acceder":
                acceder(request, response);
                break;
            case "salir":
                salir(request, response);
                break;
            case "editarPerfil":
                editarPerfil(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/GestionUsuario?op=formAcceder");
                break;
        }
    }

    // ==================== OPERACIONES GET (lectura) ====================

    /**
     * Muestra el formulario de acceso (login).
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    private void formAcceder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/JSP/vistas/acceder.jsp").forward(request, response);
    }

    /**
     * Responde en JSON si el email ya existe (Ajax).
     * Devuelve {"estado": "existe"}, {"estado": "disponible"} o {"estado": "vacio"}.
     * Usa org.json.JSONObject para construir la respuesta.
     */
    private void comprobarEmail(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        String email = request.getParameter("email");

        JSONObject json = new JSONObject();

        if (email == null || email.trim().isEmpty()) {
            json.put("estado", "vacio");
        } else {
            boolean existe = usuarioService.existeEmail(email.trim());
            json.put("estado", existe ? "existe" : "disponible");
        }

        response.getWriter().write(json.toString());
    }

    // ==================== OPERACIONES POST (escritura) ====================

    /**
     * Valida las credenciales del usuario, abre sesión y transfiere
     * el carrito de cookie a la base de datos si es su primer acceso.
     *
     * @param request la petición HTTP con email y password.
     * @param response la respuesta HTTP.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    private void acceder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Limpiar espacios en email
        if (email != null) {
            email = email.trim();
        }

        if (Utils.esVacio(email) || Utils.esVacio(password)) {
            if (esAjax) {
                enviarJsonRespuesta(response, "error", "Debes rellenar todos los campos.");
                return;
            }
            request.setAttribute("error", "Debes rellenar todos los campos.");
            request.getRequestDispatcher("/JSP/vistas/acceder.jsp").forward(request, response);
            return;
        }

        Usuario usuario = usuarioService.autenticar(email, password);

        if (usuario == null) {
            if (esAjax) {
                enviarJsonRespuesta(response, "error", "Email o contraseña incorrectos.");
                return;
            }
            request.setAttribute("error", "Email o contraseña incorrectos.");
            request.setAttribute("emailAnterior", email);
            request.getRequestDispatcher("/JSP/vistas/acceder.jsp").forward(request, response);
            return;
        }

        // Abrir sesión
        HttpSession sesion = request.getSession();
        sesion.setAttribute("usuario", usuario);

        // Registrar último acceso
        usuarioService.registrarUltimoAcceso(usuario.getIdUsuario());

        // Carrito: primer login (recién registrado) vs usuario recurrente
        if (usuario.getUltimoAcceso() == null) {
            // Primer acceso: transferir carrito anónimo (cookie) a la BD del usuario
            carritoService.transferirCookieABD(request, response, usuario);
        } else {
            // Usuario recurrente: descartar carrito anónimo y conservar el suyo en BD
            carritoService.vaciarCookie(request, response);
        }

        // Sincronizar el badge con el carrito real del usuario en BD
        sesion.setAttribute("numArticulos", carritoService.contarArticulosBD(usuario));

        // Responder
        if (esAjax) {
            enviarJsonRespuesta(response, "ok", null);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/FrontController?op=inicio");
    }

    /**
     * Cierra la sesión del usuario e invalida la sesión HTTP.
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws IOException si falla la redirección.
     */
    private void salir(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/FrontController?op=inicio");
    }

    /**
     * Actualiza los datos del perfil del usuario en sesión.
     * Usa BeanUtils.populate() para cargar los campos del formulario.
     * Para cambiar contraseña: se pide la actual + nueva (x2).
     * Email y NIF no se pueden modificar.
     *
     * @param request la petición HTTP con los datos del formulario.
     * @param response la respuesta HTTP.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    private void editarPerfil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/GestionUsuario?op=formAcceder");
            return;
        }

        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");

        // Guardar campos no editables
        String emailOriginal = usuarioSesion.getEmail();
        String nifOriginal = usuarioSesion.getNif();
        Short idOriginal = usuarioSesion.getIdUsuario();
        String passwordOriginal = usuarioSesion.getPassword();
        String avatarOriginal = usuarioSesion.getAvatar();

        // Cargar campos del formulario con BeanUtils
        try {
            BeanUtils.populate(usuarioSesion, request.getParameterMap());
        } catch (IllegalAccessException | InvocationTargetException e) {
            request.setAttribute("error", "Error al procesar el formulario.");
            request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
            return;
        }

        // Restaurar campos no editables
        usuarioSesion.setEmail(emailOriginal);
        usuarioSesion.setNif(nifOriginal);
        usuarioSesion.setIdUsuario(idOriginal);
        usuarioSesion.setPassword(passwordOriginal);

        // Avatar: procesar archivo subido o predefinido
        String nuevoAvatar = procesarAvatar(request);
        // Si procesarAvatar puso un error (archivo inválido), volver al formulario
        if (nuevoAvatar == null && request.getAttribute("error") != null) {
            usuarioSesion.setEmail(emailOriginal);
            usuarioSesion.setNif(nifOriginal);
            usuarioSesion.setIdUsuario(idOriginal);
            usuarioSesion.setPassword(passwordOriginal);
            usuarioSesion.setAvatar(avatarOriginal);
            request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
            return;
        }
        if (nuevoAvatar != null) {
            usuarioSesion.setAvatar(nuevoAvatar);
        } else {
            usuarioSesion.setAvatar(avatarOriginal);
        }

        // Gestión del cambio de contraseña (actual + nueva x2)
        String passwordActual = request.getParameter("passwordActual");
        String passwordNueva = request.getParameter("passwordNueva");
        String passwordNueva2 = request.getParameter("passwordNueva2");

        if (!Utils.esVacio(passwordNueva)) {
            // Verificar que se ha introducido la contraseña actual
            if (Utils.esVacio(passwordActual)) {
                request.setAttribute("error", "Debes introducir tu contraseña actual para cambiarla.");
                request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
                return;
            }
            // Verificar que la contraseña actual es correcta
            String actualMD5 = SecurityUtils.encriptarMD5(passwordActual);
            if (!actualMD5.equals(passwordOriginal)) {
                request.setAttribute("error", "La contraseña actual no es correcta.");
                request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
                return;
            }
            // Verificar que las nuevas coinciden
            if (!passwordNueva.equals(passwordNueva2)) {
                request.setAttribute("error", "Las nuevas contraseñas no coinciden.");
                request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
                return;
            }
            // Verificar longitud mínima
            if (passwordNueva.length() < 6) {
                request.setAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
                request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
                return;
            }
            // Aplicar nueva contraseña
            usuarioSesion.setPassword(SecurityUtils.encriptarMD5(passwordNueva));
        }

        boolean exito = usuarioService.actualizarPerfil(usuarioSesion, null);

        if (exito) {
            sesion.setAttribute("usuario", usuarioSesion);
            sesion.setAttribute("mensajeFlash", "Perfil actualizado correctamente.");
            response.sendRedirect(request.getContextPath() + "/FrontController?op=perfil");
        } else {
            request.setAttribute("error", "No se pudo actualizar el perfil.");
            request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
        }
    }

    /**
     * Descripción corta del servlet.
     *
     * @return texto informativo sobre este servlet.
     */
    @Override
    public String getServletInfo() {
        return "Controlador de gestión de usuarios - acceso, salir y perfil.";
    }

    /**
     * Envía una respuesta JSON para peticiones Ajax.
     * Usa org.json.JSONObject para construir la respuesta.
     * Formato: {"resultado": "ok"} o {"resultado": "error", "mensaje": "..."}
     *
     * @param response la respuesta HTTP.
     * @param resultado "ok" o "error".
     * @param mensaje el mensaje descriptivo (puede ser null si resultado es "ok").
     */
    private void enviarJsonRespuesta(HttpServletResponse response, String resultado, String mensaje)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        JSONObject json = new JSONObject();
        json.put("resultado", resultado);
        if (mensaje != null) {
            json.put("mensaje", mensaje);
        }
        response.getWriter().write(json.toString());
    }

    /**
     * Procesa el avatar del formulario de edición de perfil.
     * Si el usuario subió un archivo, lo guarda en IMG/avatar/ con nombre único.
     * Si eligió un predefinido distinto al actual, devuelve ese.
     * Si no cambió nada, devuelve null para mantener el original.
     *
     * @param request la petición HTTP.
     * @return el nombre del archivo avatar, o null si no se cambió.
     */
    /** Tamaño máximo permitido para el avatar (100 KB). */
    private static final long MAX_AVATAR_SIZE = 100 * 1024; // 100 KB
    /** Tipos MIME aceptados para el avatar. */
    private static final String[] TIPOS_PERMITIDOS = {"image/jpeg", "image/png"};

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

            String rutaAvatar = getServletContext().getRealPath("/IMG/avatar");
            File carpetaAvatar = new File(rutaAvatar);
            if (!carpetaAvatar.exists()) {
                carpetaAvatar.mkdirs();
            }

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

        return null;
    }

    /**
     * Obtiene la extensión del archivo subido a partir de su cabecera.
     *
     * @param part el Part del formulario multipart.
     * @return la extensión con punto (ej: ".jpg"), por defecto ".jpg".
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
