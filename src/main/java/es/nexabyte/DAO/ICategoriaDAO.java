package es.nexabyte.DAO;

import es.nexabyte.beans.Categoria;
import java.util.List;

/**
 * Interfaz DAO para operaciones CRUD sobre la tabla categorias.
 * Define los métodos de acceso a datos para las categorías de productos.
 * 
 * @author jfco1
 */
public interface ICategoriaDAO {

    /**
     * Obtiene todas las categorías de la base de datos.
     * 
     * @return una lista con todas las categorías.
     */
    List<Categoria> obtenerTodas();

    /**
     * Obtiene una categoría por su identificador.
     * 
     * @param idCategoria el identificador de la categoría.
     * @return la {@link Categoria} encontrada o null si no existe.
     */
    Categoria obtenerPorId(Byte idCategoria);
}
