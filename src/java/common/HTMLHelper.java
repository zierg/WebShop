/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import objects.Author;
import objects.Book;
import objects.ShoppingCart;

/**
 *
 * @author Иван
 */
public final class HTMLHelper {

    /**
     * Класс для метода makeFormWithFields. Хранит информацию о текстовых полях.
     */
    public static class FormTextField implements Comparable<FormTextField> {

        private final int order;
        private String fieldTitle = "";
        private String fieldName = "";
        private String fieldValue = "";
        private String fieldClass = "other";
        private String fieldType = "text";
        private boolean autofocus = false;

        public FormTextField(int order) {
            this.order = order;
        }

        @Override
        public int compareTo(FormTextField o) {
            return Integer.compare(order, o.order);
        }

        public String getName() {
            return fieldName;
        }

        public FormTextField setName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public String getValue() {
            return fieldValue;
        }

        public FormTextField setValue(String fieldValue) {
            this.fieldValue = fieldValue;
            return this;
        }

        public String getFieldClass() {
            return fieldClass;
        }

        public FormTextField setFieldClass(String fieldClass) {
            this.fieldClass = fieldClass;
            return this;
        }

        public boolean isAutofocus() {
            return autofocus;
        }

        public FormTextField setAutofocus(boolean autofocus) {
            this.autofocus = autofocus;
            return this;
        }

        public String getTitle() {
            return fieldTitle;
        }

        public FormTextField setTitle(String fieldTitle) {
            this.fieldTitle = fieldTitle;
            return this;
        }

        public int getOrder() {
            return order;
        }

        public String getType() {
            return fieldType;
        }

        public FormTextField setType(String fieldType) {
            this.fieldType = fieldType;
            return this;
        }
    }

    /**
     * Путь к таблице стилей (для подключения нужно указывать корневой каталог
     * сайта).
     */
    public static final String CSS = "/css/style.css";
    public static final String BOOK_IMAGES = "pics";

    private HTMLHelper() {
    }

    /**
     * Подготавливает мультистрочную строку к выводу на HTML странице
     * (заменяет абзацы на тег <br>)
     * @param source исходная строка
     * @return 
     */
    public static String getMultilineStringForHTML(String source) {
        return source.replace("\n", "<br />");
    }

    /**
     * Подключает CSS
     * @param root корень веб-приложения
     * @return 
     */
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

    /**
     * Код для создания гиперссылок на авторов книги
     * @param book книга
     * @param root корень веб-приложения
     * @param linkClass css-класс ссылки
     * @return 
     */
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
        return b.toString();
    }

    /**
     * Код для создания кнопки корзины.
     * Если книга куплена, создаёт код ссылки на скачивание.
     * Если книга не куплена и не в корзине, создаёт код добавления в корзину.
     * Если книга не куплена и в корзине, создаёт код удаления из корзины.
     * После добавления и удаления происходит перенаправление на страницу,
     * на которой была нажата кнопка (передаётся в параметре fromURL)
     * @param book книга
     * @param fromURL ссылка, куда перенаправлять после добавления/удаления
     * @param request HttpServletRequest (берётся request с jsp-страницы)
     * @param purchased если книга куплена, то true, иначе false
     * @return 
     */
    public static String getInCartButtonCode(Book book, String fromURL, HttpServletRequest request, boolean purchased) {
        ShoppingCart cart = (ShoppingCart) request.getSession(false).getAttribute("shopping_cart");
        String root = request.getContextPath();
        if (purchased) {
            return "<a href=\"" + root + "/download/" + book.getLink() + "\">Скачать</a>";
        } else if (cart.getBookIds().contains(book.getBookId())) {
            return "<form name=\"inCart" + book.getBookId() + "\" action = \""
                    + root + "/removeFromCart\" method=\"POST\">"
                    + "<input type=\"hidden\" name=\"book_id\" value=\"" + book.getBookId() + "\" />"
                    + "<input type=\"hidden\" name=\"from\" value=\"" + fromURL + "\" />"
                    + "<input type=\"submit\" value=\"Убрать из корзины\" /></form>";
        } else {
            return "<form name=\"inCart" + book.getBookId() + "\" action = \""
                    + root + "/addToCart\" method=\"POST\">"
                    + "<input type=\"hidden\" name=\"book_id\" value=\"" + book.getBookId() + "\" />"
                    + "<input type=\"hidden\" name=\"from\" value=\"" + fromURL + "\" />"
                    + "<input type=\"submit\" value=\"В корзину\" /></form>";
        }
    }

    /**
     * Код для создания формы вида:
     * текст1: поле1
     * ...
     * текстN: полеN
     * кнопка submit
     * @param fields поля (будут идти по позрастанию значения order)
     * @param formName имя формы
     * @param action action формы
     * @param buttonText текст кнопки
     * @param formMethod метод формы (GET или POST)
     * @return 
     */
    public static String makeFormWithFields(FormTextField[] fields, String formName, String action, String buttonText, String formMethod) {
        StringBuilder b = new StringBuilder();
        b.append("<form name=\"").append(formName).
                append("\" action=\"").append(action)
                .append("\" method=\"").append(formMethod).append("\">")
                .append("<table class=\"fields\" style=\"text-align: left;\"");
        Arrays.sort(fields);
        for (FormTextField field : fields) {
            b.append("<tr><td>").append(field.getTitle()).append("</td><td>")
                    .append("<input type=\"").append(field.getType())
                    .append("\" class=\"").append(field.getFieldClass())
                    .append("\" name=\"").append(field.getName())
                    .append("\" value=\"").append(field.getValue())
                    .append("\"").append((field.isAutofocus() ? " autofocus" : ""))
                    .append(" /></td></tr>");
        }
        b.append("<tr><td colspan=\"2\" style=\"text-align: center;\">")
                .append("<input type=\"submit\" value=\"").append(buttonText).append("\" />")
                .append("</td></tr></table></form>");
        return b.toString();
    }

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
    /**
     * Код для получения года из даты
     * @param date
     * @return 
     */
    public static String makeYear(Date date) {
        return dateFormat.format(date);
    }

    /**
     * Код для преобразования текста в формат, который можно положить в GET-запрос.
     * Например, исходный текст: http://localhost:8080/WebShop/books?first=1&search_text=%D0%BF%D1%83%D1%88
     * Из него получится: http%3A%2F%2Flocalhost%3A8080%2FWebShop%2Fbooks%3Ffirst%3D1%26search_text%3D%25D0%25BF%25D1%2583%25D1%2588
     * Метод можно использвать, например, когда нужно составить ссылку вида:
     * .....page.jsp?parameter1=value1&from=url
     * подставляем вместо URL результат метода, в итоге вся ссылка корректно передастся
     * @param path
     * @return 
     */
    public static String getURLAsGetParameer(String path) {
        try {
            return java.net.URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return path;
        }
    }

    /**
     * Код для таблицы книг
     * @param books Список книг
     * @param purchased Список ИДшников купленных книг
     * @param request HttpServletRequest (берётся request с jsp-страницы)
     * @param fromURL ссылка для создания кнопки корзины. Зачем нужна см. 
     * в методе getInCartButtonCode
     * @return 
     */
    public static String makeBookTable(List<Book> books, List<Long> purchased, HttpServletRequest request, String fromURL) {
        String root = request.getContextPath();
        StringBuilder b = new StringBuilder()
                .append("<table border=1 class=\"content_table\">") // Заголовки столбцов
                .append("<tr>")
                .append("<th class=\"book_title\" width=\"63%\">Название</th>")
                .append("<th class=\"category\" width=\"23%\">Категория</th>")
                .append("<th class=\"cost\" width=\"7%\">Стоимость</th>")
                .append("<th class=\"action\" width=\"7%\">Действия</th>")
                .append("</tr>");

        int bookNumber = 1;
        for (Book book : books) {
            b.append("<tr><td class=\"book_title\"><a name=\"").append(bookNumber).append("\" />")  // Название и ссылка на книгу
                    .append("<a class=\"main_link\" href=\"").append(root)
                    .append("/book?book_id=").append(book.getBookId())
                    .append("\">").append(book.getTitle()).append("</a><br>")
                    .append(getBookAuthorsLinks(book, root, "help_link")).append("</td>")           // Ссылки на авторов
                    .append("<td class=\"centered\"><a class=\"other\" href=\"")
                    .append(root).append("/category?category_id=")                                  // Ссылка на категорию
                    .append(book.getCategory().getCategoryId())
                    .append("\">").append(book.getCategory().getTitle()).append("</a></td>")
                    .append("<td class=\"centered\">").append(book.getCost()).append("р.</td>")     // Цена
                    .append("<td class=\"centered\">")
                    .append(getInCartButtonCode(book, fromURL + "#" + bookNumber,                   // Кнопка с корзиной
                                    request, 
                                    (purchased != null && purchased.contains(book.getBookId()))))
                    .append("</td></tr>");

            bookNumber++;
        }
        b.append("</table>");
        return b.toString();
    }
}
