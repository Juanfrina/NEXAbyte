package es.nexabyte.models;

import es.nexabyte.DAO.IUsuarioDAO;
import es.nexabyte.DAOFactory.DAOFactory;
import es.nexabyte.beans.Usuario;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONObject;

/**
 * Servicio de lógica de negocio para la gestión de usuarios.
 * Centraliza la autenticación, registro y edición de perfil.
 *
 * @author jfco1
 */
public class UsuarioService {

    /**
     * Valida las credenciales del usuario.
     *
     * @param email el correo electrónico.
     * @param password la contraseña en texto plano.
     * @return el Usuario si las credenciales son válidas, null si no.
     */
    public Usuario autenticar(String email, String password) {
        String passwordMD5 = SecurityUtils.encriptarMD5(password);
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        return usuarioDAO.validarCredenciales(email, passwordMD5);
    }

    /**
     * Registra el último acceso del usuario.
     *
     * @param idUsuario el id del usuario.
     */
    public void registrarUltimoAcceso(Short idUsuario) {
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        usuarioDAO.actualizarUltimoAcceso(idUsuario);
    }

    /**
     * Comprueba si un email ya existe en la BD.
     *
     * @param email el correo a comprobar.
     * @return true si ya existe.
     */
    public boolean existeEmail(String email) {
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        return usuarioDAO.existeEmail(email.trim());
    }

    /**
     * Actualiza los datos del perfil de un usuario.
     *
     * @param usuario el usuario con los datos actualizados.
     * @param nuevaPassword la nueva contraseña (null o vacía si no se cambia).
     * @return true si se actualizó correctamente.
     */
    public boolean actualizarPerfil(Usuario usuario, String nuevaPassword) {
        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            usuario.setPassword(SecurityUtils.encriptarMD5(nuevaPassword));
        }
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        return usuarioDAO.actualizar(usuario);
    }

    /**
     * Inserta un nuevo usuario en la BD.
     *
     * @param usuario el usuario a insertar.
     * @return true si se insertó correctamente.
     */
    public boolean registrar(Usuario usuario) {
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        return usuarioDAO.insertar(usuario);
    }

    // ==================== OPERACIONES HTTP (movidas desde controladores) ====================

    /**
     * Responde en JSON si el email ya existe (Ajax).
     * Devuelve {"estado": "existe"}, {"estado": "disponible"} o {"estado": "vacio"}.
     *
     * @param request la petición HTTP con el parámetro "email".
     * @param response la respuesta HTTP.
     * @throws IOException si falla la escritura.
     */
    public void comprobarEmailAjax(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String email = request.getParameter("email");

        JSONObject json = new JSONObject();

        if (email == null || email.trim().isEmpty()) {
            json.put("estado", "vacio");
        } else {
            boolean existe = existeEmail(email.trim());
            json.put("estado", existe ? "existe" : "disponible");
        }

        response.getWriter().write(json.toString());
    }

    /**
     * Valida las credenciales del usuario, abre sesión y transfiere
     * el carrito de cookie a la base de datos si es su primer acceso.
     *
     * @param request la petición HTTP con email y password.
     * @param response la respuesta HTTP.
     * @param carritoService servicio del carrito para la transferencia.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    public void procesarAcceso(HttpServletRequest request, HttpServletResponse response,
            CarritoService carritoService) throws ServletException, IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email != null) {
            email = email.trim();
        }

        if (Utils.esVacio(email) || Utils.esVacio(password)) {
            if (esAjax) {
                Utils.enviarJsonRespuesta(response, "error", "Debes rellenar todos los campos.");
                return;
            }
            request.setAttribute("error", "Debes rellenar todos los campos.");
            request.getRequestDispatcher("/JSP/vistas/acceder.jsp").forward(request, response);
            return;
        }

        Usuario usuario = autenticar(email, password);

        if (usuario == null) {
            if (esAjax) {
                Utils.enviarJsonRespuesta(response, "error", "Email o contraseña incorrectos.");
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
        registrarUltimoAcceso(usuario.getIdUsuario());

        // Carrito: primer login (recién registrado) vs usuario recurrente
        if (usuario.getUltimoAcceso() == null) {
            carritoService.transferirCookieABD(request, response, usuario);
        } else {
            carritoService.vaciarCookie(request, response);
        }

        // Sincronizar el badge con el carrito real del usuario en BD
        sesion.setAttribute("numArticulos", carritoService.contarArticulosBD(usuario));

        if (esAjax) {
            Utils.enviarJsonRespuesta(response, "ok", null);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

    /**
     * Cierra la sesión del usuario y redirige al inicio.
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws IOException si falla la redirección.
     */
    public void procesarSalida(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

    /**
     * Actualiza los datos del perfil del usuario en sesión.
     * Usa BeanUtils.populate() para cargar los campos del formulario.
     * Para cambiar contraseña: se pide la actual + nueva (x2).
     * Email y NIF no se pueden modificar.
     *
     * @param request la petición HTTP con los datos del formulario.
     * @param response la respuesta HTTP.
     * @param avatarService servicio para procesar el avatar subido.
     * @param servletContext el contexto del servlet para la ruta de archivos.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    public void procesarEditarPerfil(HttpServletRequest request, HttpServletResponse response,
            AvatarService avatarService, ServletContext servletContext)
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
        String nuevoAvatar = avatarService.procesarAvatar(request, servletContext);
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
            if (Utils.esVacio(passwordActual)) {
                request.setAttribute("error", "Debes introducir tu contraseña actual para cambiarla.");
                request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
                return;
            }
            String actualMD5 = SecurityUtils.encriptarMD5(passwordActual);
            if (!actualMD5.equals(passwordOriginal)) {
                request.setAttribute("error", "La contraseña actual no es correcta.");
                request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
                return;
            }
            if (!passwordNueva.equals(passwordNueva2)) {
                request.setAttribute("error", "Las nuevas contraseñas no coinciden.");
                request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
                return;
            }
            if (passwordNueva.length() < 6) {
                request.setAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
                request.getRequestDispatcher("/JSP/vistas/editarPerfil.jsp").forward(request, response);
                return;
            }
            usuarioSesion.setPassword(SecurityUtils.encriptarMD5(passwordNueva));
        }

        boolean exito = actualizarPerfil(usuarioSesion, null);

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
     * Procesa el formulario de registro de un nuevo usuario.
     * Valida campos obligatorios, comprueba email duplicado,
     * encripta la contraseña y sube el avatar.
     *
     * @param request la petición HTTP con los datos del formulario.
     * @param response la respuesta HTTP.
     * @param avatarService servicio para procesar el avatar.
     * @param servletContext el contexto del servlet para la ruta de archivos.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    public void procesarRegistro(HttpServletRequest request, HttpServletResponse response,
            AvatarService avatarService, ServletContext servletContext)
            throws ServletException, IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

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

        // 1. Campos obligatorios
        if (Utils.esVacio(email) || Utils.esVacio(password) || Utils.esVacio(password2)
                || Utils.esVacio(nombre) || Utils.esVacio(apellidos) || Utils.esVacio(nif)
                || Utils.esVacio(direccion) || Utils.esVacio(codigoPostal)
                || Utils.esVacio(localidad) || Utils.esVacio(provincia)) {
            String msg = "Los campos marcados con * son obligatorios.";
            if (esAjax) { Utils.enviarJsonRespuesta(response, "error", msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 2. Contraseñas coinciden
        if (!password.equals(password2)) {
            String msg = "Las contraseñas no coinciden.";
            if (esAjax) { Utils.enviarJsonRespuesta(response, "error", msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 3. Longitud mínima contraseña
        if (password.length() < 6) {
            String msg = "La contraseña debe tener al menos 6 caracteres.";
            if (esAjax) { Utils.enviarJsonRespuesta(response, "error", msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 4. NIF: 8 dígitos
        if (!nif.matches("[0-9]{8}")) {
            String msg = "El NIF debe contener exactamente 8 dígitos.";
            if (esAjax) { Utils.enviarJsonRespuesta(response, "error", msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 5. Código postal válido
        if (!Utils.validarCodigoPostal(codigoPostal)) {
            String msg = "El código postal no es válido (5 dígitos, 01-52).";
            if (esAjax) { Utils.enviarJsonRespuesta(response, "error", msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // 6. Email duplicado
        if (existeEmail(email.trim())) {
            String msg = "Ya existe una cuenta con ese correo electrónico.";
            if (esAjax) { Utils.enviarJsonRespuesta(response, "error", msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // Procesar avatar
        String avatarNombre = avatarService.procesarAvatar(request, servletContext);
        if (avatarNombre == null && request.getAttribute("error") != null) {
            if (esAjax) {
                Utils.enviarJsonRespuesta(response, "error", (String) request.getAttribute("error"));
                return;
            }
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }
        if (avatarNombre == null) {
            avatarNombre = "default.png";
        }

        // Cargar bean Usuario con BeanUtils
        Usuario usuario = new Usuario();
        try {
            BeanUtils.populate(usuario, request.getParameterMap());
        } catch (IllegalAccessException | InvocationTargetException e) {
            String msg = "Error al procesar el formulario.";
            if (esAjax) { Utils.enviarJsonRespuesta(response, "error", msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
            return;
        }

        // Sobrescribir campos que requieren tratamiento especial
        usuario.setEmail(email.trim());
        usuario.setPassword(SecurityUtils.encriptarMD5(password));
        usuario.setNif(nif + Utils.calcularLetraNIF(nif));
        usuario.setAvatar(avatarNombre);

        // Insertar en BD
        boolean insertado = registrar(usuario);

        if (insertado) {
            if (esAjax) { Utils.enviarJsonRespuesta(response, "ok", null); return; }
            request.setAttribute("mensaje", "Registro completado. Ya puedes acceder con tu cuenta.");
            request.getRequestDispatcher("/JSP/avisos/notificacion.jsp").forward(request, response);
        } else {
            String msg = "No se pudo completar el registro. Inténtalo de nuevo.";
            if (esAjax) { Utils.enviarJsonRespuesta(response, "error", msg); return; }
            request.setAttribute("error", msg);
            request.getRequestDispatcher("/JSP/vistas/registro.jsp").forward(request, response);
        }
    }
}
