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
import objects.Author;
import objects.Category;

/**
 *
 * @author Иван
 */
@WebServlet(name = "ShopServlet", loadOnStartup = 1, urlPatterns = {
        "/books", "/book", "/author", "/category"})
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
            case "/book": {
                showBook(request, response);
                break;
            }
            case "/author": {
                showAuthor(request, response);
                break;
            }
            case "/category": {
                showCategory(request, response);
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

    private String prepareForSearch(String searchText) {
        StringBuilder sb = new StringBuilder();
        for(char ch : searchText.toCharArray()) {
            if (Character.isLetterOrDigit(ch) || ch == ' ') {
                sb.append(ch);
            }
            else {
                sb.append(' ');
            }
        }
        return sb.toString().replaceAll("\\s+", " ").trim().toLowerCase();
    }
    
    private void showBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        long first;
        long last;
        String firstString = request.getParameter("first");
        if (firstString != null) {
            first = Long.parseLong(firstString);
        } else {
            first = 1;
        }
        last = first + DEFAULT_PAGE_SIZE - 1;
        List<Book> books;
        long totalAmount;
        String searchText = request.getParameter("search_text");
        String authorIdString = request.getParameter("author_id");
        String categoryIdString = request.getParameter("category_id");
        if (searchText != null && !searchText.isEmpty()) {
            String preparedSearchText = prepareForSearch(searchText);
            books = selector.findBooks(preparedSearchText, first, last);
            totalAmount = selector.getSearchBooksCount(preparedSearchText);
        } else if (authorIdString != null && !authorIdString.isEmpty()) {
            long authorId = Long.parseLong(authorIdString);
            books = selector.getBooksByAuthorId(first, last, authorId);
            totalAmount = selector.getAuthorsBooksCount(authorId);
        } else if (categoryIdString != null && !categoryIdString.isEmpty()) {
            long categoryId = Long.parseLong(categoryIdString);
            books = selector.getBooksByCategoryId(first, last, categoryId);
            totalAmount = selector.getCategoryBooksCount(categoryId);
        } else {
            books = selector.getBooks(first, last);
            totalAmount = selector.getAllBooksCount();
            
        }
        
        request.setAttribute("books", books);
        request.setAttribute("total_amount", totalAmount);
        request.setAttribute("first", first);
        request.getRequestDispatcher("/WEB-INF/books.jsp").forward(request, response);
    }

    private void showBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long bookId = Long.parseLong(request.getParameter("book_id"));
        Book book = selector.getBook(bookId);
        request.setAttribute("book", book);
        request.getRequestDispatcher("/WEB-INF/book.jsp").forward(request, response);
    }

    private void showAuthor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException  {
        long authorId = Long.parseLong(request.getParameter("author_id"));
        Author author = selector.getAuthor(authorId);
        request.setAttribute("author", author);
        request.getRequestDispatcher("/WEB-INF/author.jsp").forward(request, response);
    }

    private void showCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long categoryId = Long.parseLong(request.getParameter("category_id"));
        Category category = selector.getCategory(categoryId);
        request.setAttribute("category", category);
        request.getRequestDispatcher("/WEB-INF/category.jsp").forward(request, response);
    }
}
