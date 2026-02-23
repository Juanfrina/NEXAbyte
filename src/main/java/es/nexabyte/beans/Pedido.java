package es.nexabyte.beans;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * Clase que representa un pedido realizado por un usuario.
 * Implementa la interfaz {@link Serializable} para permitir su serialización.
 * @author jfco1
 */
public class Pedido implements Serializable {
     /**
     * Identificador único del pedido.
     */
    private Short idPedido;

    /**
     * Fecha en que se realizó el pedido.
     */
    private Date fecha;

    /**
     * Enum que representa el estado del pedido.
     * 'c' para carrito (pedido en curso) y 'f' para finalizado (pagado).
     */
    public enum Estado {
        c, // Carrito (pedido en curso)
        f  // Finalizado (pedido pagado)
    };

    /**
     * Estado actual del pedido.
     */
    private Estado estado;

    /**
     * Usuario que realizó el pedido.
     */
    private Usuario usuario;

    /**
     * Importe total del pedido antes de impuestos.
     */
    private Double importe;

    /**
     * Valor del IVA aplicado al pedido.
     */
    private Double iva;

    /**
     * Lista de líneas de pedido asociadas a este pedido.
     */
    private List<LineaPedido> lineasPedidos;

    /**
     * Obtiene el identificador del pedido.
     *
     * @return un valor {@link Short} que representa el id del pedido.
     */
    public Short getIdPedido() {
        return idPedido;
    }

    /**
     * Establece el identificador del pedido.
     *
     * @param idPedido un valor {@link Short} que representa el id del pedido.
     */
    public void setIdPedido(Short idPedido) {
        this.idPedido = idPedido;
    }

    /**
     * Obtiene la fecha del pedido.
     *
     * @return un objeto {@link Date} que representa la fecha del pedido.
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha del pedido.
     *
     * @param fecha un objeto {@link Date} que representa la fecha del pedido.
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el estado del pedido.
     *
     * @return el {@link Estado} actual del pedido.
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Establece el estado del pedido.
     *
     * @param estado un valor {@link Estado} que representa el nuevo estado del pedido.
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el usuario que realizó el pedido.
     *
     * @return el {@link Usuario} que realizó el pedido.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario que realizó el pedido.
     *
     * @param usuario un objeto {@link Usuario} que representa al usuario que realizó el pedido.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene el importe total del pedido antes de impuestos.
     *
     * @return un valor {@link Double} que representa el importe total del pedido.
     */
    public Double getImporte() {
        return importe;
    }

    /**
     * Establece el importe total del pedido antes de impuestos.
     *
     * @param importe un valor {@link Double} que representa el importe total del pedido.
     */
    public void setImporte(Double importe) {
        this.importe = importe;
    }

    /**
     * Obtiene el valor del IVA aplicado al pedido.
     *
     * @return un valor {@link Double} que representa el valor del IVA.
     */
    public Double getIva() {
        return iva;
    }

    /**
     * Establece el valor del IVA aplicado al pedido.
     *
     * @param iva un valor {@link Double} que representa el IVA aplicado al pedido.
     */
    public void setIva(Double iva) {
        this.iva = iva;
    }

    /**
     * Obtiene la lista de líneas de pedido asociadas a este pedido.
     *
     * @return una lista de objetos {@link LineaPedido} que representa las líneas de pedido.
     */
    public List<LineaPedido> getLineasPedidos() {
        return lineasPedidos;
    }

    /**
     * Establece la lista de líneas de pedido asociadas a este pedido.
     *
     * @param lineasPedidos una lista de objetos {@link LineaPedido} que representa las líneas de pedido.
     */
    public void setLineasPedidos(List<LineaPedido> lineasPedidos) {
        this.lineasPedidos = lineasPedidos;
    }
}
