package es.nexabyte.models;

import es.nexabyte.DAO.ILineaPedidoDAO;
import es.nexabyte.DAO.IPedidoDAO;
import es.nexabyte.DAOFactory.DAOFactory;
import es.nexabyte.beans.LineaPedido;
import es.nexabyte.beans.Pedido;
import es.nexabyte.beans.Usuario;
import java.util.List;

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
}
