package es.nexabyte.beans;
import java.io.Serializable;
/**
 * Clase que representa una línea de pedido en un sistema de pedidos.
 * Implementa la interfaz {@link Serializable} para permitir su serialización.
 * @author jfco1
 */
public class LineaPedido implements Serializable {
    /**
     * Identificador único de la línea de pedido.
     */
    private Short idLinea;

    /**
     * El pedido al que pertenece esta línea de pedido.
     */
    private Pedido idPedido;

    /**
     * El producto asociado a esta línea de pedido.
     */
    private Producto idProducto;

    /**
     * La cantidad del producto en la línea de pedido.
     */
    private Byte cantidad;

    /**
     * Obtiene el identificador de la línea de pedido.
     *
     * @return un valor {@link Short} que representa el id de la línea de pedido.
     */
    public Short getIdLinea() {
        return idLinea;
    }

    /**
     * Establece el identificador de la línea de pedido.
     *
     * @param idLinea un valor {@link Short} que representa el id de la línea de pedido.
     */
    public void setIdLinea(Short idLinea) {
        this.idLinea = idLinea;
    }

    /**
     * Obtiene el pedido al que pertenece esta línea de pedido.
     *
     * @return el {@link Pedido} asociado a esta línea de pedido.
     */
    public Pedido getIdPedido() {
        return idPedido;
    }

    /**
     * Establece el pedido al que pertenece esta línea de pedido.
     *
     * @param idPedido un objeto {@link Pedido} que representa el pedido al que se asociará esta línea de pedido.
     */
    public void setIdPedido(Pedido idPedido) {
        this.idPedido = idPedido;
    }

    /**
     * Obtiene el producto asociado a esta línea de pedido.
     *
     * @return el {@link Producto} asociado a esta línea de pedido.
     */
    public Producto getIdProducto() {
        return idProducto;
    }

    /**
     * Establece el producto asociado a esta línea de pedido.
     *
     * @param idProducto un objeto {@link Producto} que se asociará a esta línea de pedido.
     */
    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * Obtiene la cantidad del producto en esta línea de pedido.
     *
     * @return un valor {@link Byte} que representa la cantidad del producto.
     */
    public Byte getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad del producto en esta línea de pedido.
     *
     * @param cantidad un valor {@link Byte} que representa la cantidad del producto en la línea de pedido.
     */
    public void setCantidad(Byte cantidad) {
        this.cantidad = cantidad;
    }

}
