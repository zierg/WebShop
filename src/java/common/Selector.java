/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import static common.LogManager.LOG;
import dao.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import objects.Author;
import objects.Book;
import objects.Category;
import static common.Constants.*;

/**
 *
 * @author Иван
 */
public class Selector {
    
    private DataSource dataSource;
    
    private static final String SELECT_AUTHORS_FOR_BOOK = "select\n" +
                                        "  a.author_id\n" +
                                        "  , a.name\n" +
                                        "  , a.surname\n" +
                                        "  , a.middlename\n" +
                                        "from\n" +
                                        "  authors a\n" +
                                        "  , books_authors ba\n" +
                                        "where\n" +
                                        "  a.author_id = ba.author_id\n" +
                                        "  and ba.book_id = ?";
    
    private static final String BOOK_SELECT = "select \n" +
                                    "    b.book_id\n"
                                  + "  , b.title\n"
                                  + "  , b.cost\n"
                                  + "  , b.category_id\n" +
                                    "    , cat.title cat_name\n" +
                                    "  from\n" +
                                    "    books b\n" +
                                    "    , categories cat\n" +
                                    "  where\n" +
                                    "    b.category_id = cat.category_id\n" +
                                    "    and b.is_shown = 1\n";
    
    private static final String ALL_BOOKS_SELECT = "select t.*"
                                + ", rownum pos\n "
                                + " from"
                                + "("
                                + BOOK_SELECT
                                    + " order by b.title) t" +
                                    "  ";
    
    private static final String BOOK_SEARCH_SELECT = "select t.*"
                                + ", rownum pos\n "
                                + " from"
                                + "("
                                + BOOK_SELECT
                                    + " and b.book_id in"
                                    + "(select book_id from search_results where search_text = ?)"
                                    + "order by b.title) t" +
                                    "  ";
    
    private static final String SEARCH_INSERT_STATEMENT =
"insert into search_results (search_text, book_id)\n" +
"select \n" +
"    :1 search_text\n" +
"    , t.book_id\n" +
"from\n" +
"    (select \n" +
"        b.book_id\n" +
"        , b.title\n" +
"        , b.cost\n" +
"        , b.category_id\n" +
"        , cat.title cat_name\n" +
"    from\n" +
"        books b\n" +
"        , categories cat\n" +
"    where\n" +
"        b.category_id = cat.category_id\n" +
"        and b.is_shown = 1\n" +
"        and \n" +
"        (\n" +
"            like_expression[b.title][and]\n" +
"            or like_expression[cat.title][and]\n" +
"            or exists (\n" +
"                select\n" +
"                    0\n" +
"                from\n" +
"                    authors a\n" +
"                    , books_authors ba\n" +
"                where\n" +
"                    ba.book_id = b.book_id\n" +
"                    and a.author_id = ba.author_id\n" +
"                    and (\n" +
"                        like_expression[a.name][or]\n" +
"                        or like_expression[a.middlename][or]\n" +
"                        or like_expression[a.surname][or]\n" +
"                    )\n" +
"            )\n" +
"        )\n" +
"        \n" +
"    order by b.title) t";
    
    public Selector() {
        Locale.setDefault(Locale.ENGLISH);
        try {
            InitialContext initContext;
            initContext = new InitialContext();
            dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/ShopDataSource");

        } catch (NamingException ex) {
            LOG.error("Ошибка чтения ресурса базы данных.", ex);
        }
    }
    
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    /**
     * Поиск книг. Ищет по автору, категории, названию одновременно.
     * Ищет введённые слова отдельно. Например "кап доч" - найдёт Капитанская дочка.
     * "ильф петров" - найдёт все книги обоих авторов.
     * Поиск работает следующим образом:
     * в таблице search_results хранятся результаты выполнения поисковых запросов.
     * Сначала проверяется, есть ли в этой таблице поиск по такому же запросу
     * (при этом поиск должен был выполняться недавно, насколько недавно - отвечает
     * константа Constants.MAX_SEARCH_RESULT_AGE_MINUTES).
     * Если есть, то возвращаются все книги, сохранённые в результате этого поиска.
     * Если же нет, то сначала удаляются все старые результаты этого запроса,
     * затем осуществляется поиск и вставляютс новые результаты,
     * которые и возвращаются.
     * @param searchText
     * @param first
     * @param last
     * @return 
     */
    public List<Book> findBooks(String searchText, long first, long last) {
        List<Book> books = new ArrayList<>();
        try (Connection con = getConnection()) {
            PreparedStatement checkCachedSearch = con.prepareStatement(
"select\n" +
"     count(0) amount\n" +
"from (\n" +
"    select extract(minute from(current_timestamp - search_timestamp)) search_age \n" +
"from\n" +
"    search_results\n" +
"where \n" +
"    search_text = ? \n" +
"    and rownum = 1)\n" +
"where\n" +
"    search_age < ?");
            checkCachedSearch.setString(1, searchText);
            checkCachedSearch.setInt(2, MAX_SEARCH_RESULT_AGE_MINUTES);
            ResultSet rsCheck = checkCachedSearch.executeQuery();
            rsCheck.next();
            boolean searchExists = rsCheck.getBoolean("amount");
            if (!searchExists) {
                PreparedStatement deleteOldSameSearchesPS = con.prepareStatement(
                        "delete from search_results where search_text = ?");
                deleteOldSameSearchesPS.setString(1, searchText);
                deleteOldSameSearchesPS.execute();
                PreparedStatement insertPS = con.prepareStatement(prepareSelect(SEARCH_INSERT_STATEMENT, searchText));
                insertPS.setString(1, searchText);
                insertPS.execute();
            }
            
            PreparedStatement searchPS = con.prepareStatement("select * from (" +BOOK_SEARCH_SELECT + ") where pos between ? and ?");
            searchPS.setString(1, searchText);
            searchPS.setLong(2, first);
            searchPS.setLong(3, last);
            ResultSet searchRS = searchPS.executeQuery();
            while(searchRS.next()) {
                Category category = makeCategoryForBook(searchRS);
                Book book = makeBookForList(searchRS, category);
                fillBookAuthors(con, book);
                books.add(book);
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return books;
    }
    
    /**
     * Получить диапазон книг (из отсортированного списка)
     * @param first номер первой книги
     * @param last номер второй книги
     * @return 
     */
    public List<Book> getBooks(long first, long last) {
        List<Book> books = new ArrayList<>();
        
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from (" +ALL_BOOKS_SELECT + ") where pos between ? and ?");
            ps.setLong(1, first);
            ps.setLong(2, last);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Category category = makeCategoryForBook(rs);
                Book book = makeBookForList(rs, category);
                fillBookAuthors(con, book);
                books.add(book);
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return books;
    }
    
    private Category makeCategoryForBook(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getLong("category_id"));
        category.setTitle(rs.getString("cat_name"));
        return category;
    }
    
    private Book makeBookForList(ResultSet rs, Category category) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getLong("book_id"));
        book.setCategory(category);
        book.setCost(rs.getDouble("cost"));
        book.setTitle(rs.getString("title"));
        return book;
    }
    
    /**
     * Заполнить список авторов книги
     * @param con соединение с БД
     * @param book книга
     * @throws SQLException 
     */
    private void fillBookAuthors(Connection con, Book book) throws SQLException {
        PreparedStatement ps = con.prepareStatement(SELECT_AUTHORS_FOR_BOOK);
        ps.setLong(1, book.getBookId());
        List<Author> authors = book.getAuthors();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Author author = new Author();
            author.setAuthorId(rs.getLong("author_id"));
            author.setName(rs.getString("name"));
            author.setSurname(rs.getString("surname"));
            author.setMiddlename(rs.getString("middlename"));
            authors.add(author);
        }
    }
    
    /**
     * Посчитать количество всех книг
     * @return 
     */
    public long getBooksCount() {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select count(0) total_amount from (" + ALL_BOOKS_SELECT + ")");
            ResultSet rs = ps.executeQuery();
            rs.next();
            long count = rs.getLong("total_amount");
            return count;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
    /**
     * Посчитать количество всех книг, найденных по запросу
     * (запрос перед этим должен быть выполнен, т.к. данные считываются из таблицы search_results)
     * @param searchText поисковой запрос
     * @return 
     */
    public long getBooksCount(String searchText) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select count(0) total_amount from (" + BOOK_SEARCH_SELECT + ")");
            ps.setString(1, searchText);
            ResultSet rs = ps.executeQuery();
            rs.next();
            long count = rs.getLong("total_amount");
            return count;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
    
    
    /**
     * Создаёт условия для регистронезависимого поиска значения в колонке
     * на основании введённой строки поиска. Пример:
     * Введено "Пушкин: Руслан и Людмила", колонка author.surname, минимальная длина слова: 3, delim = or.
     * Получится следующее условие (со скобками):
     * ('and' = 'or'
     * or lower(author.surname) like '%' || lower('Пушкин') || '%'
     * or lower(author.surname) like '%' || lower('Руслан') || '%'
     * or lower(author.surname) like '%' || lower('Людмила') || '%') 
     * @param column имя колонки (вместе с псевдонимом таблицы)
     * @param preparedSearchText текст для поиска.
     * @param minLength минимальная длина слова
     * @param delim разделитель частей условия. Может содержать ТОЛЬКО одно из следующих значений: or, and.
     * @return 
     */
    private String getSearchCondition(String column, String preparedSearchText, int minLength, String delim) {
        StringTokenizer token = new StringTokenizer(preparedSearchText, " ");
        StringBuilder condition = new StringBuilder(" ( 'and' = '" + delim + "'");
        while(token.hasMoreTokens()) {
            String next = token.nextToken();
            if (next.length() < minLength) {
                continue;
            }
            condition.append("\n").append(delim).append(" lower(").
                    append(column).append(") like '%' || lower('").
                    append(next).append("') || '%'");
        }
        condition.append(") ");
        return condition.toString();
    }
    
    /**
     * Заменяет в запросе выражения вида like_expression[column][or]
     * на результат метода getSearchCondition для column и or.
     * @param select запрос (select)
     * @param searchText поисковой запрос
     * @return 
     */
    private String prepareSelect(String select, String searchText) {
        String ret = select.replaceAll("like_expression\\[(.*)\\]\\[(.*)\\]",
                getSearchCondition("$1", searchText, MIN_SYMBOLS_IN_SEARCH, "$2"));
        return ret;
    }
}
