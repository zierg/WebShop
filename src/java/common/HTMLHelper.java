/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

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
    
    private HTMLHelper () {}
    
    public static String getMultilineStringForHTML(String source) {
        return source.replace("\n", "<br>");
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
}
