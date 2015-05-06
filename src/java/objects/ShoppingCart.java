/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Иван
 */
public class ShoppingCart {
    
    private final List<Long> bookIds = new ArrayList<>();
    
    public ShoppingCart() {
    }
    
    public void addBookId(long bookId) {
        bookIds.add(bookId);
    }

    public void removeBookId(long bookId) {
        bookIds.remove(bookId);
    }

    public List<Long> getBookIds() {
        return bookIds;
    }
}
