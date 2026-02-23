package es.nexabyte.DAO;

import es.nexabyte.beans.Pedido;
import es.nexabyte.beans.Usuario;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación DAO para la tabla pedidos.
 * Todas las operaciones de escritura utilizan transacciones.
 * 
 * @author jfco1
 */
public class PedidoDAO implements IPedidoDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public Short insertar(Pedido pedido) {
        String sql = "INSERT INTO pedidos (fecha, estado, idusuario, importe, iva) VALUES (?, ?, ?, ?, ?)";
        Connection con = null;
        Short idGenerado = null;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, new Date(System.currentTimeMillis()));
            ps.setString(2, pedido.getEstado() != null ? pedido.getEstado().name() : "c");
            ps.setShort(3, pedido.getUsuario().getIdUsuario());
            ps.setObject(4, pedido.getImporte());
            ps.setObject(5, pedido.getIva());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                idGenerado = keys.getShort(1);
            }
            keys.close();
            ps.close();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al insertar pedido: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { }
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ex) { }
            }
            ConnectionFactory.closeConnection(con);
        }
        return idGenerado;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pedido obtenerCarrito(Short idUsuario) {
        Pedido pedido = null;
        String sql = "SELECT * FROM pedidos WHERE idusuario = ? AND estado = 'c'";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setShort(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pedido = mapearPedido(rs);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener carrito: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return pedido;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pedido> obtenerFinalizados(Short idUsuario) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE idusuario = ? AND estado = 'f' ORDER BY fecha DESC";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setShort(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos finalizados: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return pedidos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean finalizar(Short idPedido) {
        String sql = "UPDATE pedidos SET estado = 'f', fecha = ? WHERE idpedido = ?";
        Connection con = null;
        boolean exito = false;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, new Date(System.currentTimeMillis()));
            ps.setShort(2, idPedido);
            exito = ps.executeUpdate() > 0;
            ps.close();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al finalizar pedido: " + e.getMessage());
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
    public boolean actualizarImportes(Short idPedido) {
        // 1. Calcular base imponible sumando precio * cantidad de cada línea
        String sqlSuma = "SELECT COALESCE(SUM(p.precio * lp.cantidad), 0) AS base "
                + "FROM lineaspedidos lp "
                + "JOIN productos p ON lp.idproducto = p.idproducto "
                + "WHERE lp.idpedido = ?";
        String sqlUpdate = "UPDATE pedidos SET importe = ?, iva = ? WHERE idpedido = ?";
        Connection con = null;
        boolean exito = false;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            // Obtener base imponible
            double baseImponible = 0;
            PreparedStatement psSuma = con.prepareStatement(sqlSuma);
            psSuma.setShort(1, idPedido);
            ResultSet rs = psSuma.executeQuery();
            if (rs.next()) {
                baseImponible = rs.getDouble("base");
            }
            rs.close();
            psSuma.close();

            // Calcular IVA (21%)
            double iva = baseImponible * 0.21;

            // Actualizar pedido
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
            psUpdate.setDouble(1, baseImponible);
            psUpdate.setDouble(2, iva);
            psUpdate.setShort(3, idPedido);
            exito = psUpdate.executeUpdate() > 0;
            psUpdate.close();

            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al actualizar importes: " + e.getMessage());
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
    public boolean eliminar(Short idPedido) {
        Connection con = null;
        boolean exito = false;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            // Primero eliminar líneas de pedido
            PreparedStatement psLineas = con.prepareStatement(
                "DELETE FROM lineaspedidos WHERE idpedido = ?");
            psLineas.setShort(1, idPedido);
            psLineas.executeUpdate();
            psLineas.close();

            // Luego eliminar el pedido
            PreparedStatement psPedido = con.prepareStatement(
                "DELETE FROM pedidos WHERE idpedido = ?");
            psPedido.setShort(1, idPedido);
            exito = psPedido.executeUpdate() > 0;
            psPedido.close();

            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al eliminar pedido: " + e.getMessage());
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
     * Convierte una fila del ResultSet en un objeto Pedido.
     *
     * @param rs el ResultSet posicionado en la fila a leer.
     * @return el Pedido con sus campos cargados.
     * @throws SQLException si falla alguna lectura de columna.
     */
    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getShort("idpedido"));
        p.setFecha(rs.getDate("fecha"));
        String estado = rs.getString("estado");
        if (estado != null) {
            p.setEstado(Pedido.Estado.valueOf(estado));
        }
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getShort("idusuario"));
        p.setUsuario(u);
        p.setImporte(rs.getObject("importe") != null ? rs.getDouble("importe") : null);
        p.setIva(rs.getObject("iva") != null ? rs.getDouble("iva") : null);
        return p;
    }
}
