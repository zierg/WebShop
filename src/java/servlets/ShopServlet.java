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
import common.UserSelector;
import java.util.ArrayList;
import objects.Author;
import objects.Category;
import objects.ShoppingCart;
import objects.User;

/**
 *
 * @author Иван
 */
@WebServlet(name = "ShopServlet", loadOnStartup = 1, urlPatterns = {
    "/books", "/book", "/author", "/category", "/register", "/logout", "/login",
    "/profile", "/saveUserData", "/saveLoginData", "/addToCart", "/removeFromCart",
    "/cart", "/purchase", "/clearCart", "/history"})
public class ShopServlet extends HttpServlet {

    private final Selector selector = new Selector();
    private final UserSelector userSelector = selector.getUserSelector();

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        checkCartExisting(request, response);
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
            case "/register": {
                registeringErrorOccured(request, response, null);
                break;
            }
            case "/logout": {
                logout(request, response);
                break;
            }
            case "/profile": {
                forward("/WEB-INF/user/profile.jsp", request, response);
                break;
            }
            case "/login": {
                redirect("/login.jsp", request, response);
                break;
            }
            case "/cart": {
                openCart(request, response);
                break;
            }
            case "/purchase": {
                purchase(request, response);
                break;
            }
            case "/clearCart": {
                clearCart(request, response);
                break;
            }
            case "/history": {
                openHistory(request, response);
                break;
            }
            default: {
                redirect("/login", request, response);
                break;
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        checkCartExisting(request, response);
        String userPath = request.getServletPath();
        switch (userPath) {
            case "/register": {
                register(request, response);
                break;
            }
            case "/login": {
                login(request, response);
                break;
            }
            case "/saveUserData": {
                saveUserData(request, response);
                break;
            }
            case "/saveLoginData": {
                saveLoginData(request, response);
                break;
            }
            case "/addToCart": {
                addToCart(request, response);
                break;
            }
            case "/removeFromCart": {
                removeFromCart(request, response);
                break;
            }
        }
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

    private void redirect(String path, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getServletContext().getContextPath() + path);
    }

    private void forward(String path, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(path).forward(request, response);
    }

    private void setErrorText(HttpServletRequest request, String errorText) {
        request.setAttribute("error_text", errorText);
    }

    private void setMessage(HttpServletRequest request, String message) {
        request.setAttribute("message_text", message);

    }

    private void setRedirectedMessage(HttpServletRequest request, String redirectedMessage) {
        request.getSession().setAttribute("redirected_message", redirectedMessage);
    }

    private void setRedirectedError(HttpServletRequest request, String errorText) {
        request.getSession().setAttribute("redirected_error", errorText);
    }

    private String prepareForSearch(String searchText) {
        StringBuilder sb = new StringBuilder();
        for (char ch : searchText.toCharArray()) {
            if (Character.isLetterOrDigit(ch) || ch == ' ') {
                sb.append(ch);
            } else {
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
            String inDescription = request.getParameter("in_description");
            String searchParameters = (inDescription != null && inDescription.equals("on")) ? "[in_description]" : "";
            books = selector.findBooks(preparedSearchText, first, last, searchParameters);
            totalAmount = selector.getSearchBooksCount(preparedSearchText, searchParameters);
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

        if (books.isEmpty()) {
            setErrorText(request, "По данному запросу ничего не найдено.");
        }
        
        User user = (User) request.getSession(false).getAttribute("user");
        List<Long> purchased;
        if (user != null) {
            purchased = userSelector.getPurchasedBookIds(books, user.getUserId());
        } else {
            purchased = new ArrayList<>();
        }
        
        request.setAttribute("purchased", purchased);
        request.setAttribute("books", books);
        request.setAttribute("total_amount", totalAmount);
        request.setAttribute("first", first);
        forward("/WEB-INF/books.jsp", request, response);
    }

    private void showBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long bookId = Long.parseLong(request.getParameter("book_id"));
        boolean purchased = false;
        User user = (User) request.getSession(false).getAttribute("user");
        if (user != null) {
            purchased = userSelector.checkBookPurchasing(user.getUserId(), bookId);
        }
        Book book = selector.getBook(bookId);
        request.setAttribute("purchased", purchased);
        request.setAttribute("book", book);
        forward("/WEB-INF/book.jsp", request, response);
    }

    private void showAuthor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long authorId = Long.parseLong(request.getParameter("author_id"));
        Author author = selector.getAuthor(authorId);
        request.setAttribute("author", author);
        forward("/WEB-INF/author.jsp", request, response);
    }

    private void showCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long categoryId = Long.parseLong(request.getParameter("category_id"));
        Category category = selector.getCategory(categoryId);
        request.setAttribute("category", category);
        forward("/WEB-INF/category.jsp", request, response);
    }

    private void register(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");

        if (username == null || username.isEmpty()) {
            registeringErrorOccured(request, response, "Введите имя пользователя.");
            return;
        }

        if (userSelector.doesUserExist(username)) {
            registeringErrorOccured(request, response, "Имя пользователя " + username + " уже занято.");
            return;
        }

        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");

        if (password1.isEmpty()) {
            registeringErrorOccured(request, response, "Введите пароль.");
            return;
        }

        if (!password1.equals(password2)) {
            registeringErrorOccured(request, response, "Введённые пароли не совпадают.");
            return;
        }

        User user = new User();
        user.setLogin(username);
        user.setPassword(password1);
        userSelector.registerUser(user);
        setRedirectedMessage(request, "Регистрация завершена! Вы можете заполнить дополнительные данные в личном кабинете.");
        request.getSession(true).setAttribute("user", user);
        redirect("/books", request, response);
    }

    private void registeringErrorOccured(HttpServletRequest request, HttpServletResponse response, String errorText)
            throws ServletException, IOException {
        setErrorText(request, errorText);
        forward("/register.jsp", request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getSession(true).setAttribute("user", null);
        response.sendRedirect(request.getServletContext().getContextPath() + "/books");
    }

    private void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");

        if (username == null || username.isEmpty()) {
            loginErrorOccured(request, response, "Введите имя пользователя.");
            return;
        }

        String enteringError = "Ошибка входа. Имя пользователя или пароль неверны.";
        if (!userSelector.doesUserExist(username)) {
            loginErrorOccured(request, response, enteringError);
            return;
        }

        String password = request.getParameter("password");

        User user = userSelector.getUserByLogin(username);
        if (!password.equals(user.getPassword())) {
            loginErrorOccured(request, response, enteringError);
            return;
        }
        
        ShoppingCart currentCart = (ShoppingCart) request.getSession(false).getAttribute("shopping_cart");
        
        
        for (long bookId : currentCart.getBookIds()) {
            userSelector.addToCart(user.getUserId(), bookId);
        }
        ShoppingCart userCart = userSelector.getUsersCart(user.getUserId());

        setRedirectedMessage(request, "Вход выполнен.");
        request.getSession(true).setAttribute("user", user);
        request.getSession(false).setAttribute("shopping_cart", userCart);
        response.sendRedirect(request.getServletContext().getContextPath() + "/books");
    }

    private void loginErrorOccured(HttpServletRequest request, HttpServletResponse response, String errorText)
            throws ServletException, IOException {
        setErrorText(request, errorText);
        forward("/login.jsp", request, response);
    }

    private void saveUserData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession(false).getAttribute("user");
        if (user == null) {
            redirect("/login", request, response);
            return;
        }
        String surname = request.getParameter("surname");
        String name = request.getParameter("name");
        String middlename = request.getParameter("middlename");
        String mail = request.getParameter("mail");
        user.setMail(mail);
        user.setMiddlename(middlename);
        user.setName(name);
        user.setSurname(surname);
        userSelector.saveUserData(user);
        setRedirectedMessage(request, "Пользовательские данные сохранены.");
        redirect("/profile", request, response);
    }

    private void saveLoginData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession(false).getAttribute("user");
        if (user == null) {
            redirect("/login", request, response);
            return;
        }
        String oldPassword = request.getParameter("old_password");
        if (!oldPassword.equals(user.getPassword())) {
            setRedirectedError(request, "Текущий пароль введён неверно.");
            redirect("/profile", request, response);
            return;
        }
        
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        
        if (password1.isEmpty()) {
            setRedirectedError(request, "Введите новый пароль.");
            redirect("/profile", request, response);
            return;
        }
        
        if (!password1.equals(password2)) {
            setRedirectedError(request, "Новые пароли не совпадают.");
            redirect("/profile", request, response);
            return;
        }
        
        user.setPassword(password1);
        userSelector.saveLoginData(user);
        setRedirectedMessage(request, "Новый пароль сохранён.");
        redirect("/profile", request, response);
    }

    private void addToCart(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String from = request.getParameter("from");
        Long bookId = Long.parseLong(request.getParameter("book_id"));
        ShoppingCart cart = (ShoppingCart) request.getSession(false).getAttribute("shopping_cart");
        User user = (User) request.getSession(false).getAttribute("user");
        if (user != null) {
            userSelector.addToCart(user.getUserId(), bookId);
        }
        cart.addBookId(bookId);
        response.sendRedirect(from);
    }
    
    private void checkCartExisting(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        ShoppingCart cart = (ShoppingCart) request.getSession(true).getAttribute("shopping_cart");
        if (cart == null) {
            cart = new ShoppingCart();
            request.getSession(false).setAttribute("shopping_cart", cart);
        }
    }

    private void removeFromCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String from = request.getParameter("from");
        Long bookId = Long.parseLong(request.getParameter("book_id"));
        ShoppingCart cart = (ShoppingCart) request.getSession(false).getAttribute("shopping_cart");
        User user = (User) request.getSession(false).getAttribute("user");
        if (user != null) {
            userSelector.removeFromCart(user.getUserId(), bookId);
        }
        cart.removeBookId(bookId);
        response.sendRedirect(from);
    }

    private void openCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ShoppingCart cart = (ShoppingCart) request.getSession(true).getAttribute("shopping_cart");
        List<Book> books = selector.getBooksFromCart(cart);
        request.setAttribute("books", books);
        forward("/WEB-INF/user/shoppingCart.jsp", request, response);
    }

    private void purchase(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession(false).getAttribute("user");
        if (user == null) {
            setRedirectedError(request, "Для покупки необходимо войти или зарегистрироваться.");
            redirect("/login", request, response);
            return;
        }
        ShoppingCart cart = (ShoppingCart) request.getSession(true).getAttribute("shopping_cart");
        userSelector.purchase(user.getUserId(), cart);
        userSelector.clearCart(user.getUserId());
        cart.getBookIds().clear();
        setRedirectedMessage(request, "Покупка завершена! Ссылки на скачивание приобретённых книг находятся в личном кабинете.");
        redirect("/history", request, response);
    }

    private void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ShoppingCart cart = (ShoppingCart) request.getSession(false).getAttribute("shopping_cart");
        User user = (User) request.getSession(false).getAttribute("user");
        if (user != null) {
            userSelector.clearCart(user.getUserId());
        }
        cart.getBookIds().clear();
        request.getSession(false).setAttribute("shopping_cart", cart);
        setRedirectedMessage(request, "Корзина очищена.");
        redirect("/cart", request, response);
    }

    private void openHistory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        User user = (User) request.getSession(false).getAttribute("user");
        if (user == null) {
            setRedirectedError(request, "Для просмотра покупок необходимо войти или зарегистрироваться.");
            redirect("/login", request, response);
            return;
        }
        
        List<Book> books = selector.getBooksFromHistory(user.getUserId());
        List<Long> purchased = new ArrayList<>();
        for (Book book : books) {
            purchased.add(book.getBookId());
        }
        request.setAttribute("books", books);
        request.setAttribute("purchased", purchased);
        forward("/WEB-INF/user/history.jsp", request, response);
    }
}
