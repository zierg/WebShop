/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import org.apache.log4j.Logger;

/**
 * Класс, хранящий логгеры.
 * @author Ivan
 */
public class LogManager {
    private final static Logger shopLogger = Logger.getLogger("shopLogger");
    
    public final static Logger LOG = shopLogger;
    
    private LogManager() {}
}
