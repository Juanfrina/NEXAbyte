package es.nexabyte.beans;
import java.io.Serializable;
/**
 * Clase que representa un producto en el sistema.
 * Implementa la interfaz {@link Serializable} para permitir su serialización.
 * @author jfco1
 */
public class Producto implements Serializable {
    /**
     * Identificador único del producto.
     */
    private Short idProducto;

    /**
     * La categoría a la que pertenece el producto.
     */
    private Categoria categoria;

    /**
     * Nombre del producto.
     */
    private String nombre;

    /**
     * Descripción del producto.
     */
    private String descripcion;

    /**
     * Precio del producto.
     */
    private Double precio;

    /**
     * Marca del producto.
     */
    private String marca;

    /**
     * Imagen asociada al producto.
     */
    private String imagen;

    /**
     * Obtiene el identificador del producto.
     *
     * @return un valor {@link Short} que representa el id del producto.
     */
    public Short getIdProducto() {
        return idProducto;
    }

    /**
     * Establece el identificador del producto.
     *
     * @param idProducto un valor {@link Short} que representa el id del producto.
     */
    public void setIdProducto(Short idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * Obtiene la categoría del producto.
     *
     * @return un objeto {@link Categoria} que representa la categoría del producto.
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría del producto.
     *
     * @param categoria un objeto {@link Categoria} que representa la categoría del producto.
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return un valor {@link String} que representa el nombre del producto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del producto.
     *
     * @param nombre un valor {@link String} que representa el nombre del producto.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del producto.
     *
     * @return un valor {@link String} que representa la descripción del producto.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del producto.
     *
     * @param descripcion un valor {@link String} que representa la descripción del producto.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el precio del producto.
     *
     * @return un valor {@link Double} que representa el precio del producto.
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio del producto.
     *
     * @param precio un valor {@link Double} que representa el precio del producto.
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene la marca del producto.
     *
     * @return un valor {@link String} que representa la marca del producto.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Establece la marca del producto.
     *
     * @param marca un valor {@link String} que representa la marca del producto.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Obtiene la imagen asociada al producto.
     *
     * @return un valor {@link String} que representa la imagen del producto.
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Establece la imagen asociada al producto.
     *
     * @param imagen un valor {@link String} que representa la imagen del producto.
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
