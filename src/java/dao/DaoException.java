/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author Ivan
 */
public class DaoException extends RuntimeException {

    /**
     * Creates a new instance of
     * <code>DaoException</code> without detail message.
     */
    public DaoException() {
    }

    /**
     * Constructs an instance of
     * <code>DaoException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DaoException(String msg) {
        super(msg);
    }
    
    public DaoException(Throwable cause) {
        super(cause);
    }
    
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
