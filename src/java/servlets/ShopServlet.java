/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import common.Selector;
import java.io.IOException;

import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import objects.Book;
import static common.Constants.*;

/**
 *
 * @author Иван
 */
@WebServlet(name = "ShopServlet", loadOnStartup = 1, urlPatterns = {
        "/books"})
public class ShopServlet extends HttpServlet {

    private final Selector selector = new Selector();

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userPath = request.getServletPath();
        switch (userPath) {
            case "/books": {
                showBooks(request, response);
                break;
            }
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userPath = request.getServletPath();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void showBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        long first;
        long last;
        String firstString = request.getParameter("first");
        if (firstString != null) {
            first = Long.parseLong(firstString);
            last = Long.parseLong(request.getParameter("last"));
        } else {
            first = 1;
            last = DEFAULT_PAGE_SIZE;
        }
        // Добавить проверку условий фильтрации!!
        String totalAmountString =request.getParameter("total_amount");
        long totalAmount;
        if (totalAmountString == null) {
            // Добавить проверку условий фильтрации!!
            totalAmount = selector.getBooksCount();
        } else {
            totalAmount = Long.parseLong(totalAmountString);
        }
        List<Book> books = selector.getBooks(first, last);
        last = first + books.size();
        request.setAttribute("books", books);
        request.setAttribute("total_amount", totalAmount);
        request.setAttribute("first", first);
        request.setAttribute("last", last);
        request.getRequestDispatcher("/WEB-INF/books.jsp").forward(request, response);
    }
}
