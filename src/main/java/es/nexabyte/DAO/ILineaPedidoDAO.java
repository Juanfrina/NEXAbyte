package es.nexabyte.DAO;

import es.nexabyte.beans.LineaPedido;
import java.util.List;

/**
 * Interfaz DAO para operaciones CRUD sobre la tabla lineaspedidos.
 * Define los métodos de acceso a datos para las líneas de pedido.
 * 
 * @author jfco1
 */
public interface ILineaPedidoDAO {

    /**
     * Inserta una nueva línea de pedido.
     * 
     * @param linea la {@link LineaPedido} a insertar.
     * @return true si la inserción fue exitosa.
     */
    boolean insertar(LineaPedido linea);

    /**
     * Obtiene todas las líneas de un pedido determinado.
     * 
     * @param idPedido el identificador del pedido.
     * @return una lista de líneas de pedido con sus productos cargados.
     */
    List<LineaPedido> obtenerPorPedido(Short idPedido);

    /**
     * Actualiza la cantidad de una línea de pedido.
     * 
     * @param idLinea el identificador de la línea.
     * @param cantidad la nueva cantidad.
     * @return true si la actualización fue exitosa.
     */
    boolean actualizarCantidad(Short idLinea, Integer cantidad);

    /**
     * Elimina una línea de pedido.
     * 
     * @param idLinea el identificador de la línea a eliminar.
     * @return true si se eliminó correctamente.
     */
    boolean eliminar(Short idLinea);

    /**
     * Elimina todas las líneas de un pedido.
     * 
     * @param idPedido el identificador del pedido.
     * @return true si se eliminaron correctamente.
     */
    boolean eliminarPorPedido(Short idPedido);

    /**
     * Busca si ya existe una línea para un producto en un pedido.
     * 
     * @param idPedido el identificador del pedido.
     * @param idProducto el identificador del producto.
     * @return la {@link LineaPedido} existente o null.
     */
    LineaPedido buscarPorPedidoYProducto(Short idPedido, Short idProducto);
}
