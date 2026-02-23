package es.nexabyte.DAO;

import es.nexabyte.beans.Pedido;
import java.util.List;

/**
 * Interfaz DAO para operaciones CRUD sobre la tabla pedidos.
 * Define los métodos de acceso a datos para la gestión de pedidos.
 * 
 * @author jfco1
 */
public interface IPedidoDAO {

    /**
     * Inserta un nuevo pedido (carrito) en la base de datos.
     * 
     * @param pedido el objeto {@link Pedido} a insertar.
     * @return el identificador generado del nuevo pedido.
     */
    Short insertar(Pedido pedido);

    /**
     * Obtiene el carrito activo (estado 'c') de un usuario.
     * 
     * @param idUsuario el identificador del usuario.
     * @return el {@link Pedido} en estado carrito o null si no tiene.
     */
    Pedido obtenerCarrito(Short idUsuario);

    /**
     * Obtiene los pedidos finalizados (estado 'f') de un usuario.
     * 
     * @param idUsuario el identificador del usuario.
     * @return una lista de pedidos finalizados.
     */
    List<Pedido> obtenerFinalizados(Short idUsuario);

    /**
     * Finaliza un pedido cambiando su estado de 'c' a 'f'.
     * Calcula y establece el importe (base imponible) y el IVA.
     * 
     * @param idPedido el identificador del pedido a finalizar.
     * @return true si se finalizó correctamente.
     */
    boolean finalizar(Short idPedido);

    /**
     * Recalcula y actualiza el importe (base imponible) y el IVA de un pedido
     * sumando precio × cantidad de todas sus líneas.
     * 
     * @param idPedido el identificador del pedido.
     * @return true si la actualización fue exitosa.
     */
    boolean actualizarImportes(Short idPedido);

    /**
     * Elimina un pedido y todas sus líneas asociadas.
     * 
     * @param idPedido el identificador del pedido a eliminar.
     * @return true si se eliminó correctamente.
     */
    boolean eliminar(Short idPedido);
}
