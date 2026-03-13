package es.nexabyte.controllers;

import es.nexabyte.models.AvatarService;
import es.nexabyte.models.UsuarioService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    /** Servicio para procesamiento de avatares. */
    private final AvatarService avatarService = new AvatarService();

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
     * Delega toda la lógica de validación, avatar y registro al servicio.
     *
     * @param request  la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException      si falla la E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        usuarioService.procesarRegistro(request, response, avatarService, getServletContext());
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
}
