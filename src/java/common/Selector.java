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
import objects.BookAttr;
import objects.BookParam;
import objects.ShoppingCart;

/**
 * Выполняет операции в БД, связанные с контентом магазина.
 * @author Иван
 */
public class Selector {

    private DataSource dataSource;
    private static final String SELECT_AUTHORS_FOR_BOOK =
            "select\n"
            + "  a.author_id\n"
            + "  , a.name\n"
            + "  , a.surname\n"
            + "  , a.middlename\n"
            + "from\n"
            + "  authors a\n"
            + "  , books_authors ba\n"
            + "where\n"
            + "  a.author_id = ba.author_id\n"
            + "  and ba.book_id = ?";
    private static final String SELECT_PARAMETERS_FOR_BOOK =
            "select\n"
            + "    a.name attr_name\n"
            + "    , p.value\n"
            + "    , p.attr_id\n"
            + "from\n"
            + "    book_attrs a\n"
            + "    , book_params p\n"
            + "where\n"
            + "    p.book_id = ?\n"
            + "    and p.attr_id = a.attr_id";
    private static final String BOOK_SELECT_FOR_LIST =
            "select \n"
            + "    b.book_id\n"
            + "  , b.title\n"
            + "  , b.cost\n"
            + "  , b.category_id\n"
            + "  , b.link"
            + "    , cat.title cat_name\n"
            + "  from\n"
            + "    books b\n"
            + "    , categories cat\n"
            + "  where\n"
            + "    b.category_id = cat.category_id\n"
            + "    and b.is_shown = 1\n";
    private static final String ONE_BOOK_SELECT =
            "select \n"
            + "    b.book_id\n"
            + "    , b.title\n"
            + "    , b.cost\n"
            + "    , b.category_id\n"
            + "    , cat.title cat_name\n"
            + "    , b.description\n"
            + "    , b.release_date\n"
            + "    , b.link\n"
            + "    , b.is_shown\n"
            + "    , b.image_link\n"
            + "from\n"
            + "    books b\n"
            + "    , categories cat\n"
            + "where\n"
            + "    b.book_id = ?\n"
            + "    and b.category_id = cat.category_id";
    private static final String ALL_BOOKS_SELECT = "select t.*"
            + ", rownum pos\n "
            + " from"
            + "("
            + BOOK_SELECT_FOR_LIST
            + " order by b.title) t"
            + "  ";
    private static final String BOOK_SEARCH_SELECT = "select t.*"
            + ", rownum pos\n "
            + " from"
            + "("
            + BOOK_SELECT_FOR_LIST
            + " and b.book_id in"
            + "(select book_id from search_results where search_text = ?)"
            + "order by b.title) t"
            + "  ";
    private static final String SEARCH_INSERT_STATEMENT =
            "insert into search_results (search_text, book_id)\n"
            + "select \n"
            + "    :1 search_text\n"
            + "    , t.book_id\n"
            + "from\n"
            + "    (select \n"
            + "        b.book_id\n"
            + "        , b.title\n"
            + "        , b.cost\n"
            + "        , b.category_id\n"
            + "        , cat.title cat_name\n"
            + "    from\n"
            + "        books b\n"
            + "        , categories cat\n"
            + "    where\n"
            + "        b.category_id = cat.category_id\n"
            + "        and b.is_shown = 1\n"
            + "        and \n"
            + "        (\n"
            + "            like_expression[b.title][and]\n"
            + "            or like_expression[cat.title][and]\n"
            + "            or exists (\n"
            + "                select\n"
            + "                    0\n"
            + "                from\n"
            + "                    authors a\n"
            + "                    , books_authors ba\n"
            + "                where\n"
            + "                    ba.book_id = b.book_id\n"
            + "                    and a.author_id = ba.author_id\n"
            + "                    and (\n"
            + "                        like_expression[a.name][or]\n"
            + "                        or like_expression[a.middlename][or]\n"
            + "                        or like_expression[a.surname][or]\n"
            + "                    )\n"
            + "            )\n"
            + "            or (\n"
            + "                :2 = 1\n"
            + "                and like_expression[b.description][and]\n"
            + "            )\n"
            + "        ) \n"
            + "    order by b.title) t";
    private static final String SELECT_BOOKS_BY_AUTHOR = BOOK_SELECT_FOR_LIST
            + " and exists(select 0 from books_authors where book_id = b.book_id and author_id = ?)";
    private static final String SELECT_BOOKS_BY_CATEGORY = BOOK_SELECT_FOR_LIST
            + " and b.category_id = ?";

    private final UserSelector userSelector;
    
    private static Selector instance;
    
    public static Selector getInstance() {
        if (instance == null) {
            instance = new Selector();
        }
        return instance;
    }
    
    private Selector() {
        Locale.setDefault(Locale.ENGLISH);
        try {
            InitialContext initContext;
            initContext = new InitialContext();
            dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/ShopDataSource");
            userSelector = new UserSelector(dataSource);
        } catch (NamingException ex) {
            LOG.error("Ошибка чтения ресурса базы данных.", ex);
            throw new DaoException(ex);
        }
    }
    
    public UserSelector getUserSelector() {
        return userSelector;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Поиск книг. Ищет по автору, категории, названию одновременно. Ищет
     * введённые слова отдельно. Например "кап доч" - найдёт Капитанская дочка.
     * "ильф петров" - найдёт все книги обоих авторов. Поиск работает следующим
     * образом: в таблице search_results хранятся результаты выполнения
     * поисковых запросов. Сначала проверяется, есть ли в этой таблице поиск по
     * такому же запросу (при этом поиск должен был выполняться недавно,
     * насколько недавно - отвечает константа
     * Constants.MAX_SEARCH_RESULT_AGE_MINUTES). Если есть, то возвращаются все
     * книги, сохранённые в результате этого поиска. Если же нет, то сначала
     * удаляются все старые результаты этого запроса, затем осуществляется поиск
     * и вставляютс новые результаты, которые и возвращаются.
     *
     * @param searchText
     * @param first
     * @param last
     * @return
     */
    public List<Book> findBooks(String searchText, long first, long last, String searchParamerers) {
        List<Book> books = new ArrayList<>();
        String inTableText = searchParamerers + searchText;
        try (Connection con = getConnection()) {
            PreparedStatement checkCachedSearch = con.prepareStatement(
                    "select\n"
                    + "     count(0) amount\n"
                    + "from (\n"
                    + "    select extract(minute from(current_timestamp - search_timestamp)) search_age \n"
                    + "from\n"
                    + "    search_results\n"
                    + "where \n"
                    + "    search_text = ? \n"
                    + "    and rownum = 1)\n"
                    + "where\n"
                    + "    search_age < ?");
            checkCachedSearch.setString(1, inTableText);
            checkCachedSearch.setInt(2, MAX_SEARCH_RESULT_AGE_MINUTES);
            ResultSet rsCheck = checkCachedSearch.executeQuery();
            rsCheck.next();
            boolean searchExists = rsCheck.getBoolean("amount");
            if (!searchExists) {
                PreparedStatement deleteOldSameSearchesPS = con.prepareStatement(
                        "delete from search_results where search_text = ?");
                deleteOldSameSearchesPS.setString(1, inTableText);
                deleteOldSameSearchesPS.execute();
                int inDescription = searchParamerers.contains("[in_description]") ? 1 : 0;
                PreparedStatement insertPS = con.prepareStatement(prepareSelect(SEARCH_INSERT_STATEMENT, searchText));
                insertPS.setString(1, inTableText);
                insertPS.setInt(2, inDescription);
                insertPS.execute();
            }

            PreparedStatement searchPS = con.prepareStatement("select * from (" + BOOK_SEARCH_SELECT + ") where pos between ? and ?");
            searchPS.setString(1, inTableText);
            searchPS.setLong(2, first);
            searchPS.setLong(3, last);
            ResultSet searchRS = searchPS.executeQuery();
            while (searchRS.next()) {
                Category category = makeCategoryForBook(searchRS);
                Book book = makeBook(searchRS, category, false);
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
     *
     * @param first номер первой книги
     * @param last номер второй книги
     * @return
     */
    public List<Book> getBooks(long first, long last) {
        List<Book> books = new ArrayList<>();

        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from (" + ALL_BOOKS_SELECT + ") where pos between ? and ?");
            ps.setLong(1, first);
            ps.setLong(2, last);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category category = makeCategoryForBook(rs);
                Book book = makeBook(rs, category, false);
                fillBookAuthors(con, book);
                books.add(book);
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return books;
    }

    /**
     * Получить диапазон книг одного автора из отсортированного списка
     * @param first номер первой книги
     * @param last номер последней книги
     * @param authorId ИД автора
     * @return 
     */
    public List<Book> getBooksByAuthorId(long first, long last, long authorId) {
        List<Book> books = new ArrayList<>();
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from ("
                    + "select t.*, rownum pos from ("
                    + SELECT_BOOKS_BY_AUTHOR + ") t order by t.title) where pos between ? and ?");
            ps.setLong(1, authorId);
            ps.setLong(2, first);
            ps.setLong(3, last);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category category = makeCategoryForBook(rs);
                Book book = makeBook(rs, category, false);
                fillBookAuthors(con, book);
                books.add(book);
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return books;
    }

    /**
     * Получить диапазон книг одной категории из отсортированного списка
     * @param first номер первой книги
     * @param last номер последней книги
     * @param categoryId ИД категории
     * @return 
     */
    public List<Book> getBooksByCategoryId(long first, long last, long categoryId) {
        List<Book> books = new ArrayList<>();
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from ("
                    + "select t.*, rownum pos from ("
                    + SELECT_BOOKS_BY_CATEGORY + ") t order by t.title) where pos between ? and ?");
            ps.setLong(1, categoryId);
            ps.setLong(2, first);
            ps.setLong(3, last);
            ResultSet rs = ps.executeQuery();
            Category category = null;
            while (rs.next()) {
                if (category == null) {
                    category = makeCategoryForBook(rs);
                }
                Book book = makeBook(rs, category, false);
                fillBookAuthors(con, book);
                books.add(book);
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return books;
    }

    /**
     * Получить книгу по её ИД (все столбцы из таблицы).
     * @param bookId ИД книги
     * @return 
     */
    public Book getBook(long bookId) {
        Book book = null;
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(ONE_BOOK_SELECT);
            ps.setLong(1, bookId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            Category category = makeCategoryForBook(rs);
            book = makeBook(rs, category, true);
            fillBookAuthors(con, book);
            fillBookParameters(con, book);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return book;
    }

    /**
     * Получить категорию по её ИД (все столбцы из таблицы).
     * @param categoryId ИД категории
     * @return 
     */
    public Category getCategory(long categoryId) {
        Category category = new Category();
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from categories where category_id = ?");
            ps.setLong(1, categoryId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            category.setCategoryId(categoryId);
            category.setDescription(DatabaseHelper.toStringFromClob(rs.getClob("description")));
            category.setTitle(rs.getString("title"));
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return category;
    }

    /**
     * Получить автора по его ИД (все столбцы таблицы).
     * @param authorId ИД автора
     * @return 
     */
    public Author getAuthor(long authorId) {
        Author author = null;
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from authors where author_id = ?");
            ps.setLong(1, authorId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            author = makeAuthor(rs, true);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return author;
    }

    /**
     * Посчитать количество всех книг
     *
     * @return
     */
    public long getAllBooksCount() {
        return getBooksCount(ALL_BOOKS_SELECT);
    }

    /**
     * Посчитать количество всех книг, найденных по запросу (запрос перед этим
     * должен быть выполнен, т.к. данные считываются из таблицы search_results)
     *
     * @param searchText поисковой запрос
     * @param searchParameters параметры поиска (например "[in_description]").
     * Поскольку с поиском в описаниях и без него результаты поиска отличаются,
     * то сохранять нужно под разными ключами. Параметры поиска приписываются 
     * в начало поискового запроса. Например, если был запрос "Капитанская,дочка"
     * и стоял флаг "искать в описаниях", то в БД запишется следующее значение:
     * "[in_description]капитанская дочка".
     * @return количество книг
     */
    public long getSearchBooksCount(String searchText, String searchParameters) {
        return getBooksCount(BOOK_SEARCH_SELECT, searchParameters + searchText);
    }

    /**
     * Количество книг автора
     * @param authorId ИД автора
     * @return 
     */
    public long getAuthorsBooksCount(long authorId) {
        return getBooksCount(SELECT_BOOKS_BY_AUTHOR, Long.toString(authorId));
    }

    /**
     * Количество книг категории
     * @param categoryId ИД категории
     * @return 
     */
    public long getCategoryBooksCount(long categoryId) {
        return getBooksCount(SELECT_BOOKS_BY_CATEGORY, Long.toString(categoryId));
    }

    /**
     * Количество книг (хотя в принципе подходит для любых записей, тюкю
     * в query может быть всё, что угодно), возвращаемых в результате запроса.
     * @param query запрос
     * @param parameters значения параметров запроса (должны идти в нужном порядке)
     * @return 
     */
    public long getBooksCount(String query, String... parameters) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select count(0) total_amount from (" + query + ")");
            for (int i = 1; i <= parameters.length; i++) {
                ps.setString(i, parameters[i - 1]);
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            long count = rs.getLong("total_amount");
            return count;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * Создаёт объект категории, заполненный только необходимыми для отображения
     * в гиперссылке данными (название и ИД)
     * @param rs результат запроса, в котором должны быть столбцы cat_name
     * и category_id.
     * @return
     * @throws SQLException 
     */
    private Category makeCategoryForBook(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getLong("category_id"));
        category.setTitle(rs.getString("cat_name"));
        return category;
    }

    /**
     * Создаёт объект Book
     * @param rs результат запроса
     * @param category категория
     * @param allInfo если true, то загружаются все поля таблицы,
     * иначе только те, которые необходимы для отображения книги в списке
     * (ИД, категория, цена, название, ссылка)
     * @return
     * @throws SQLException 
     */
    private Book makeBook(ResultSet rs, Category category, boolean allInfo) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getLong("book_id"));
        book.setCategory(category);
        book.setCost(rs.getDouble("cost"));
        book.setTitle(rs.getString("title"));
        book.setLink(rs.getString("link"));
        if (allInfo) {
            book.setDescription(DatabaseHelper.toStringFromClob(rs.getClob("description")));
            book.setReleaseDate(rs.getTimestamp("release_date"));
            book.setIsShown(rs.getBoolean("is_shown"));
            book.setImageLink(rs.getString("image_link"));
        }
        return book;
    }

    /**
     * Создаёт объект Author
     * @param rs результат запроса
     * @param allInfo если true, то загружаются все поля таблицы,
     * иначе только те, которые необходимы для отображения автора в гиперссылке
     * (ИД, ФИО)
     * @return
     * @throws SQLException 
     */
    private Author makeAuthor(ResultSet rs, boolean allInfo) throws SQLException {
        Author author = new Author();
        author.setAuthorId(rs.getLong("author_id"));
        author.setName(rs.getString("name"));
        author.setSurname(rs.getString("surname"));
        author.setMiddlename(rs.getString("middlename"));
        if (allInfo) {
            author.setImageLink(rs.getString("image_link"));
            author.setBiography(DatabaseHelper.toStringFromClob(rs.getClob("biography")));
        }
        return author;
    }

    /**
     * Заполнить список авторов книги
     *
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
            Author author = makeAuthor(rs, false);
            authors.add(author);
        }
    }

    /**
     * Заполнить список параметров книги
     *
     * @param con соединение с БД
     * @param book книга
     * @throws SQLException
     */
    private void fillBookParameters(Connection con, Book book) throws SQLException {
        PreparedStatement ps = con.prepareStatement(SELECT_PARAMETERS_FOR_BOOK);
        ps.setLong(1, book.getBookId());
        List<BookParam> params = book.getParameters();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            BookParam param = new BookParam();
            BookAttr attr = new BookAttr();
            attr.setId(rs.getLong("attr_id"));
            attr.setName(rs.getString("attr_name"));
            param.setValue(rs.getString("value"));
            param.setAttr(attr);
            params.add(param);
        }
    }

    /**
     * Создаёт условия для регистронезависимого поиска значения в колонке на
     * основании введённой строки поиска. Пример: Введено "Пушкин: Руслан и
     * Людмила", колонка author.surname, минимальная длина слова: 3, delim = or.
     * Получится следующее условие (со скобками): ('and' = 'or' or
     * lower(author.surname) like '%' || lower('Пушкин') || '%' or
     * lower(author.surname) like '%' || lower('Руслан') || '%' or
     * lower(author.surname) like '%' || lower('Людмила') || '%')
     *
     * @param column имя колонки (вместе с псевдонимом таблицы)
     * @param preparedSearchText текст для поиска.
     * @param minLength минимальная длина слова
     * @param delim разделитель частей условия. Может содержать ТОЛЬКО одно из
     * следующих значений: or, and.
     * @return
     */
    private String getSearchCondition(String column, String preparedSearchText, int minLength, String delim) {
        StringTokenizer token = new StringTokenizer(preparedSearchText, " ");
        StringBuilder condition = new StringBuilder(" ( 'and' = '" + delim + "'");
        while (token.hasMoreTokens()) {
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
     * Заменяет в запросе выражения вида like_expression[column][or] на
     * результат метода getSearchCondition для column и or.
     *
     * @param select запрос (select)
     * @param searchText поисковой запрос
     * @return
     */
    private String prepareSelect(String select, String searchText) {
        String ret = select.replaceAll("like_expression\\[(.*)\\]\\[(.*)\\]",
                getSearchCondition("$1", searchText, MIN_SYMBOLS_IN_SEARCH, "$2"));
        return ret;
    }
    
    /**
     * Получить все книги, находящиеся в корзине
     * @param cart корзина
     * @return 
     */
    public List<Book> getBooksFromCart(ShoppingCart cart) {
        List<Book> books = new ArrayList<>();
        List<Long> bookIds = cart.getBookIds();
        if (bookIds.isEmpty()) {
            return books;
        }
        StringBuilder b = new StringBuilder(BOOK_SELECT_FOR_LIST + " and book_id in (");
        for (int i = 0; i < bookIds.size(); i++) {
            b.append("?,");
        }
        b.replace(b.length()-1, b.length(), ")");
        String query = b.toString();
        
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    query);
            int i = 1;
            for (Long id : bookIds) {
                ps.setLong(i, id);
                i++;
            }
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Category category = makeCategoryForBook(rs);
                Book book = makeBook(rs, category, false);
                fillBookAuthors(con, book);
                books.add(book);
            }
            return books;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    
    /**
     * Получить все книги, хранящиеся в истории юзера (т.е. купленные).
     * @param userId ИД юзера
     * @return 
     */
    public List<Book> getBooksFromHistory(long userId) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(BOOK_SELECT_FOR_LIST + " and book_id in"
                    + " (select book_id from history where user_id = ?)");
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                Category category = makeCategoryForBook(rs);
                Book book = makeBook(rs, category, false);
                fillBookAuthors(con, book);
                books.add(book);
            }
            return books;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
}
