/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

import dao.BookAttrsDao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import objects.BookAttr;
import oracle.conditions.LongConditionCreator;

/**
 *
 * @author Иван
 */
class OracleBookAttrsDao extends OracleUniversalDAO<BookAttr> implements BookAttrsDao {

    private static final String TABLE_NAME = "book_attrs";
    private final LongConditionCreator attrIDConditionCreator;

    public OracleBookAttrsDao(DataSource dataSource) {
        super(dataSource);
        attrIDConditionCreator = new LongConditionCreator(TABLE_NAME, "attr_id");
    }

    @Override
    public boolean deleteDBObject(long objectId) {
        return deleteObjectByID(objectId);
    }

    @Override
    public List<BookAttr> getAllDBObjects() {
        return getAllObjects(TABLE_NAME);
    }

    @Override
    public BookAttr getDBObjectById(long objectId) {
        attrIDConditionCreator.setValue(objectId);
        return getUniqueObject(attrIDConditionCreator);
    }

    @Override
    public boolean addDBObject(BookAttr object) {
        return addObject(object);
    }

    @Override
    public boolean updateDBObject(BookAttr object) {
        return updateObject(object);
    }

    @Override
    protected String makeInsertStatement() {
        final String INSERT = "INSERT INTO " + TABLE_NAME + " (name) VALUES (?)";
        return INSERT;
    }

    @Override
    protected String makeDeleteStatement() {
        final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE attr_id = ?";
        return DELETE;
    }

    @Override
    protected String makeUpdateStatement() {
        final String UPDATE = "UPDATE " + TABLE_NAME + " SET name = ?";
        return UPDATE;
    }

    @Override
    protected void prepareInsertStatement(PreparedStatement ps, BookAttr object) throws SQLException {
        ps.setString(1, object.getName());
    }

    @Override
    protected void prepareUpdateStatement(PreparedStatement ps, BookAttr object) throws SQLException {
        ps.setString(1, object.getName());
    }

    @Override
    protected BookAttr makeObject(ResultSet rs) throws SQLException {
        return makeBookAttr(rs);
    }
}
