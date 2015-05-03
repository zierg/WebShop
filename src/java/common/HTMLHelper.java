/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.text.SimpleDateFormat;
import java.util.Date;
import objects.Author;
import objects.Book;

/**
 *
 * @author Иван
 */
public final class HTMLHelper {

    /**
     * Путь к таблице стилей (для подключения нужно указывать корневой каталог
     * сайта).
     */
    public static final String CSS = "/css/style.css";
    public static final String BOOK_IMAGES = "pics";

    private HTMLHelper() {
    }

    public static String getMultilineStringForHTML(String source) {
        return source.replace("\n", "<br />");
    }

    public static String includeCSS(String root) {
        return "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + root + CSS + "\" />";
    }

    /**
     * Метод можно использовать, если строка может быть null и требуется
     * выводить вместо null пустую строку ("")
     *
     * @param source исходная строка
     * @return исходную строку, если она не null, иначе ""
     */
    public static String fromNull(String source) {
        if (source == null) {
            return "";
        } else {
            return source;
        }
    }

    public static String getBookAuthorsLinks(Book book, String root, String linkClass) {
        StringBuilder b = new StringBuilder();
        int i = 1;
        int max = book.getAuthors().size();
        for (Author author : book.getAuthors()) {
            String hrefString = author.getSurname() + " "
                    + author.getName().charAt(0) + "."
                    + (!author.getMiddlename().isEmpty() ? (author.getMiddlename().charAt(0) + ".") : "");
            b.append("<a class=\"").append(linkClass).append("\" href=\"")
                    .append(root).append("/author?author_id=")
                    .append(author.getAuthorId()) 
                    .append("\">").append(hrefString).append("</a>")
                    .append((i < max ? " " : ""));
            i++;
        }
        System.out.println(b);
        return b.toString();
    }
    
    public static String getInCartButtonCode(Book book) {
        return "В корзину";
    }
    
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
    public static String makeYear(Date date) {
        return dateFormat.format(date);
    }
}
