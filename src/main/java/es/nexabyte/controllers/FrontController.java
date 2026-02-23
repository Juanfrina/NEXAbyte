package es.nexabyte.controllers;

import es.nexabyte.DAO.IProductoDAO;
import es.nexabyte.DAOFactory.DAOFactory;
import es.nexabyte.beans.LineaPedido;
import es.nexabyte.beans.Pedido;
import es.nexabyte.beans.Producto;
import es.nexabyte.beans.Usuario;
import es.nexabyte.models.CarritoService;
import es.nexabyte.models.PedidoService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controlador frontal que gestiona toda la navegación de la aplicación.
 * Recibe el parámetro "op" para determinar la operación a realizar
 * y delega la lógica a los controladores específicos o carga datos
 * y redirige a las vistas correspondientes.
 * 
 * @author jfco1
 */
@WebServlet(name = "FrontController", urlPatterns = {"/FrontController"})
public class FrontController extends HttpServlet {

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
                destino = cargarInicio(request);
                break;
            case "buscar":
                destino = buscarProductos(request);
                break;
            case "verProducto":
                destino = verProducto(request);
                break;
            case "registro":
                destino = "/JSP/vistas/registro.jsp";
                break;
            case "carrito":
                destino = verCarrito(request);
                break;
            case "perfil":
                destino = "/JSP/vistas/perfil.jsp";
                break;
            case "editarPerfil":
                destino = "/JSP/vistas/editarPerfil.jsp";
                break;
            case "verPedidos":
                destino = verPedidos(request);
                break;
            case "verPedido":
                destino = verDetallePedido(request);
                break;
            default:
                destino = cargarInicio(request);
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
     * Carga la página de inicio con todos los productos y las marcas disponibles.
     * 
     * @param request la petición HTTP.
     * @return la ruta de la vista de inicio.
     */
    private String cargarInicio(HttpServletRequest request) {
        IProductoDAO productoDAO = DAOFactory.getProductoDAO();
        List<Producto> productos = productoDAO.obtenerAleatorios(6);
        List<String> marcas = productoDAO.obtenerMarcas();

        request.setAttribute("productos", productos);
        request.setAttribute("marcas", marcas);
        request.setAttribute("precioMaximo", productoDAO.obtenerPrecioMaximo());
        return "/index.jsp";
    }

    /**
     * Busca productos aplicando los filtros recibidos del formulario.
     * Mantiene los valores de los filtros para mostrarlos en la vista.
     * 
     * @param request la petición HTTP con los parámetros de búsqueda.
     * @return la ruta de la vista de inicio con los resultados.
     */
    private String buscarProductos(HttpServletRequest request) {
        String nombre = request.getParameter("nombre");
        String categoriaStr = request.getParameter("idCategoria");
        String marca = request.getParameter("marca");
        String precioMinStr = request.getParameter("precioMin");
        String precioMaxStr = request.getParameter("precioMax");

        Byte idCategoria = null;
        Double precioMin = null;
        Double precioMax = null;

        try {
            if (categoriaStr != null && !categoriaStr.isEmpty()) {
                idCategoria = Byte.parseByte(categoriaStr);
            }
            if (precioMinStr != null && !precioMinStr.isEmpty()) {
                double val = Double.parseDouble(precioMinStr);
                if (val > 0) {
                    precioMin = val;
                }
            }
            if (precioMaxStr != null && !precioMaxStr.isEmpty()) {
                double val = Double.parseDouble(precioMaxStr);
                // Solo filtra si el máximo no coincide con el tope del slider
                Double tope = DAOFactory.getProductoDAO().obtenerPrecioMaximo();
                if (val < tope) {
                    precioMax = val;
                }
            }
        } catch (NumberFormatException e) {
            // Ignorar filtros con formato incorrecto
        }

        IProductoDAO productoDAO = DAOFactory.getProductoDAO();
        List<Producto> productos = productoDAO.buscar(nombre, idCategoria, marca, precioMin, precioMax);
        List<String> marcas = productoDAO.obtenerMarcas();

        request.setAttribute("productos", productos);
        request.setAttribute("marcas", marcas);
        request.setAttribute("precioMaximo", productoDAO.obtenerPrecioMaximo());

        // Mantener los filtros activos en la vista
        request.setAttribute("filtroNombre", nombre);
        request.setAttribute("filtroCategoria", categoriaStr);
        request.setAttribute("filtroMarca", marca);
        request.setAttribute("filtroPrecioMin", precioMinStr);
        request.setAttribute("filtroPrecioMax", precioMaxStr);

        return "/index.jsp";
    }

    /**
     * Carga el detalle de un producto específico.
     * 
     * @param request la petición HTTP con el id del producto.
     * @return la ruta de la vista del producto.
     */
    private String verProducto(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                Short idProducto = Short.parseShort(idStr);
                IProductoDAO productoDAO = DAOFactory.getProductoDAO();
                Producto producto = productoDAO.obtenerPorId(idProducto);
                request.setAttribute("producto", producto);
            } catch (NumberFormatException e) {
                // ID no válido
            }
        }
        return "/JSP/vistas/producto.jsp";
    }

    /**
     * Carga el carrito para usuario registrado (BD) o anónimo (cookie).
     * Sincroniza el contador de artículos en la sesión.
     *
     * @param request la petición HTTP.
     * @return la ruta de la vista del carrito.
     */
    private String verCarrito(HttpServletRequest request) {
        HttpSession sesion = request.getSession(true);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        List<LineaPedido> lineasPedido;
        if (usuario != null) {
            lineasPedido = carritoService.obtenerLineasBD(usuario);
        } else {
            lineasPedido = carritoService.obtenerLineasDesdeCookie(request);
        }

        request.setAttribute("lineasPedido", lineasPedido);

        // Sincronizar contador en sesión (badge cabecera)
        sesion.setAttribute("numArticulos", carritoService.contarArticulos(lineasPedido));

        return "/JSP/vistas/carrito.jsp";
    }

    /**
     * Carga la lista de pedidos finalizados del usuario.
     *
     * @param request la petición HTTP.
     * @return la ruta de la vista de pedidos.
     */
    private String verPedidos(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

        if (usuario != null) {
            List<Pedido> pedidos = pedidoService.obtenerFinalizados(usuario);
            request.setAttribute("pedidos", pedidos);
        }
        return "/JSP/vistas/verPedidos.jsp";
    }

    /**
     * Carga el detalle de un pedido finalizado con sus líneas.
     *
     * @param request la petición HTTP con el parámetro "id".
     * @return la ruta de la vista de detalle del pedido.
     */
    private String verDetallePedido(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

        String idStr = request.getParameter("id");
        if (idStr != null && usuario != null) {
            try {
                Short idPedido = Short.parseShort(idStr);
                Pedido pedido = pedidoService.obtenerDetallePedido(usuario, idPedido);
                if (pedido != null) {
                    request.setAttribute("pedido", pedido);
                }
            } catch (NumberFormatException e) {
                // ID no válido
            }
        }
        return "/JSP/vistas/pedido.jsp";
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
