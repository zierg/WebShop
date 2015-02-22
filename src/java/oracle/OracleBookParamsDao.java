/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

import dao.BookParamsDao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import objects.BookAttr;
import objects.BookParam;
import oracle.conditions.ConditionCreator;

/**
 *
 * @author Иван
 */
public class OracleBookParamsDao extends OracleUniversalDAO<BookParam> implements BookParamsDao {
    
    private static final String TABLE_NAME = "book_params";
    private static final String SELECT_FOR_ALL = "SELECT bp.*, a.name FROM"
            + " book_params bp JOIN book_attrs a ON bp.attr_id = a.attr_id";
    
    
    private final BookParamConditionCreator bookParamConditionCreator;
    
    public OracleBookParamsDao(DataSource dataSource) {
        super(dataSource);
        bookParamConditionCreator = new BookParamConditionCreator();
    }

    @Override
    public boolean addParam(BookParam param) {
        return addObject(param);
    }

    @Override
    public boolean deleteParam(long bookId, long attrId) {
        final String DELETE = "DELETE from " + TABLE_NAME
                + " WHERE book_id = " + bookId + " and attr_id = " + attrId;
        return executeCustomStatement(DELETE);
    }

    @Override
    public boolean updateParam(BookParam param) {
        return updateObject(param);
    }

    @Override
    public List<BookParam> getParamsByBookId(long bookId) {
        return getAllObjectsByCustomQuery(SELECT_FOR_ALL + " WHERE book_id = " + bookId);
    }

    @Override
    public BookParam getParam(long bookId, long attrId) {
        return getUniqueObject(bookParamConditionCreator);
    }
    
    @Override
    protected String makeInsertStatement() {
        final String INSERT = "INSERT INTO " + TABLE_NAME
                + "(attr_id, book_id, value) VALUES (?, ?, ?)";
        return INSERT;
    }

    @Override
    protected String makeDeleteStatement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String makeUpdateStatement() {
        final String UPDATE = "UPDATE " + TABLE_NAME +
                " SET attr_id = ?, book_id = ?, value = ?";
        return UPDATE;
    }

    @Override
    protected void prepareInsertStatement(PreparedStatement ps, BookParam object) throws SQLException {
        ps.setLong(1, object.getAttrId());
        ps.setLong(2, object.getBookId());
        ps.setString(3, object.getValue());
    }

    @Override
    protected void prepareUpdateStatement(PreparedStatement ps, BookParam object) throws SQLException {
        ps.setLong(1, object.getAttrId());
        ps.setLong(2, object.getBookId());
        ps.setString(3, object.getValue());
    }

    @Override
    protected BookParam makeObject(ResultSet rs) throws SQLException {
        BookAttr attr = makeBookAttr(rs);
        return makeBookParam(rs, attr);
    }
    
    private static class BookParamConditionCreator extends ConditionCreator {

        private long bookId;
        private long attrId;
        
        public void setBookId(long bookId) {
            this.bookId = bookId;
        }
        
        public void setAttrId(long attrId) {
            this.attrId = attrId;
        }
        
        @Override
        public String createSelect() {
            final String SELECT = SELECT_FOR_ALL + " WHERE attr_id = ? and book_id = ?";
            return SELECT;
        }

        @Override
        public void prepareSelectStatement(PreparedStatement ps) throws SQLException {
            ps.setLong(1, attrId);
            ps.setLong(2, bookId);
        }
        
    }
}
