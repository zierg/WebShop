/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

import dao.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import oracle.conditions.ConditionCreator;

/**
 * Класс для упрощения создания других DAO для Oracle.
 * Содержит несколько абстрактных методов, подразумевающих логически
 * объединённую последовательность действий. Эти методы используются
 * при работе с БД.
 * @author Ivan
 */
abstract class OracleUniversalDAO<T> extends Abstract {

    public OracleUniversalDAO(DataSource dataSource) {
       super(dataSource);
    }
    /**
     * Добавить объект
     */
    protected final boolean addObject(T object) {
        final String INSERT = makeInsertStatement();
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(INSERT);
            prepareInsertStatement(ps, object);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Удалить объект по ID
     */
    protected final boolean deleteObjectByID(long id) {
        final String DELETE = makeDeleteStatement();
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(DELETE);
            ps.setLong(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Обвновить объект
     */
    protected final boolean updateObject(T object) {
        final String UPDATE = makeUpdateStatement();
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(UPDATE);
            prepareUpdateStatement(ps, object);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }

    }

    /**
     * Получаем список всех объектов
     */
    protected final List<T> getAllObjects(String tableName) {
        final String SELECT = "SELECT * FROM " + tableName;
        return getAllObjectsByCustomQuery(SELECT);
    }
    
    /**
     * Получаем список объектов на основании запроса, составленного пользователем.
     */
    protected final List<T> getAllObjectsByCustomQuery(String query) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            List<T> entityList = selectObjects(ps);
            return entityList;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
    protected final boolean executeCustomStatement(String statement) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(statement);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Получаем список объектов на основании результатов запроса с условием
     * @param creator класс-создатель запросов
     */
    protected final List<T> getObjectsWithConditions(ConditionCreator creator) {
        final String SELECT = creator.createSelect();
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(SELECT);
            creator.prepareSelectStatement(ps);
            List<T> objectList = selectObjects(ps);
            return objectList;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Возвращает один объект из запроса
     */
    protected final T getUniqueObject(ConditionCreator creator) {
        // Используем готовый метод, возвращающий список, и берём из списка первый элемент
        List<T> objectList = getObjectsWithConditions(creator);
        if (objectList.isEmpty()) {
            return null;
        } else {
            return objectList.get(0);
        }
    }

    /**
     * Делает запрос и возвращает список с объектами, созданными
     * на основе результатов запроса
     */
    private List<T> selectObjects(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        List<T> objectList = new ArrayList<>();
        while (rs.next()) {
            T newObject = makeObject(rs);
            objectList.add(newObject);
        }
        return objectList;
    }

    /**
     * Подготовить строку для вставки вида
     * "INSERT INTO TABLE(col1, col2, ..., col_n) values(?,?,...,?)"
     */
    protected abstract String makeInsertStatement();// 

    /**
     * Подготовить строку для удаления вида
     * "DELETE FROM TABLE WHERE ID = ?"
     */
    protected abstract String makeDeleteStatement();

    /**
     * Подготовить строку для обновления вида
     * "UPDATE TABLE SET col1 = ?, col2 = ?, ..., col_n = ?"
     */
    protected abstract String makeUpdateStatement();

    /**
     * Заменить в PreparedStatement'е для вставки знаки вопроса (?) на значения, хранящиеся в Object 
     */
    protected abstract void prepareInsertStatement(PreparedStatement ps, T object) throws SQLException;

    /**
     * Заменить в PreparedStatement'е для обновления знаки вопроса (?) на значения, хранящиеся в Object 
     */
    protected abstract void prepareUpdateStatement(PreparedStatement ps, T object) throws SQLException;

    /**
     * Создать объект, заполненый значениями, взятыми из текущей строки результатов запроса
     */
    protected abstract T makeObject(ResultSet rs) throws SQLException;
}
