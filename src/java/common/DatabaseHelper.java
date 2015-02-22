/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.Locale;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import objects.Book;
import static common.LogManager.LOG;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Иван
 */
public final class DatabaseHelper {

    private DatabaseHelper() {
    }

    public static String toStringFromClob(Clob clob) throws SQLException {
        int length = (int) clob.length();
        String s = null;
        if (length > 0) {
            s = clob.getSubString(1, length);
        }
        LOG.debug("s = " + s);
        return s;
    }

    public static Book testBook() {
        DataSource sour = null;
        Locale.setDefault(Locale.ENGLISH);
        try {
            InitialContext initContext;
            initContext = new InitialContext();
            sour = (DataSource) initContext.lookup("java:comp/env/jdbc/ShopDataSource");
        } catch (NamingException ex) {
            LOG.error("Ошибка чтения ресурса базы данных.", ex);
        }
        try (Connection con = sour.getConnection()) {
            PreparedStatement ps = con.prepareStatement("Select * from authors");
            //prepareInsertStatement(ps, object);
            ResultSet rs = ps.executeQuery();
            rs.next();//rs.next();
            Book book = new Book();
            //book.setBookId(rs.getInt("book_id"));
            book.setDescription(DatabaseHelper.toStringFromClob(rs.getClob("biography")));
            return book;
        } catch (SQLException ex) {
            LOG.error(ex);
            return null;
        }
    }
}
