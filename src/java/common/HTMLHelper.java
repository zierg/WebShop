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
    
    private HTMLHelper () {}
    
    public static String getMultilineStringForHTML(String source) {
        return source.replace("\n", "<br>");
    }
}
