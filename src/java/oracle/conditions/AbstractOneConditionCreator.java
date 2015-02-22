/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle.conditions;

/**
 * Класс для облегчения создания ConditionCreator-ов с единственным условием.
 * @author Ivan
 */
public abstract class AbstractOneConditionCreator<T> extends ConditionCreator {
    
    protected T value;
    
    private final String query;
    
    /**
     * Конструктор. Создаёт запрос автоматически на основе параметров
     * @param tableName имя таблицы, к которой будем делать запрос
     * @param columnName имя столбца, к которому будем применять условие.
     */
    public AbstractOneConditionCreator(String tableName, String columnName) {
        query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
    }
    
    /**
     * Конструктор. Запрос определяет пользователь класса.
     * @param query 
     */
    public AbstractOneConditionCreator(String query) {
        this.query = query;
    }

    @Override
    public String createSelect() {
        return query;
    }

    /**
     * Устанвливает значения, которое будет подставлено в условие
     * @param value значение
     */
    public void setValue(T value) {
        this.value = value;
    }
}
