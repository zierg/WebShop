/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import static common.LogManager.LOG;

/**
 *
 * @author Ivan
 */
public class TomcatFilter implements Filter {
    
    private String encoding;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        encoding = filterConfig.getInitParameter("requestEncoding");
        if (encoding == null) {
            encoding = "UTF-8";
            if (LOG.isDebugEnabled()) {
                LOG.debug("Не удалось считать параметры кодировки. Применена кодировка " + encoding);
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("TomcatFilter запущен. Имя фильтра: " + filterConfig.getFilterName());
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getMethod().equalsIgnoreCase("POST")) {
            request.setCharacterEncoding(encoding);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Перекодировка");
            }
        } 
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("TomcatFilter уничтожен.");
        }
    }
}
