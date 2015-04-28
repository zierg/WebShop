/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import static common.LogManager.LOG;
import dao.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import objects.Author;
import objects.Book;
import objects.Category;

/**
 *
 * @author Иван
 */
public class Selector {
    
    private DataSource dataSource;
    
    private static final String SELECT_AUTHORS_FOR_BOOK = "select\n" +
                                        "  a.author_id\n" +
                                        "  , a.name\n" +
                                        "  , a.surname\n" +
                                        "  , a.middlename\n" +
                                        "from\n" +
                                        "  authors a\n" +
                                        "  , books_authors ba\n" +
                                        "where\n" +
                                        "  a.author_id = ba.author_id\n" +
                                        "  and ba.book_id = ?";
    
    private static final String BOOKS_SELECT = "select t.*"
                                + ", rownum pos\n "
                                + " from"
                                + "(select \n" +
                                    "    b.book_id\n"
                                  + "  , b.title\n"
                                  + "  , b.cost\n"
                                  + "  , b.category_id\n" +
                                    "    , cat.title cat_name\n" +
                                    "  from\n" +
                                    "    books b\n" +
                                    "    , categories cat\n" +
                                    "  where\n" +
                                    "    b.category_id = cat.category_id\n" +
                                    "    and b.is_shown = 1\n"
                                    + " order by b.title) t" +
                                    "  ";
    
    public Selector() {
        Locale.setDefault(Locale.ENGLISH);
        try {
            InitialContext initContext;
            initContext = new InitialContext();
            dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/ShopDataSource");

        } catch (NamingException ex) {
            LOG.error("Ошибка чтения ресурса базы данных.", ex);
        }
    }
    
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public List<Book> getBooks(long first, long last) {
        List<Book> books = new ArrayList<>();
        
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from (" +BOOKS_SELECT + ") where pos between ? and ?");
            ps.setLong(1, first);
            ps.setLong(2, last);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Category category = makeCategoryForBook(rs);
                Book book = makeBookForList(rs, category);
                fillBookAuthors(con, book);
                books.add(book);
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return books;
    }
    
    private Category makeCategoryForBook(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getLong("category_id"));
        category.setTitle(rs.getString("cat_name"));
        return category;
    }
    
    private Book makeBookForList(ResultSet rs, Category category) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getLong("book_id"));
        book.setCategory(category);
        book.setCost(rs.getDouble("cost"));
        book.setTitle(rs.getString("title"));
        return book;
    }
    
    private void fillBookAuthors(Connection con, Book book) throws SQLException {
        PreparedStatement ps = con.prepareStatement(SELECT_AUTHORS_FOR_BOOK);
        ps.setLong(1, book.getBookId());
        List<Author> authors = book.getAuthors();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Author author = new Author();
            author.setAuthorId(rs.getLong("author_id"));
            author.setName(rs.getString("name"));
            author.setSurname(rs.getString("surname"));
            author.setMiddlename(rs.getString("middlename"));
            authors.add(author);
        }
    }
    
    public long getBooksCount() {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select count(0) total_amount from (" + BOOKS_SELECT + ")");
            ResultSet rs = ps.executeQuery();
            rs.next();
            long count = rs.getLong("total_amount");
            return count;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
}
