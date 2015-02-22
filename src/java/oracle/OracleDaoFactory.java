/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

import dao.*;
import java.util.Locale;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import static common.LogManager.LOG;

/**
 *
 * @author Иван
 */
public class OracleDaoFactory implements DaoFactory {
    
    private DataSource dataSource;
    
    private BookDao bookDao;
    private BookAttrsDao bookAttrsDao;
    private BookParamsDao bookParamsDao;
    
    public OracleDaoFactory() {
        Locale.setDefault(Locale.ENGLISH);
        try {
            InitialContext initContext;
            initContext = new InitialContext();
            dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/ShopDataSource");
            bookAttrsDao = new OracleBookAttrsDao(dataSource);
            bookParamsDao = new OracleBookParamsDao(dataSource);
        } catch (NamingException ex) {
            LOG.error("Ошибка чтения ресурса базы данных.", ex);
        }
    }

    @Override
    public BookDao getBookDao() {
        return bookDao;
    }

    @Override
    public BookAttrsDao getBookAttrsDao() {
        return bookAttrsDao;
    }

    @Override
    public BookParamsDao getBookParamsDao() {
        return bookParamsDao;
    }
    
}
