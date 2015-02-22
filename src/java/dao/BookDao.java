/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import objects.Book;

/**
 *
 * @author Иван
 */
public interface BookDao extends CommonDaoInterface<Book> {
    List<Book> getAllVisibleBooks();
    List<Book> getAuthorBooks(int authorId);
}
