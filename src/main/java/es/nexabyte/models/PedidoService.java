package es.nexabyte.models;

import es.nexabyte.DAO.ILineaPedidoDAO;
import es.nexabyte.DAO.IPedidoDAO;
import es.nexabyte.DAOFactory.DAOFactory;
import es.nexabyte.beans.LineaPedido;
import es.nexabyte.beans.Pedido;
import es.nexabyte.beans.Usuario;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servicio de lógica de negocio para la gestión de pedidos.
 * Centraliza la finalización de pedidos y la consulta de pedidos finalizados.
 *
 * @author jfco1
 */
public class PedidoService {

    /**
     * Finaliza el pedido del carrito del usuario: actualiza importes
     * y cambia el estado de 'c' (carrito) a 'f' (finalizado).
     *
     * @param usuario el usuario registrado.
     * @return true si se finalizó correctamente, false si no había carrito.
     */
    public boolean finalizar(Usuario usuario) {
        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());

        if (carrito != null) {
            pedidoDAO.actualizarImportes(carrito.getIdPedido());
            pedidoDAO.finalizar(carrito.getIdPedido());
            return true;
        }
        return false;
    }

    /**
     * Obtiene la lista de pedidos finalizados de un usuario.
     *
     * @param usuario el usuario registrado.
     * @return lista de pedidos finalizados.
     */
    public List<Pedido> obtenerFinalizados(Usuario usuario) {
        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        return pedidoDAO.obtenerFinalizados(usuario.getIdUsuario());
    }

    /**
     * Obtiene un pedido finalizado con sus líneas, verificando que pertenece al usuario.
     *
     * @param usuario el usuario propietario del pedido.
     * @param idPedido el id del pedido a consultar.
     * @return el pedido con sus líneas, o null si no existe o no pertenece al usuario.
     */
    public Pedido obtenerDetallePedido(Usuario usuario, Short idPedido) {
        IPedidoDAO pedidoDAO = DAOFactory.getPedidoDAO();
        ILineaPedidoDAO lineaDAO = DAOFactory.getLineaPedidoDAO();

        List<Pedido> pedidos = pedidoDAO.obtenerFinalizados(usuario.getIdUsuario());
        for (Pedido p : pedidos) {
            if (p.getIdPedido().equals(idPedido)) {
                List<LineaPedido> lineas = lineaDAO.obtenerPorPedido(idPedido);
                p.setLineasPedidos(lineas);
                return p;
            }
        }
        return null;
    }

    // ==================== OPERACIONES HTTP (movidas desde controladores) ====================

    /**
     * Procesa la finalización del pedido. Solo para usuarios registrados.
     *
     * @param request la petición HTTP.
     * @param response la respuesta HTTP.
     * @throws ServletException si falla el forward.
     * @throws IOException si hay error de E/S.
     */
    public void procesarFinalizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/GestionUsuario?op=formAcceder");
            return;
        }

        boolean ok = finalizar(usuario);
        if (ok) {
            sesion.setAttribute("numArticulos", 0);
            request.setAttribute("mensaje", "Pedido finalizado correctamente. ¡Gracias por tu compra!");
        } else {
            request.setAttribute("error", "No hay ningún pedido en curso.");
        }

        request.getRequestDispatcher("/JSP/avisos/notificacion.jsp").forward(request, response);
    }

    /**
     * Prepara la vista con la lista de pedidos finalizados del usuario.
     *
     * @param request la petición HTTP.
     * @return la ruta de la vista de pedidos.
     */
    public String prepararVistaVerPedidos(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

        if (usuario != null) {
            List<Pedido> pedidos = obtenerFinalizados(usuario);
            request.setAttribute("pedidos", pedidos);
        }
        return "/JSP/vistas/verPedidos.jsp";
    }

    /**
     * Prepara la vista con el detalle de un pedido finalizado.
     *
     * @param request la petición HTTP con el parámetro "id".
     * @return la ruta de la vista de detalle del pedido.
     */
    public String prepararVistaDetallePedido(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

        String idStr = request.getParameter("id");
        if (idStr != null && usuario != null) {
            try {
                Short idPedido = Short.parseShort(idStr);
                Pedido pedido = obtenerDetallePedido(usuario, idPedido);
                if (pedido != null) {
                    request.setAttribute("pedido", pedido);
                }
            } catch (NumberFormatException e) {
                // ID no válido
            }
        }
        return "/JSP/vistas/pedido.jsp";
    }
}
