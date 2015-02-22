/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;


/**
 *
 * @author Иван
 */
public class BookParam {
    private BookAttr attr = new BookAttr();
    private long bookId;
    private String value = "";

    /**
     * @return the attr
     */
    public BookAttr getAttr() {
        return attr;
    }

    /**
     * @param attr the attr to set
     */
    public void setAttr(BookAttr attr) {
        this.attr = attr;
    }

    /**
     * @return the bookId
     */
    public long getBookId() {
        return bookId;
    }

    /**
     * @param bookId the bookId to set
     */
    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    public long getAttrId() {
        return attr.getId();
    }
    
    public void setAttrId(long attrId) {
        if (attr == null) {
            attr = new BookAttr();
        }
        attr.setId(attrId);
    }
}
