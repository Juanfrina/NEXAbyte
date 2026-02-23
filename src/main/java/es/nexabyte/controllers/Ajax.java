package es.nexabyte.controllers;

import es.nexabyte.models.Utils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * Servlet para peticiones Ajax genéricas.
 * Gestiona operaciones ligeras que se resuelven sin navegación,
 * como el cálculo de la letra del NIF.
 *
 * @author jfco1
 */
@WebServlet(name = "Ajax", urlPatterns = {"/Ajax"})
public class Ajax extends HttpServlet {

    /**
     * GET: operaciones de lectura Ajax.
     * Parámetro "op":
     *   - letraNif: calcula la letra del NIF a partir de los 8 dígitos.
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
            response.sendError(400, "Operación no especificada.");
            return;
        }

        switch (operacion) {
            case "letraNif":
                letraNif(request, response);
                break;
            default:
                response.sendError(400, "Operación no válida.");
                break;
        }
    }

    /**
     * POST: delega en doGet porque todas las operaciones son de lectura.
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si hay error en el servlet.
     * @throws IOException si hay error de E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Calcula la letra del NIF a partir de los 8 dígitos recibidos por Ajax.
     * Responde en JSON: {"nif": "12345678", "letra": "Z"}
     * Usa org.json.JSONObject para construir la respuesta.
     *
     * @param request la petición con el parámetro "nif".
     * @param response la respuesta donde se escribe el JSON.
     * @throws IOException si falla la escritura.
     */
    private void letraNif(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        String numeros = request.getParameter("nif");
        String letra = Utils.calcularLetraNIF(numeros);

        JSONObject json = new JSONObject();
        json.put("nif", numeros != null ? numeros : "");
        json.put("letra", letra);
        response.getWriter().write(json.toString());
    }

    /**
     * Descripción corta del servlet.
     *
     * @return texto informativo sobre este servlet.
     */
    @Override
    public String getServletInfo() {
        return "Servlet para peticiones Ajax genéricas (NIF, etc.).";
    }
}
