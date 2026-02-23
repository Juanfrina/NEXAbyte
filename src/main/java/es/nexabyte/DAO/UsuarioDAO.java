package es.nexabyte.DAO;

import es.nexabyte.beans.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Implementación DAO para la tabla usuarios.
 * Todas las operaciones de escritura utilizan transacciones.
 * 
 * @author jfco1
 */
public class UsuarioDAO implements IUsuarioDAO {

    /**
     * Convierte una fila del ResultSet en un objeto Usuario.
     *
     * @param rs el ResultSet posicionado en la fila a leer.
     * @return el Usuario con todos sus campos cargados.
     * @throws SQLException si falla alguna lectura de columna.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getShort("idusuario"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setNombre(rs.getString("nombre"));
        u.setApellidos(rs.getString("apellidos"));
        u.setNif(rs.getString("nif"));
        u.setTelefono(rs.getString("telefono"));
        u.setDireccion(rs.getString("direccion"));
        u.setCodigoPostal(rs.getString("codigo_postal"));
        u.setLocalidad(rs.getString("localidad"));
        u.setProvincia(rs.getString("provincia"));
        u.setUltimoAcceso(rs.getTimestamp("ultimo_acceso"));
        u.setAvatar(rs.getString("avatar"));
        return u;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (email, password, nombre, apellidos, nif, "
                + "telefono, direccion, codigo_postal, localidad, provincia, avatar) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        boolean exito = false;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false); // Transacción

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario.getEmail());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getApellidos());
            ps.setString(5, usuario.getNif());
            ps.setString(6, usuario.getTelefono());
            ps.setString(7, usuario.getDireccion());
            ps.setString(8, usuario.getCodigoPostal());
            ps.setString(9, usuario.getLocalidad());
            ps.setString(10, usuario.getProvincia());
            ps.setString(11, usuario.getAvatar() != null ? usuario.getAvatar() : "default.png");

            exito = ps.executeUpdate() > 0;
            ps.close();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
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
    public Usuario obtenerPorEmail(String email) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario = mapearUsuario(rs);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por email: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return usuario;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al comprobar email: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Usuario validarCredenciales(String email, String password) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE email = ? AND password = ?";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario = mapearUsuario(rs);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return usuario;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET password = ?, nombre = ?, apellidos = ?, "
                + "telefono = ?, direccion = ?, codigo_postal = ?, localidad = ?, "
                + "provincia = ?, avatar = ? WHERE idusuario = ?";
        Connection con = null;
        boolean exito = false;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario.getPassword());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellidos());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getDireccion());
            ps.setString(6, usuario.getCodigoPostal());
            ps.setString(7, usuario.getLocalidad());
            ps.setString(8, usuario.getProvincia());
            ps.setString(9, usuario.getAvatar());
            ps.setShort(10, usuario.getIdUsuario());

            exito = ps.executeUpdate() > 0;
            ps.close();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
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
    public void actualizarUltimoAcceso(Short idUsuario) {
        String sql = "UPDATE usuarios SET ultimo_acceso = ? WHERE idusuario = ?";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setShort(2, idUsuario);
            ps.executeUpdate();
            ps.close();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error al actualizar último acceso: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { }
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ex) { }
            }
            ConnectionFactory.closeConnection(con);
        }
    }
}
