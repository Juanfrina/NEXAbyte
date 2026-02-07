package es.nexabyte.beans;
import java.io.Serializable;
/**
 * Clase que representa una categoría de producto.
 * Implementa la interfaz {@link Serializable} para permitir su serialización.
 * @author jfco1
 */
public class Categoria implements Serializable {
    /**
     * Identificador único de la categoría.
     */
    private Byte idCategoria;

    /**
     * Nombre de la categoría.
     */
    private String nombre;

    /**
     * Ruta o URL de la imagen asociada a la categoría.
     */
    private String imagen;

    /**
     * Obtiene el identificador de la categoría.
     *
     * @return un valor {@link Byte} que representa el id de la categoría.
     */
    public Byte getIdCategoria() {
        return idCategoria;
    }

    /**
     * Establece el identificador de la categoría.
     *
     * @param idCategoria un valor {@link Byte} que representa el id de la categoría.
     */
    public void setIdCategoria(Byte idCategoria) {
        this.idCategoria = idCategoria;
    }

    /**
     * Obtiene el nombre de la categoría.
     *
     * @return una cadena que representa el nombre de la categoría.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la categoría.
     *
     * @param nombre una cadena que representa el nombre de la categoría.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la ruta o URL de la imagen asociada a la categoría.
     *
     * @return una cadena que representa la ruta o URL de la imagen.
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Establece la ruta o URL de la imagen asociada a la categoría.
     *
     * @param imagen una cadena que representa la ruta o URL de la imagen.
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

}
