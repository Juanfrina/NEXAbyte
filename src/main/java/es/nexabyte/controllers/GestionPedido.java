package es.nexabyte.controllers;

import es.nexabyte.beans.LineaPedido;
import es.nexabyte.beans.Usuario;
import es.nexabyte.models.CarritoService;
import es.nexabyte.models.PedidoService;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador para la gestión del carrito y los pedidos.
 * 
 * doGet: verCarrito (lectura).
 * doPost: anadir, aumentar, disminuir, eliminar, vaciar, finalizar (escritura).
 * 
 * Usuarios anónimos: carrito almacenado en cookie (2 días).
 * Usuarios registrados: carrito almacenado en la BD (pedido con estado 'c').
 * 
 * @author jfco1
 */
@WebServlet(name = "GestionPedido", urlPatterns = {"/GestionPedido"})
public class GestionPedido extends HttpServlet {

    /** Servicio para las operaciones del carrito (BD y cookie). */
    private final CarritoService carritoService = new CarritoService();
    /** Servicio para finalizar pedidos. */
    private final PedidoService pedidoService = new PedidoService();

    /**
     * GET: solo operaciones de lectura (ver carrito).
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
            operacion = "verCarrito";
        }

        switch (operacion) {
            case "verCarrito":
                verCarrito(request, response);
                break;
            default:
                // Cualquier otra operación por GET se redirige al carrito
                response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
                break;
        }
    }

    /**
     * POST: operaciones de escritura (modifican datos del carrito/pedido).
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
            response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
            return;
        }

        switch (operacion) {
            case "anadir":
                anadir(request, response);
                break;
            case "aumentar":
                aumentar(request, response);
                break;
            case "disminuir":
                disminuir(request, response);
                break;
            case "eliminar":
                eliminar(request, response);
                break;
            case "vaciar":
                vaciar(request, response);
                break;
            case "finalizar":
                finalizar(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
                break;
        }
    }

    // ==================== OPERACIONES GET (lectura) ====================

    /**
     * Carga y muestra el carrito del usuario (BD o cookie).
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    private void verCarrito(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(true);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        List<LineaPedido> lineasPedido;
        if (usuario != null) {
            lineasPedido = carritoService.obtenerLineasBD(usuario);
        } else {
            lineasPedido = carritoService.obtenerLineasDesdeCookie(request);
        }

        request.setAttribute("lineasPedido", lineasPedido);

        // Sincronizar contador en sesión
        sesion.setAttribute("numArticulos", carritoService.contarArticulos(lineasPedido));

        request.getRequestDispatcher("/JSP/vistas/carrito.jsp").forward(request, response);
    }

    // ==================== OPERACIONES POST (escritura) ====================

    /**
     * Añade un producto al carrito. Soporta peticiones Ajax.
     *
     * @param request la petición HTTP con "idProducto".
     * @param response la respuesta HTTP.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    private void anadir(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        Short idProducto = parsearIdProducto(request.getParameter("idProducto"));

        if (idProducto == null) {
            if (esAjax) { response.setStatus(400); return; }
            response.sendRedirect(request.getContextPath() + "/FrontController?op=inicio");
            return;
        }

        HttpSession sesion = request.getSession(true);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        int totalArticulos;
        if (usuario != null) {
            carritoService.anadirBD(usuario, idProducto);
            totalArticulos = carritoService.contarArticulosBD(usuario);
        } else {
            totalArticulos = carritoService.anadirCookieYContar(request, response, idProducto);
        }

        sesion.setAttribute("numArticulos", totalArticulos);

        if (esAjax) {
            response.setContentType("application/json;charset=UTF-8");
            JsonObject json = new JsonObject();
            json.addProperty("totalArticulos", totalArticulos);
            response.getWriter().write(json.toString());
        } else {
            response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
        }
    }

    /**
     * Aumenta en 1 la cantidad de un producto. Responde por Ajax.
     *
     * @param request la petición HTTP con "idProducto".
     * @param response la respuesta HTTP.
     * @throws IOException si hay error de E/S.
     */
    private void aumentar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        actualizarCantidad(request, response, 1);
    }

    /**
     * Disminuye en 1 la cantidad de un producto. Responde por Ajax.
     *
     * @param request la petición HTTP con "idProducto".
     * @param response la respuesta HTTP.
     * @throws IOException si hay error de E/S.
     */
    private void disminuir(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        actualizarCantidad(request, response, -1);
    }

    /**
     * Lógica común para aumentar/disminuir cantidad. Delega al servicio.
     * Soporta peticiones Ajax: devuelve el total de artículos.
     *
     * @param request la petición HTTP con "idProducto".
     * @param response la respuesta HTTP.
     * @param delta variación de cantidad (+1 o -1).
     * @throws IOException si hay error de E/S.
     */
    private void actualizarCantidad(HttpServletRequest request, HttpServletResponse response, int delta)
            throws IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        Short idProducto = parsearIdProducto(request.getParameter("idProducto"));
        if (idProducto == null) {
            if (esAjax) { response.sendError(400); return; }
            response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
            return;
        }

        HttpSession sesion = request.getSession(true);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        int totalArticulos;
        if (usuario != null) {
            carritoService.actualizarCantidadBD(usuario, idProducto, delta);
            totalArticulos = carritoService.contarArticulosBD(usuario);
        } else {
            totalArticulos = carritoService.actualizarCantidadCookie(request, response, idProducto, delta);
        }

        sesion.setAttribute("numArticulos", totalArticulos);

        if (esAjax) {
            response.setContentType("application/json;charset=UTF-8");
            JsonObject json = new JsonObject();
            json.addProperty("totalArticulos", totalArticulos);
            response.getWriter().write(json.toString());
        } else {
            response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
        }
    }

    /**
     * Elimina un producto del carrito. Soporta peticiones Ajax.
     *
     * @param request la petición HTTP con "idProducto".
     * @param response la respuesta HTTP.
     * @throws IOException si hay error de E/S.
     */
    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        Short idProducto = parsearIdProducto(request.getParameter("idProducto"));
        if (idProducto == null) {
            if (esAjax) { response.sendError(400); return; }
            response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
            return;
        }

        HttpSession sesion = request.getSession(true);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        int totalArticulos;
        if (usuario != null) {
            carritoService.eliminarProductoBD(usuario, idProducto);
            totalArticulos = carritoService.contarArticulosBD(usuario);
        } else {
            totalArticulos = carritoService.eliminarProductoCookie(request, response, idProducto);
        }
        sesion.setAttribute("numArticulos", totalArticulos);

        if (esAjax) {
            response.setContentType("application/json;charset=UTF-8");
            JsonObject json = new JsonObject();
            json.addProperty("totalArticulos", totalArticulos);
            response.getWriter().write(json.toString());
        } else {
            response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
        }
    }

    /**
     * Vacía completamente el carrito. Soporta peticiones Ajax.
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws IOException si hay error de E/S.
     */
    private void vaciar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        HttpSession sesion = request.getSession(true);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        if (usuario != null) {
            carritoService.vaciarBD(usuario);
        } else {
            carritoService.vaciarCookie(request, response);
        }

        sesion.setAttribute("numArticulos", 0);

        if (esAjax) {
            response.setContentType("application/json;charset=UTF-8");
            JsonObject json = new JsonObject();
            json.addProperty("totalArticulos", 0);
            response.getWriter().write(json.toString());
        } else {
            response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
        }
    }

    /**
     * Finaliza el pedido. Solo para usuarios registrados. Delega al servicio.
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    private void finalizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/GestionUsuario?op=formAcceder");
            return;
        }

        boolean ok = pedidoService.finalizar(usuario);
        if (ok) {
            sesion.setAttribute("numArticulos", 0);
            request.setAttribute("mensaje", "Pedido finalizado correctamente. ¡Gracias por tu compra!");
        } else {
            request.setAttribute("error", "No hay ningún pedido en curso.");
        }

        request.getRequestDispatcher("/JSP/avisos/notificacion.jsp").forward(request, response);
    }

    // ==================== UTILIDAD ====================

    /**
     * Parsea un String a Short de forma segura. Devuelve null si no es válido.
     *
     * @param idStr el texto a convertir.
     * @return el Short parseado, o null si el formato es incorrecto.
     */
    private Short parsearIdProducto(String idStr) {
        if (idStr == null) return null;
        try {
            return Short.parseShort(idStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Descripción corta del servlet.
     *
     * @return texto informativo sobre este servlet.
     */
    @Override
    public String getServletInfo() {
        return "Controlador de gestión de pedidos y carrito.";
    }
}
