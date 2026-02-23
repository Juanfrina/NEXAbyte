package es.nexabyte.DAO;

import es.nexabyte.beans.Producto;
import java.util.List;

/**
 * Interfaz DAO para operaciones CRUD sobre la tabla productos.
 * Define los métodos de acceso a datos para los productos de la tienda.
 * 
 * @author jfco1
 */
public interface IProductoDAO {

    /**
     * Obtiene todos los productos de la base de datos.
     * 
     * @return una lista con todos los productos.
     */
    List<Producto> obtenerTodos();

    /**
     * Obtiene una selección aleatoria de productos.
     * 
     * @param cantidad el número de productos aleatorios a obtener.
     * @return una lista con la cantidad indicada de productos aleatorios.
     */
    List<Producto> obtenerAleatorios(int cantidad);

    /**
     * Obtiene un producto por su identificador.
     * 
     * @param idProducto el identificador del producto.
     * @return el {@link Producto} encontrado o null si no existe.
     */
    Producto obtenerPorId(Short idProducto);

    /**
     * Obtiene los productos que pertenecen a una categoría concreta.
     * 
     * @param idCategoria el identificador de la categoría.
     * @return una lista de productos de esa categoría.
     */
    List<Producto> obtenerPorCategoria(Byte idCategoria);

    /**
     * Busca productos aplicando múltiples filtros simultáneamente.
     * Los parámetros nulos o vacíos se ignoran en la búsqueda.
     * 
     * @param nombre texto a buscar en el nombre del producto.
     * @param idCategoria identificador de la categoría (null para todas).
     * @param marca marca del producto (null para todas).
     * @param precioMin precio mínimo (null para sin límite inferior).
     * @param precioMax precio máximo (null para sin límite superior).
     * @return una lista de productos que cumplen los filtros.
     */
    List<Producto> buscar(String nombre, Byte idCategoria, String marca,
                          Double precioMin, Double precioMax);

    /**
     * Obtiene la lista de marcas distintas disponibles.
     * 
     * @return una lista de nombres de marcas sin duplicados.
     */
    List<String> obtenerMarcas();

    /**
     * Obtiene el precio máximo entre todos los productos.
     * Se usa para establecer el límite del slider de filtro de precio.
     * 
     * @return el precio máximo o 0.0 si no hay productos.
     */
    Double obtenerPrecioMaximo();
}
