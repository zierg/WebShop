/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle.conditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Класс, используемый OracleUniversalDAO. Определяет, какой запрос к БД 
 * нужно выполнить.
 * @author Ivan
 */
public abstract class ConditionCreator {

    /**
     * Создаёт запрос, который будет использоваться в PreparedStatement.
     * Вместо параметров, значения которых нужно взять из переменных, ставится ?
     * @return запрос
     */
    public abstract String createSelect();
    
    /**
     * Заменяет все знаки вопроса на нужные значения.
     * @param ps PreparedStatement, созданный на основе вызова createSelect().
     * @throws SQLException 
     */
    public abstract void prepareSelectStatement(PreparedStatement ps) throws SQLException;
}
