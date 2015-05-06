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
import javax.servlet.annotation.WebFilter;
import objects.User;

/**
 * Фильтрует доступ к содержимому папки download.
 * Если ссылка неправильная или юзер не купил книгу,
 * то перенаправляет на страницу с ошибкой, иначе даёт скачать.
 * @author Ivan
 */
@WebFilter("/download/*")
public class DownloadFilter implements Filter {

    private Selector selector;
    private UserSelector userSelector;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        selector = Selector.getInstance();
        userSelector = selector.getUserSelector();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            String filename = req.getServletPath().replaceFirst("/download/", "");
            User user = (User) req.getSession(false).getAttribute("user");
            if (user == null || !userSelector.isLinkAccessableForUser(user.getUserId(), filename)) {
                req.getRequestDispatcher("/WEB-INF/unavailableLink.jsp").forward(request, response);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("DownloadFilter уничтожен.");
        }
    }
}
