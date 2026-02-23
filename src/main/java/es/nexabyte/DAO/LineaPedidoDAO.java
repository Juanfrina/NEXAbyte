package es.nexabyte.DAO;

import es.nexabyte.beans.Categoria;
import es.nexabyte.beans.LineaPedido;
import es.nexabyte.beans.Pedido;
import es.nexabyte.beans.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación DAO para la tabla lineaspedidos.
 * Todas las operaciones de escritura utilizan transacciones.
 * 
 * @author jfco1
 */
public class LineaPedidoDAO implements ILineaPedidoDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertar(LineaPedido linea) {
        String sql = "INSERT INTO lineaspedidos (idpedido, idproducto, cantidad) VALUES (?, ?, ?)";
        Connection con = null;
        boolean exito = false;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setShort(1, linea.getPedido().getIdPedido());
            ps.setShort(2, linea.getProducto().getIdProducto());
            ps.setInt(3, linea.getCantidad());
            exito = ps.executeUpdate() > 0;
            ps.close();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al insertar línea de pedido: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { }
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ex) { }
            }
            ConnectionFactory.closeConnection(con);
        }
        return exito;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LineaPedido> obtenerPorPedido(Short idPedido) {
        List<LineaPedido> lineas = new ArrayList<>();
        String sql = "SELECT lp.idlinea, lp.idpedido, lp.idproducto, lp.cantidad, "
                + "p.nombre, p.descripcion, p.precio, p.marca, p.imagen, p.idcategoria, "
                + "c.nombre AS nombre_categoria, c.imagen AS imagen_categoria "
                + "FROM lineaspedidos lp "
                + "INNER JOIN productos p ON lp.idproducto = p.idproducto "
                + "INNER JOIN categorias c ON p.idcategoria = c.idcategoria "
                + "WHERE lp.idpedido = ?";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setShort(1, idPedido);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LineaPedido lp = new LineaPedido();
                lp.setIdLinea(rs.getShort("idlinea"));
                lp.setCantidad(rs.getInt("cantidad"));

                // Pedido (solo con el id)
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getShort("idpedido"));
                lp.setPedido(pedido);

                // Producto completo
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

                lp.setProducto(prod);
                lineas.add(lp);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener líneas de pedido: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return lineas;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean actualizarCantidad(Short idLinea, Integer cantidad) {
        String sql = "UPDATE lineaspedidos SET cantidad = ? WHERE idlinea = ?";
        Connection con = null;
        boolean exito = false;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, cantidad);
            ps.setShort(2, idLinea);
            exito = ps.executeUpdate() > 0;
            ps.close();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al actualizar cantidad: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { }
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ex) { }
            }
            ConnectionFactory.closeConnection(con);
        }
        return exito;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eliminar(Short idLinea) {
        String sql = "DELETE FROM lineaspedidos WHERE idlinea = ?";
        Connection con = null;
        boolean exito = false;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setShort(1, idLinea);
            exito = ps.executeUpdate() > 0;
            ps.close();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al eliminar línea: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { }
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ex) { }
            }
            ConnectionFactory.closeConnection(con);
        }
        return exito;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eliminarPorPedido(Short idPedido) {
        String sql = "DELETE FROM lineaspedidos WHERE idpedido = ?";
        Connection con = null;
        boolean exito = false;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setShort(1, idPedido);
            exito = ps.executeUpdate() >= 0;
            ps.close();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al eliminar líneas del pedido: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { }
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ex) { }
            }
            ConnectionFactory.closeConnection(con);
        }
        return exito;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LineaPedido buscarPorPedidoYProducto(Short idPedido, Short idProducto) {
        LineaPedido lp = null;
        String sql = "SELECT * FROM lineaspedidos WHERE idpedido = ? AND idproducto = ?";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setShort(1, idPedido);
            ps.setShort(2, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lp = new LineaPedido();
                lp.setIdLinea(rs.getShort("idlinea"));
                lp.setCantidad(rs.getInt("cantidad"));

                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getShort("idpedido"));
                lp.setPedido(pedido);

                Producto prod = new Producto();
                prod.setIdProducto(rs.getShort("idproducto"));
                lp.setProducto(prod);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al buscar línea: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return lp;
    }
}
