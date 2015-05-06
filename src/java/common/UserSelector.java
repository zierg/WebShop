/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import dao.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import objects.Book;
import objects.ShoppingCart;
import objects.User;

/**
 * Класс для работы с данными в БД, связанными с юзером. Конструктор закрытый,
 * получить экземпляр можно из класса Selector
 * @author Иван
 */
public class UserSelector {

    private final DataSource dataSource;

    UserSelector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Проверка, существует ли юзер с таким логином
     * @param username логин
     * @return 
     */
    public boolean doesUserExist(String username) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select count(0) user_exists from users where login = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getBoolean("user_exists");
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Зарегистрировать юзера (в БД добавляются только логин, пароль и is_admin)
     * @param user 
     */
    public void registerUser(User user) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("insert into users(login, password, is_admin) values(?,?,?)");
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getIsAdmin());
            ps.execute();
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Получить все данные юзера с известным логином
     * @param login логин юзера
     * @return 
     */
    public User getUserByLogin(String login) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from users where login = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            rs.next();
            User user = new User();
            user.setUserId(rs.getLong("user_id"));
            user.setIsAdmin(rs.getBoolean("is_admin"));
            user.setLogin(login);
            user.setMail(rs.getString("mail"));
            user.setMiddlename(rs.getString("middlename"));
            user.setSurname(rs.getString("surname"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Сохранить данные пользователя (ФИО и е-мейл). У юзера должен быть заполнен ИД.
     * @param user 
     */
    public void saveUserData(User user) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("update users set name = ?, surname = ?, middlename = ?, mail = ? where user_id = ?");
            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getMiddlename());
            ps.setString(4, user.getMail());
            ps.setLong(5, user.getUserId());
            ps.execute();
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Сохранить логин и пароль пользователя. У юзера должен быть заполнен ИД.
     * @param user 
     */
    public void saveLoginData(User user) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("update users set password = ? where user_id = ?");
            ps.setString(1, user.getPassword());
            ps.setLong(2, user.getUserId());
            ps.execute();
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Добавить книгу в корзину (только в таблицу shopping_cart! объект в сессии
     * меняется сервлетом).
     * @param userId ИД юзера
     * @param bookId  ИД книги
     */
    public void addToCart(long userId, long bookId) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "merge into shopping_carts sc using "
                            + " (select * from (select ? book_id, ? user_id from dual) t "
                            + " where not exists(select 0 from history h where h.user_id = t.user_id"
                            + " and h.book_id = t.book_id)) t"
                            + " on (sc.book_id = t.book_id and sc.user_id = t.user_id)"
                            + " when not matched then"
                            + " insert (user_id, book_id) values (t.user_id, t.book_id)");
            ps.setLong(1, bookId);
            ps.setLong(2, userId);
            ps.execute();
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Удалить книгу из корзины
     * @param userId
     * @param bookId 
     */
    public void removeFromCart(long userId, long bookId) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "delete from shopping_carts where user_id = ? and book_id = ?");
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            ps.execute();
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Очистить корзину
     * @param userId 
     */
    public void clearCart(long userId) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "delete from shopping_carts where user_id = ?");
            ps.setLong(1, userId);
            ps.execute();
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
    /**
     * Получить корзину юзера
     * @param userId
     * @return 
     */
    public ShoppingCart getUsersCart(long userId) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "select book_id from shopping_carts where user_id = ?");
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            ShoppingCart cart = new ShoppingCart();
            while(rs.next()) {
                cart.addBookId(rs.getLong("book_id"));
            }
            return cart;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
    /**
     * Получить список ИДшников купленных книг. Проверяются только книги из списка
     * booksToCheck, т.е. книги, купленные пользователем, но отсутствующие в booksToCheck,
     * не попадут в результат метода. Метод используется для создания кнопок корзины
     * на страницах со списком книг. На одной странице только определённый диапазон книг,
     * его и нужно передать в booksToCheck.
     * @param booksToCheck
     * @param userId
     * @return 
     */
    public List<Long> getPurchasedBookIds(List<Book> booksToCheck, long userId) {
        StringBuilder b = new StringBuilder("select book_id from history where user_id = ? and book_id in (");
        for (int i = 0; i < booksToCheck.size(); i++) {
            b.append("?,");
        }
        b.replace(b.length()-1, b.length(), ")");
        String query = b.toString();
        List<Long> purchasedBookIds = new ArrayList<>();
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    query);
            ps.setLong(1, userId);
            int i = 2;
            for (Book book : booksToCheck) {
                ps.setLong(i, book.getBookId());
                i++;
            }
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                purchasedBookIds.add(rs.getLong("book_id"));
            }
            return purchasedBookIds;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
    /**
     * Проверить, купил ли юзер книгу
     * @param userId
     * @param bookId
     * @return 
     */
    public boolean checkBookPurchasing(long userId, long bookId) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "select count(0) existing from history where user_id = ? and book_id = ? and rownum = 1");
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return (rs.getBoolean("existing"));
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
    /**
     * Занести данные о покупке книги
     * @param userId
     * @param cart 
     */
    public void purchase(long userId, ShoppingCart cart) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "insert into history(user_id, book_id, amount, book_cost, purchase_date)"
                + " select sc.user_id, sc.book_id, 1, b.cost, current_date"
                + " from shopping_carts sc, books b"
                + " where b.book_id = sc.book_id"
                + " and sc.user_id = ?");
            ps.setLong(1, userId);
            ps.execute();
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
    /**
     * Проверить, может ли юзер пройти по ссылке для загрузки книги.
     * @param userId
     * @param link
     * @return true, если ссылка есть у книги в таблице books и юзер купил эту книгу
     */
    public boolean isLinkAccessableForUser(long userId, String link) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "select count(0) can_follow_link from books b, history h"
                + " where h.user_id = ? and b.book_id = h.book_id and "
                + " b.link = ?");
            ps.setLong(1, userId);
            ps.setString(2, link);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getBoolean("can_follow_link");
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
}
