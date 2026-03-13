package es.nexabyte.controllers;

import es.nexabyte.models.AvatarService;
import es.nexabyte.models.CarritoService;
import es.nexabyte.models.UsuarioService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    /** Servicio para el procesamiento de avatares. */
    private final AvatarService avatarService = new AvatarService();

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
                request.getRequestDispatcher("/JSP/vistas/acceder.jsp").forward(request, response);
                break;
            case "comprobarEmail":
                usuarioService.comprobarEmailAjax(request, response);
                break;
            default:
                request.getRequestDispatcher("/JSP/vistas/acceder.jsp").forward(request, response);
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
                usuarioService.procesarAcceso(request, response, carritoService);
                break;
            case "salir":
                usuarioService.procesarSalida(request, response);
                break;
            case "editarPerfil":
                usuarioService.procesarEditarPerfil(request, response, avatarService, getServletContext());
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/GestionUsuario?op=formAcceder");
                break;
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
}
