package es.nexabyte.DAOFactory;

import es.nexabyte.DAO.*;

/**
 * Factoría abstracta para crear instancias de los DAOs.
 * Implementa el patrón Factory para desacoplar la creación de DAOs
 * de su implementación concreta.
 * 
 * @author jfco1
 */
public class DAOFactory {

    /**
     * Crea y devuelve una instancia de {@link ICategoriaDAO}.
     * 
     * @return una implementación de ICategoriaDAO.
     */
    public static ICategoriaDAO getCategoriaDAO() {
        return new CategoriaDAO();
    }

    /**
     * Crea y devuelve una instancia de {@link IProductoDAO}.
     * 
     * @return una implementación de IProductoDAO.
     */
    public static IProductoDAO getProductoDAO() {
        return new ProductoDAO();
    }

    /**
     * Crea y devuelve una instancia de {@link IUsuarioDAO}.
     * 
     * @return una implementación de IUsuarioDAO.
     */
    public static IUsuarioDAO getUsuarioDAO() {
        return new UsuarioDAO();
    }

    /**
     * Crea y devuelve una instancia de {@link IPedidoDAO}.
     * 
     * @return una implementación de IPedidoDAO.
     */
    public static IPedidoDAO getPedidoDAO() {
        return new PedidoDAO();
    }

    /**
     * Crea y devuelve una instancia de {@link ILineaPedidoDAO}.
     * 
     * @return una implementación de ILineaPedidoDAO.
     */
    public static ILineaPedidoDAO getLineaPedidoDAO() {
        return new LineaPedidoDAO();
    }
}
