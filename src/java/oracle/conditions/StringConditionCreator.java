/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle.conditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Класс для создания запросов с одним условием по столбцу типа String.
 * @author Ivan
 */
public class StringConditionCreator extends AbstractOneConditionCreator<String> {
   
    /**
     * Конструктор. Создаёт запрос автоматически на основе параметров
     * @param tableName имя таблицы, к которой будем делать запрос
     * @param columnName имя столбца, к которому будем применять условие.
     */
    public StringConditionCreator(String tableName, String columnName) {
        super(tableName, columnName);
    }
    
    /**
     * Конструктор. Запрос определяет пользователь класса.
     * @param query 
     */
    public StringConditionCreator(String query) {
        super(query);
    }

    @Override
    public void prepareSelectStatement(PreparedStatement ps) throws SQLException {
        ps.setString(1, value);
    }
}
