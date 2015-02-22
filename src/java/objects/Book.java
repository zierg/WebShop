/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Иван
 */
public class Book {
    private long bookId;
    private String description = "";
    private String title = "";
    private String link = "";
    private float cost;
    private Date releaseDate;
    private Category category;
    private boolean isShown;
    private final List<BookParam> parameters = new ArrayList<>();

    /**
     * @return the book_id
     */
    public long getBookId() {
        return bookId;
    }

    /**
     * @param bookId the book_id to set
     */
    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the cost
     */
    public float getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(float cost) {
        this.cost = cost;
    }

    /**
     * @return the releaseDate
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * @param releaseDate the releaseDate to set
     */
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * @return the isShown
     */
    public boolean isIsShown() {
        return isShown;
    }

    /**
     * @param isShown the isShown to set
     */
    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }

    /**
     * @return the parameters
     */
    public List<BookParam> getParameters() {
        return parameters;
    }
    
    public void addParameter(BookParam parameter) {
        parameters.add(parameter);
    }
}
