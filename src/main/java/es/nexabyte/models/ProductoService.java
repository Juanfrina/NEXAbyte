package es.nexabyte.models;

import es.nexabyte.DAO.IProductoDAO;
import es.nexabyte.DAOFactory.DAOFactory;
import es.nexabyte.beans.Producto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Servicio de lógica de negocio para la gestión de productos.
 * Centraliza la carga de productos, búsqueda con filtros
 * y consulta de detalle de producto.
 *
 * @author jfco1
 */
public class ProductoService {

    /**
     * Carga la página de inicio con productos aleatorios y las marcas disponibles.
     *
     * @param request la petición HTTP.
     * @return la ruta de la vista de inicio.
     */
    public String cargarInicio(HttpServletRequest request) {
        IProductoDAO productoDAO = DAOFactory.getProductoDAO();
        List<Producto> productos = productoDAO.obtenerAleatorios(8);
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
    public String buscarProductos(HttpServletRequest request) {
        String nombre = request.getParameter("nombre");
        String[] categoriasArr = request.getParameterValues("idCategoria");
        String[] marcasArr = request.getParameterValues("marca");
        String precioMinStr = request.getParameter("precioMin");
        String precioMaxStr = request.getParameter("precioMax");

        List<Byte> idsCategorias = new ArrayList<>();
        List<String> marcas = new ArrayList<>();
        Double precioMin = null;
        Double precioMax = null;

        try {
            if (categoriasArr != null) {
                for (String cs : categoriasArr) {
                    if (cs != null && !cs.isEmpty()) {
                        idsCategorias.add(Byte.parseByte(cs));
                    }
                }
            }
            if (marcasArr != null) {
                for (String m : marcasArr) {
                    if (m != null && !m.isEmpty()) {
                        marcas.add(m);
                    }
                }
            }
            if (precioMinStr != null && !precioMinStr.isEmpty()) {
                double val = Double.parseDouble(precioMinStr);
                if (val > 0) {
                    precioMin = val;
                }
            }
            if (precioMaxStr != null && !precioMaxStr.isEmpty()) {
                double val = Double.parseDouble(precioMaxStr);
                Double tope = DAOFactory.getProductoDAO().obtenerPrecioMaximo();
                if (val < tope) {
                    precioMax = val;
                }
            }
        } catch (NumberFormatException e) {
            // Ignorar filtros con formato incorrecto
        }

        IProductoDAO productoDAO = DAOFactory.getProductoDAO();
        List<Producto> productos = productoDAO.buscar(nombre,
                idsCategorias.isEmpty() ? null : idsCategorias,
                marcas.isEmpty() ? null : marcas,
                precioMin, precioMax);
        List<String> todasMarcas = productoDAO.obtenerMarcas();

        request.setAttribute("productos", productos);
        request.setAttribute("marcas", todasMarcas);
        request.setAttribute("precioMaximo", productoDAO.obtenerPrecioMaximo());

        // Mantener los filtros activos en la vista
        request.setAttribute("filtroNombre", nombre);
        request.setAttribute("filtroCategorias",
                idsCategorias.isEmpty() ? null : idsCategorias);
        request.setAttribute("filtroMarcas",
                marcas.isEmpty() ? null : marcas);
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
    public String verProducto(HttpServletRequest request) {
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
}
