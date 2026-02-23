package es.nexabyte.DAO;

import es.nexabyte.beans.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación DAO para la tabla categorias.
 * Gestiona el acceso a datos de las categorías de productos.
 * 
 * @author jfco1
 */
public class CategoriaDAO implements ICategoriaDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Categoria> obtenerTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT idcategoria, nombre, imagen FROM categorias ORDER BY nombre";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setIdCategoria(rs.getByte("idcategoria"));
                cat.setNombre(rs.getString("nombre"));
                cat.setImagen(rs.getString("imagen"));
                categorias.add(cat);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return categorias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Categoria obtenerPorId(Byte idCategoria) {
        Categoria cat = null;
        String sql = "SELECT idcategoria, nombre, imagen FROM categorias WHERE idcategoria = ?";
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setByte(1, idCategoria);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cat = new Categoria();
                cat.setIdCategoria(rs.getByte("idcategoria"));
                cat.setNombre(rs.getString("nombre"));
                cat.setImagen(rs.getString("imagen"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener categoría por ID: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return cat;
    }
}
