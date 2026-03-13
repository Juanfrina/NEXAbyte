package es.nexabyte.controllers;

import es.nexabyte.models.CarritoService;
import es.nexabyte.models.PedidoService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
                String destino = carritoService.prepararVistaCarrito(request);
                request.getRequestDispatcher(destino).forward(request, response);
                break;
            default:
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
                carritoService.procesarAnadir(request, response);
                break;
            case "aumentar":
                carritoService.procesarActualizarCantidad(request, response, 1);
                break;
            case "disminuir":
                carritoService.procesarActualizarCantidad(request, response, -1);
                break;
            case "eliminar":
                carritoService.procesarEliminar(request, response);
                break;
            case "vaciar":
                carritoService.procesarVaciar(request, response);
                break;
            case "finalizar":
                pedidoService.procesarFinalizar(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/GestionPedido?op=verCarrito");
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
        return "Controlador de gestión de pedidos y carrito.";
    }
}
