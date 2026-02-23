package es.nexabyte.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Fábrica de conexiones a la base de datos mediante Pool de conexiones (JNDI).
 * Utiliza el DataSource configurado en context.xml para obtener conexiones
 * del pool gestionado por el contenedor (Tomcat).
 * 
 * @author jfco1
 */
public class ConnectionFactory {

    /** DataSource del pool de conexiones. */
    private static DataSource dataSource;

    /*
     * Bloque estático que inicializa el DataSource al cargar la clase.
     * Busca el recurso JNDI "jdbc/nexabyte" configurado en context.xml.
     */
    static {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/nexabyte");
        } catch (NamingException e) {
            throw new ExceptionInInitializerError(
                "Error al inicializar el pool de conexiones: " + e.getMessage());
        }
    }

    /**
     * Obtiene una conexión del pool de conexiones.
     * 
     * @return una {@link Connection} activa del pool.
     * @throws SQLException si no se puede obtener la conexión.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Cierra una conexión devolviéndola al pool.
     * 
     * @param connection la conexión a cerrar/devolver al pool.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
