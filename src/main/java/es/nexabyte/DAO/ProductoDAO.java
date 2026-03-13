package es.nexabyte.DAO;

import es.nexabyte.beans.Categoria;
import es.nexabyte.beans.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación DAO para la tabla productos.
 * Gestiona el acceso a datos de los productos de la tienda.
 * 
 * @author jfco1
 */
public class ProductoDAO implements IProductoDAO {

    /** Consulta base que une productos con categorías. */
    private static final String SQL_BASE = 
        "SELECT p.idproducto, p.idcategoria, p.nombre, p.descripcion, p.precio, " +
        "p.marca, p.imagen, c.nombre AS nombre_categoria, c.imagen AS imagen_categoria " +
        "FROM productos p INNER JOIN categorias c ON p.idcategoria = c.idcategoria";

    /**
     * Convierte una fila del ResultSet en un Producto con su Categoría.
     *
     * @param rs el ResultSet posicionado en la fila a leer.
     * @return el Producto con su categoría asociada.
     * @throws SQLException si falla alguna lectura de columna.
     */
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto prod = new Producto();
        prod.setIdProducto(rs.getShort("idproducto"));
        prod.setNombre(rs.getString("nombre"));
        prod.setDescripcion(rs.getString("descripcion"));
        prod.setPrecio(rs.getDouble("precio"));
        prod.setMarca(rs.getString("marca"));
        prod.setImagen(rs.getString("imagen"));

        Categoria cat = new Categoria();
        cat.setIdCategoria(rs.getByte("idcategoria"));
        cat.setNombre(rs.getString("nombre_categoria"));
        cat.setImagen(rs.getString("imagen_categoria"));
        prod.setCategoria(cat);

        return prod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = SQL_BASE + " ORDER BY p.nombre";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return productos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Producto> obtenerAleatorios(int cantidad) {
        List<Producto> productos = new ArrayList<>();
        String sql = SQL_BASE + " ORDER BY RAND() LIMIT ?";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, cantidad);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener productos aleatorios: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return productos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Producto obtenerPorId(Short idProducto) {
        Producto prod = null;
        String sql = SQL_BASE + " WHERE p.idproducto = ?";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setShort(1, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                prod = mapearProducto(rs);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return prod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Producto> obtenerPorCategoria(Byte idCategoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = SQL_BASE + " WHERE p.idcategoria = ? ORDER BY p.nombre";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setByte(1, idCategoria);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener productos por categoría: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return productos;
    }

    /**
     * {@inheritDoc}
     * Construye la consulta SQL dinámicamente según los filtros proporcionados.
     * Busca en nombre y descripción. Soporta múltiples categorías y marcas.
     */
    @Override
    public List<Producto> buscar(String nombre, List<Byte> idsCategorias, List<String> marcas,
                                  Double precioMin, Double precioMax) {
        List<Producto> productos = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SQL_BASE + " WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        // Filtro por nombre o descripción (búsqueda parcial)
        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND (p.nombre LIKE ? OR p.descripcion LIKE ?)");
            parametros.add("%" + nombre.trim() + "%");
            parametros.add("%" + nombre.trim() + "%");
        }

        // Filtro por categorías (múltiples con IN)
        if (idsCategorias != null && !idsCategorias.isEmpty()) {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < idsCategorias.size(); i++) {
                if (i > 0) placeholders.append(", ");
                placeholders.append("?");
                parametros.add(idsCategorias.get(i));
            }
            sql.append(" AND p.idcategoria IN (").append(placeholders).append(")");
        }

        // Filtro por marcas (múltiples con IN)
        if (marcas != null && !marcas.isEmpty()) {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < marcas.size(); i++) {
                if (i > 0) placeholders.append(", ");
                placeholders.append("?");
                parametros.add(marcas.get(i).trim());
            }
            sql.append(" AND p.marca IN (").append(placeholders).append(")");
        }

        // Filtro por precio mínimo
        if (precioMin != null) {
            sql.append(" AND p.precio >= ?");
            parametros.add(precioMin);
        }

        // Filtro por precio máximo
        if (precioMax != null) {
            sql.append(" AND p.precio <= ?");
            parametros.add(precioMax);
        }

        sql.append(" ORDER BY p.nombre");

        Connection con = null;
        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString());

            // Establecer los parámetros dinámicamente
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return productos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> obtenerMarcas() {
        List<String> marcas = new ArrayList<>();
        String sql = "SELECT DISTINCT marca FROM productos ORDER BY marca";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                marcas.add(rs.getString("marca"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener marcas: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return marcas;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double obtenerPrecioMaximo() {
        Double max = 0.0;
        String sql = "SELECT MAX(precio) AS max_precio FROM productos";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                max = rs.getDouble("max_precio");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener precio máximo: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return max;
    }
}
