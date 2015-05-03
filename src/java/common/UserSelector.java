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
import javax.sql.DataSource;
import objects.User;

/**
 *
 * @author Иван
 */
public class UserSelector {
    
    private DataSource dataSource;
    
    UserSelector(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
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
    
    public User getUserByLogin(String login) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from users where login = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            rs.next();
            User user = new User();
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
}
