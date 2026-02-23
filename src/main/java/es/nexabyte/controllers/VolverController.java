package es.nexabyte.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controlador simple que redirige al inicio de la aplicación.
 * Se usa como destino genérico de "volver" desde cualquier vista.
 * 
 * @author jfco1
 */
@WebServlet(name = "VolverController", urlPatterns = {"/VolverController"})
public class VolverController extends HttpServlet {

    /**
     * Redirige al FrontController con la operación de inicio.
     *
     * @param request  la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException      si falla la E/S.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/FrontController?op=inicio");
    }

    /**
     * POST: delega en doGet para redirigir al inicio.
     *
     * @param request  la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException      si falla la E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Devuelve info breve del servlet.
     *
     * @return descripción corta.
     */
    @Override
    public String getServletInfo() {
        return "Controlador de redirección al inicio.";
    }
}
