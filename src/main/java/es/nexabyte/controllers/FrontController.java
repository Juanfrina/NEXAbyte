package es.nexabyte.controllers;

import es.nexabyte.models.CarritoService;
import es.nexabyte.models.PedidoService;
import es.nexabyte.models.ProductoService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controlador frontal que gestiona toda la navegación de la aplicación.
 * Recibe el parámetro "op" para determinar la operación a realizar
 * y delega la lógica a los servicios correspondientes.
 * 
 * @author jfco1
 */
@WebServlet(name = "FrontController", urlPatterns = {"/FrontController"})
public class FrontController extends HttpServlet {

    /** Servicio para operaciones de productos. */
    private final ProductoService productoService = new ProductoService();
    /** Servicio para operaciones del carrito. */
    private final CarritoService carritoService = new CarritoService();
    /** Servicio para operaciones de pedidos. */
    private final PedidoService pedidoService = new PedidoService();

    /**
     * Todas las operaciones de este controlador son de lectura/navegación,
     * por lo que se procesan exclusivamente por GET.
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
            operacion = "inicio";
        }

        String destino;

        switch (operacion) {
            case "inicio":
                destino = productoService.cargarInicio(request);
                break;
            case "buscar":
                destino = productoService.buscarProductos(request);
                break;
            case "verProducto":
                destino = productoService.verProducto(request);
                break;
            case "registro":
                destino = "/JSP/vistas/registro.jsp";
                break;
            case "carrito":
                destino = carritoService.prepararVistaCarrito(request);
                break;
            case "perfil":
                destino = "/JSP/vistas/perfil.jsp";
                break;
            case "editarPerfil":
                destino = "/JSP/vistas/editarPerfil.jsp";
                break;
            case "verPedidos":
                destino = pedidoService.prepararVistaVerPedidos(request);
                break;
            case "verPedido":
                destino = pedidoService.prepararVistaDetallePedido(request);
                break;
            default:
                destino = productoService.cargarInicio(request);
                break;
        }

        request.getRequestDispatcher(destino).forward(request, response);
    }

    /**
     * POST procesa las mismas operaciones que GET.
     * Se usa para formularios de búsqueda/filtro para que los parámetros
     * no se muestren en la URL del navegador.
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
     * Descripción corta del servlet.
     *
     * @return texto informativo sobre este servlet.
     */
    @Override
    public String getServletInfo() {
        return "Controlador frontal de NEXAbyte - Gestiona la navegación de la aplicación.";
    }
}
