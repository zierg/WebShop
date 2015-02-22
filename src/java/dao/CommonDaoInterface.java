/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;

/**
 *
 * @author Иван
 */
interface CommonDaoInterface<T> {
    boolean addDBObject(T object);
    boolean deleteDBObject(long objectId);
    boolean updateDBObject(T object);
    List<T> getAllDBObjects();
    T getDBObjectById(long objectId);
}
