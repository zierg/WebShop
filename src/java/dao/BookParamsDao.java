/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import objects.BookParam;

/**
 *
 * @author Иван
 */
public interface BookParamsDao  {
    boolean addParam(BookParam param);
    boolean deleteParam(long bookId, long attrId);
    boolean updateParam(BookParam param);
    List<BookParam> getParamsByBookId(long bookId);
    BookParam getParam(long bookId, long attrId);
}
