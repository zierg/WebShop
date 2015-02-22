/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

import objects.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Класс, предоставляющий подключения к БД для тех, кто от него наследуется.
 * 
 * Также содержит методы для создания DTO-объектов на основе результатов
 * запроса к БД (ResultSet-а). 
 * 
 * ВАЖНО: если не указано иначе, то заголовки столбцов
 * в ResultSet-е должны соответствовать заголовкам из БД (можно посмотреть в 
 * папке sql scripts-> Структура.sql)
 * 
 * В ResultSet-e ОБЯЗАТЕЛЬНО должна быть выбрана нужная строка (при помощи rs.next())
 * @author Ольга
 */
abstract class Abstract {
    
    private DataSource dataSource;

    public Abstract(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Получение соединения к БД.
     * @return соединение с БД.
     * @throws SQLException 
     */
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected BookAttr makeBookAttr(ResultSet rs) throws SQLException {
        BookAttr bookAttr = new BookAttr();
        bookAttr.setId(rs.getLong("attr_id"));
        bookAttr.setName(rs.getString("name"));
        return bookAttr;
    }
    
    protected BookParam makeBookParam(ResultSet rs, BookAttr attr) throws SQLException {
        BookParam bookParam = new BookParam();
        bookParam.setAttr(attr);
        bookParam.setBookId(rs.getLong("book_id"));
        bookParam.setValue(rs.getString("value"));
        return bookParam;
    }
    /**
     * Создание объекта Service на основе результата запроса.
     * @param rs результат запроса
     * @param typeService тип сервиса
     * @return сервис со всеми заполненными полями
     * @throws SQLException 
     */
    /*protected Service makeService(ResultSet rs, TypeService typeService) throws SQLException {
        Service service = new Service();
        service.setCost(rs.getDouble("cost"));
        service.setIdService(rs.getInt("ID_Service"));
        service.setNameService(rs.getString("name_service"));
        service.setOptional(rs.getBoolean("optional"));
        service.setTypeService(typeService);
        return service;
    }*/

    
}
