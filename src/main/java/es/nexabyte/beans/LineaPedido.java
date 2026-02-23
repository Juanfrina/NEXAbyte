package es.nexabyte.beans;
import java.io.Serializable;
/**
 * Clase que representa una línea de pedido en un sistema de pedidos.
 * Cada línea asocia un producto con un pedido e indica la cantidad solicitada.
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
    private Pedido pedido;

    /**
     * El producto asociado a esta línea de pedido.
     */
    private Producto producto;

    /**
     * La cantidad del producto en la línea de pedido.
     */
    private Integer cantidad;

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
    public Pedido getPedido() {
        return pedido;
    }

    /**
     * Establece el pedido al que pertenece esta línea de pedido.
     *
     * @param pedido un objeto {@link Pedido} que representa el pedido al que se asociará esta línea de pedido.
     */
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    /**
     * Obtiene el producto asociado a esta línea de pedido.
     *
     * @return el {@link Producto} asociado a esta línea de pedido.
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Establece el producto asociado a esta línea de pedido.
     *
     * @param producto un objeto {@link Producto} que se asociará a esta línea de pedido.
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /**
     * Obtiene la cantidad del producto en esta línea de pedido.
     *
     * @return un valor {@link Integer} que representa la cantidad del producto.
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad del producto en esta línea de pedido.
     *
     * @param cantidad un valor {@link Integer} que representa la cantidad del producto en la línea de pedido.
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}
