/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author Иван
 */
public interface DaoFactory {
    BookDao getBookDao();
    BookAttrsDao getBookAttrsDao();
    BookParamsDao getBookParamsDao();
}
