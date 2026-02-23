package es.nexabyte.models;

import es.nexabyte.DAO.ILineaPedidoDAO;
import es.nexabyte.DAO.IPedidoDAO;
import es.nexabyte.DAO.IProductoDAO;
import es.nexabyte.DAOFactory.DAOFactory;
import es.nexabyte.beans.LineaPedido;
import es.nexabyte.beans.Pedido;
import es.nexabyte.beans.Producto;
import es.nexabyte.beans.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servicio de lógica de negocio para la gestión del carrito.
 * Centraliza todas las operaciones sobre el carrito tanto para
 * usuarios registrados (BD) como anónimos (cookie).
 *
 * @author jfco1
 */
public class CarritoService {

    // ==================== OPERACIONES BD (usuario registrado) ====================

    /**
     * Añade un producto al carrito en BD. Si ya existe, incrementa la cantidad.
     *
     * @param usuario el usuario registrado.
     * @param idProducto el id del producto a añadir.
     */
    public void anadirBD(Usuario usuario, Short idProducto) {
        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        ILineaPedidoDAO lineaDAO = DAOFactory.getLineaPedidoDAO();

        Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
        if (carrito == null) {
            carrito = new Pedido();
            carrito.setUsuario(usuario);
            carrito.setEstado(Pedido.Estado.c);
            carrito.setImporte(0.0);
            carrito.setIva(0.0);
            Short idPedido = pedidoDAO.insertar(carrito);
            carrito.setIdPedido(idPedido);
        }

        LineaPedido existente = lineaDAO.buscarPorPedidoYProducto(carrito.getIdPedido(), idProducto);
        if (existente != null) {
            lineaDAO.actualizarCantidad(existente.getIdLinea(), existente.getCantidad() + 1);
        } else {
            LineaPedido linea = new LineaPedido();
            linea.setPedido(carrito);
            Producto prod = new Producto();
            prod.setIdProducto(idProducto);
            linea.setProducto(prod);
            linea.setCantidad(1);
            lineaDAO.insertar(linea);
        }

        pedidoDAO.actualizarImportes(carrito.getIdPedido());
    }

    /**
     * Cuenta el total de artículos en el carrito BD del usuario.
     *
     * @param usuario el usuario registrado.
     * @return el número total de artículos.
     */
    public int contarArticulosBD(Usuario usuario) {
        int total = 0;
        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        ILineaPedidoDAO lineaDAO = DAOFactory.getLineaPedidoDAO();
        Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
        if (carrito != null) {
            List<LineaPedido> lineas = lineaDAO.obtenerPorPedido(carrito.getIdPedido());
            for (LineaPedido l : lineas) {
                total += l.getCantidad();
            }
        }
        return total;
    }

    /**
     * Obtiene las líneas del carrito desde BD.
     *
     * @param usuario el usuario registrado.
     * @return lista de líneas de pedido del carrito.
     */
    public List<LineaPedido> obtenerLineasBD(Usuario usuario) {
        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        ILineaPedidoDAO lineaDAO = DAOFactory.getLineaPedidoDAO();
        Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
        if (carrito != null) {
            return lineaDAO.obtenerPorPedido(carrito.getIdPedido());
        }
        return new ArrayList<>();
    }

    /**
     * Actualiza la cantidad de un producto en el carrito BD (delta: +1 o -1).
     * Si la cantidad resultante es 0 o menos, elimina la línea.
     *
     * @param usuario el usuario registrado.
     * @param idProducto el id del producto.
     * @param delta la variación de cantidad (+1 o -1).
     */
    public void actualizarCantidadBD(Usuario usuario, Short idProducto, int delta) {
        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        ILineaPedidoDAO lineaDAO = DAOFactory.getLineaPedidoDAO();

        Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
        if (carrito != null) {
            LineaPedido linea = lineaDAO.buscarPorPedidoYProducto(carrito.getIdPedido(), idProducto);
            if (linea != null) {
                int nuevaCantidad = linea.getCantidad() + delta;
                if (nuevaCantidad <= 0) {
                    lineaDAO.eliminar(linea.getIdLinea());
                } else {
                    lineaDAO.actualizarCantidad(linea.getIdLinea(), nuevaCantidad);
                }
                pedidoDAO.actualizarImportes(carrito.getIdPedido());
            }
        }
    }

    /**
     * Elimina un producto del carrito BD.
     *
     * @param usuario el usuario registrado.
     * @param idProducto el id del producto a eliminar.
     */
    public void eliminarProductoBD(Usuario usuario, Short idProducto) {
        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        ILineaPedidoDAO lineaDAO = DAOFactory.getLineaPedidoDAO();

        Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
        if (carrito != null) {
            LineaPedido linea = lineaDAO.buscarPorPedidoYProducto(carrito.getIdPedido(), idProducto);
            if (linea != null) {
                lineaDAO.eliminar(linea.getIdLinea());
                pedidoDAO.actualizarImportes(carrito.getIdPedido());
            }
        }
    }

    /**
     * Vacía completamente el carrito BD del usuario.
     *
     * @param usuario el usuario registrado.
     */
    public void vaciarBD(Usuario usuario) {
        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        ILineaPedidoDAO lineaDAO = DAOFactory.getLineaPedidoDAO();

        Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
        if (carrito != null) {
            lineaDAO.eliminarPorPedido(carrito.getIdPedido());
            pedidoDAO.actualizarImportes(carrito.getIdPedido());
        }
    }

    // ==================== OPERACIONES COOKIE (usuario anónimo) ====================

    /**
     * Añade un producto al carrito en cookie y devuelve el total de artículos.
     *
     * @param request la petición HTTP (para leer la cookie actual).
     * @param response la respuesta HTTP (para guardar la cookie actualizada).
     * @param idProducto el id del producto a añadir.
     * @return el número total de artículos tras añadir.
     */
    public int anadirCookieYContar(HttpServletRequest request, HttpServletResponse response, Short idProducto) {
        List<int[]> items = parsearCookie(obtenerCookieCarrito(request));

        boolean encontrado = false;
        for (int[] item : items) {
            if (item[0] == idProducto) {
                item[1]++;
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            items.add(new int[]{idProducto, 1});
        }

        guardarCookieCarrito(response, request, items);

        int total = 0;
        for (int[] item : items) {
            total += item[1];
        }
        return total;
    }

    /**
     * Actualiza la cantidad de un producto en la cookie (delta: +1 o -1).
     * Devuelve el total de artículos tras la actualización.
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @param idProducto el id del producto.
     * @param delta la variación de cantidad.
     * @return el número total de artículos en el carrito.
     */
    public int actualizarCantidadCookie(HttpServletRequest request, HttpServletResponse response,
                                         Short idProducto, int delta) {
        List<int[]> items = parsearCookie(obtenerCookieCarrito(request));

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i)[0] == idProducto) {
                items.get(i)[1] += delta;
                if (items.get(i)[1] <= 0) {
                    items.remove(i);
                }
                break;
            }
        }
        guardarCookieCarrito(response, request, items);

        int total = 0;
        for (int[] item : items) {
            total += item[1];
        }
        return total;
    }

    /**
     * Elimina un producto de la cookie del carrito.
     * Devuelve el total de artículos tras la eliminación.
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @param idProducto el id del producto a eliminar.
     * @return el número total de artículos en el carrito.
     */
    public int eliminarProductoCookie(HttpServletRequest request, HttpServletResponse response, Short idProducto) {
        List<int[]> items = parsearCookie(obtenerCookieCarrito(request));
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i)[0] == idProducto) {
                items.remove(i);
                break;
            }
        }
        guardarCookieCarrito(response, request, items);

        int total = 0;
        for (int[] item : items) {
            total += item[1];
        }
        return total;
    }

    /**
     * Vacía la cookie del carrito (la elimina).
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     */
    public void vaciarCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookieCarrito = new Cookie(Utils.COOKIE_CARRITO, "");
        cookieCarrito.setMaxAge(0);
        cookieCarrito.setPath(request.getContextPath());
        response.addCookie(cookieCarrito);
    }

    /**
     * Obtiene las líneas del carrito desde la cookie, cargando productos de BD.
     *
     * @param request la petición HTTP.
     * @return lista de líneas de pedido construidas desde la cookie.
     */
    public List<LineaPedido> obtenerLineasDesdeCookie(HttpServletRequest request) {
        List<LineaPedido> lineas = new ArrayList<>();
        List<int[]> items = parsearCookie(obtenerCookieCarrito(request));
        if (items.isEmpty()) return lineas;

        IProductoDAO productoDAO = DAOFactory.getProductoDAO();
        for (int[] item : items) {
            Producto producto = productoDAO.obtenerPorId((short) item[0]);
            if (producto != null) {
                LineaPedido linea = new LineaPedido();
                linea.setProducto(producto);
                linea.setCantidad(item[1]);
                lineas.add(linea);
            }
        }
        return lineas;
    }

    /**
     * Transfiere el carrito de cookie a BD al hacer login.
     * Suma las cantidades si el producto ya existe en el carrito BD.
     * Después elimina la cookie.
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @param usuario el usuario que acaba de hacer login.
     */
    public void transferirCookieABD(HttpServletRequest request, HttpServletResponse response, Usuario usuario) {
        String carritoStr = obtenerCookieCarrito(request);
        if (carritoStr.isEmpty()) return;

        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        ILineaPedidoDAO lineaDAO = DAOFactory.getLineaPedidoDAO();

        Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
        if (carrito == null) {
            carrito = new Pedido();
            carrito.setUsuario(usuario);
            carrito.setEstado(Pedido.Estado.c);
            carrito.setImporte(0.0);
            carrito.setIva(0.0);
            Short idPedido = pedidoDAO.insertar(carrito);
            carrito.setIdPedido(idPedido);
        }

        List<int[]> items = parsearCookie(carritoStr);
        for (int[] item : items) {
            Short idProducto = (short) item[0];
            int cantidad = item[1];

            LineaPedido existente = lineaDAO.buscarPorPedidoYProducto(carrito.getIdPedido(), idProducto);
            if (existente != null) {
                lineaDAO.actualizarCantidad(existente.getIdLinea(), existente.getCantidad() + cantidad);
            } else {
                LineaPedido nuevaLinea = new LineaPedido();
                nuevaLinea.setPedido(carrito);
                Producto prod = new Producto();
                prod.setIdProducto(idProducto);
                nuevaLinea.setProducto(prod);
                nuevaLinea.setCantidad(cantidad);
                lineaDAO.insertar(nuevaLinea);
            }
        }

        pedidoDAO.actualizarImportes(carrito.getIdPedido());
        vaciarCookie(request, response);
    }

    // ==================== UTILIDADES INTERNAS ====================

    /**
     * Busca la cookie del carrito en la petición y devuelve su contenido.
     *
     * @param request la petición HTTP donde buscar la cookie.
     * @return el valor de la cookie, o cadena vacía si no existe.
     */
    private String obtenerCookieCarrito(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (Utils.COOKIE_CARRITO.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return "";
    }

    /**
     * Convierte el texto de la cookie en una lista de pares [idProducto, cantidad].
     * Formato esperado: "idProducto:cantidad~idProducto:cantidad~..."
     *
     * @param carritoStr el contenido crudo de la cookie.
     * @return lista con arrays de 2 posiciones: {idProducto, cantidad}.
     */
    private List<int[]> parsearCookie(String carritoStr) {
        List<int[]> items = new ArrayList<>();
        if (carritoStr == null || carritoStr.isEmpty()) return items;

        String[] partes = carritoStr.split("~");
        for (String parte : partes) {
            try {
                String[] datos = parte.split(":");
                int idProducto = Integer.parseInt(datos[0]);
                int cantidad = Integer.parseInt(datos[1]);
                if (cantidad > 0) {
                    items.add(new int[]{idProducto, cantidad});
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // Ignorar entradas mal formadas
            }
        }
        return items;
    }

    /**
     * Convierte la lista de items en texto y la graba en la cookie del carrito.
     *
     * @param response la respuesta HTTP donde añadir la cookie.
     * @param request la petición HTTP (para obtener el contextPath).
     * @param items los pares [idProducto, cantidad] a serializar.
     */
    private void guardarCookieCarrito(HttpServletResponse response, HttpServletRequest request, List<int[]> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append("~");
            sb.append(items.get(i)[0]).append(":").append(items.get(i)[1]);
        }

        Cookie cookie = new Cookie(Utils.COOKIE_CARRITO, sb.toString());
        cookie.setMaxAge(Utils.COOKIE_DURACION);
        cookie.setPath(request.getContextPath());
        response.addCookie(cookie);
    }

    /**
     * Cuenta el total de artículos en una lista de líneas de pedido.
     *
     * @param lineas las líneas del carrito.
     * @return el número total de artículos.
     */
    public int contarArticulos(List<LineaPedido> lineas) {
        int total = 0;
        for (LineaPedido lp : lineas) {
            total += lp.getCantidad();
        }
        return total;
    }

    /**
     * Cuenta el total de artículos almacenados en la cookie del carrito.
     *
     * @param request la petición HTTP.
     * @return el número total de artículos.
     */
    public int contarArticulosCookie(HttpServletRequest request) {
        List<int[]> items = parsearCookie(obtenerCookieCarrito(request));
        int total = 0;
        for (int[] item : items) {
            total += item[1];
        }
        return total;
    }
}
