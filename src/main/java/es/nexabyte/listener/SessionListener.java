package es.nexabyte.listener;

import es.nexabyte.DAO.ICategoriaDAO;
import es.nexabyte.DAOFactory.DAOFactory;
import es.nexabyte.beans.Categoria;
import es.nexabyte.beans.Pedido;
import es.nexabyte.models.EnumConverter;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.commons.beanutils.ConvertUtils;

/**
 * Listener del contexto de la aplicación.
 * Se ejecuta al iniciar y detener la aplicación web.
 * Carga las categorías como atributo de contexto accesible desde todas las páginas.
 * Registra los conversores necesarios para BeanUtils.
 * 
 * @author jfco1
 */
@WebListener
public class SessionListener implements ServletContextListener {

    /**
     * Se ejecuta al iniciar la aplicación.
     * Carga todas las categorías de la base de datos en el contexto de la aplicación.
     * Registra el conversor de enums para BeanUtils.
     * 
     * @param sce el evento de contexto del servlet.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext contexto = sce.getServletContext();

        // Registrar conversor de enums para BeanUtils
        ConvertUtils.register(new EnumConverter(), Pedido.Estado.class);

        // Cargar categorías en el contexto de la aplicación
        try {
            ICategoriaDAO categoriaDAO = DAOFactory.getCategoriaDAO();
            List<Categoria> categorias = categoriaDAO.obtenerTodas();
            contexto.setAttribute("categorias", categorias);
            System.out.println("NEXAbyte: Categorías cargadas en el contexto (" 
                + categorias.size() + " categorías).");
        } catch (Exception e) {
            System.err.println("NEXAbyte: Error al cargar categorías: " + e.getMessage());
        }
    }

    /**
     * Se ejecuta al detener la aplicación.
     * 
     * @param sce el evento de contexto del servlet.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("NEXAbyte: Aplicación detenida.");
    }
}
