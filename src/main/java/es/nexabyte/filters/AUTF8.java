package es.nexabyte.filters;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

@WebFilter(filterName = "AUTF8", urlPatterns = {"/*"})
public class AUTF8 implements Filter {

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        
    }

    @Override   
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
