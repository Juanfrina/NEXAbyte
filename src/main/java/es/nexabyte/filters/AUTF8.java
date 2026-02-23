package es.nexabyte.filters;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

/**
 * Filtro que fuerza la codificación UTF-8 en todas las peticiones.
 * Se aplica a todas las URLs para evitar problemas con acentos y caracteres especiales.
 *
 * @author jfco1
 */
@WebFilter(filterName = "AUTF8", urlPatterns = {"/*"})
public class AUTF8 implements Filter {

    /**
     * Inicialización del filtro. No necesita configuración extra.
     *
     * @param fConfig configuración del filtro.
     * @throws ServletException si algo falla al arrancar.
     */
    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        
    }

    /**
     * Establece UTF-8 en cada petición antes de que llegue al servlet.
     *
     * @param request la petición entrante.
     * @param response la respuesta saliente.
     * @param chain la cadena de filtros para seguir adelante.
     * @throws IOException si hay error de E/S.
     * @throws ServletException si hay error en el servlet.
     */
    @Override   
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    /**
     * Limpieza al destruir el filtro. No hay nada que liberar.
     */
    @Override
    public void destroy() {
    }
}
