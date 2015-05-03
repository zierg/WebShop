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
    private String imageLink = "";
    private String title = "";
    private String link = "";
    private double cost;
    private Date releaseDate;
    private Category category;
    private boolean isShown;
    private List<Author> authors = new ArrayList<>();
    private List<BookParam> parameters = new ArrayList<>();

    public List<Author> getAuthors() {
        return authors;
    }
    
    public List<BookParam> getParameters() {
        return parameters;
    }
    
    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        if (imageLink == null) {
            this.imageLink = "";
        } else {
            this.imageLink = imageLink;
        }
    }

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
    public double getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(double cost) {
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
}
