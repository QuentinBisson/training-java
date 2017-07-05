package fr.ebiz.computerdatabase.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Servlet filter used to encode requests to UTF-8.
 */
@WebFilter("/*")
public class EncodingFilter implements Filter {

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String charset = StandardCharsets.UTF_8.name();

        request.setCharacterEncoding(charset);

        // pass the request along the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nothing to do here
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Nothing to do here
    }
}