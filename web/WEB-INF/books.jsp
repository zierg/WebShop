<%-- 
    Document   : books
    Created on : 28.04.2015, 16:32:06
    Author     : Иван
--%>

<%@page import="common.HTMLHelper"%>
<%@page import="objects.Author"%>
<%@page import="objects.Book"%>
<%@page import="java.util.List"%>
<%@page import="static common.Constants.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Все книги</title>
    </head>
    <% String ROOT = request.getContextPath();%>
    <%= HTMLHelper.includeCSS(ROOT)%>
    <body>
        <%
            List<Book> books = (List<Book>) request.getAttribute("books");
            if (books == null) {
                out.print("fatal error");
                return;
            }
        %> 
        <table border=1 class="content_table">
            <tr>
                <th class="book_title">Название</th>
                <th class="category">Категория</th>
                <th class="cost">Стоимость</th>
                <th class="action">Действия</th>
            </tr>
            <%
                for (Book book : books) {
            %> 
            <tr>
                <td class="book_title">
                    <a class="main_link" href="<%= ROOT%>/book?book_id=<%= book.getBookId()%>"><%= book.getTitle()%></a><br>
                    <%
                        int i = 1;
                        int max = book.getAuthors().size();
                        for (Author author : book.getAuthors()) {
                            String hrefString = author.getSurname() + " "
                                    + author.getName().charAt(0) + "."
                                    + (!author.getMiddlename().isEmpty() ? (author.getMiddlename().charAt(0) +  ".") : "");
                    %>
                    <a class="help_link" href="<%= ROOT%>/author?author_id=<%= author.getAuthorId()%>"><%= hrefString%></a>
                    <%= (i < max ? " " : "") %>
                    <%
                            i++;
                        }
                    %>
                </td>
                <td class="category">
                    <a class="other" href="<%= ROOT%>/category?category_id=<%= book.getCategory().getCategoryId()%>">
                        <%= book.getCategory().getTitle()%></a>
                </td>
                <td class="cost">
                    <%= book.getCost()%> р.
                </td>
                <td class="action">
                    В корзину
                </td>
            </tr>
            <%
                }
            %>
        </table>
    <center>
        <%
            long totalAmount = (Long) request.getAttribute("total_amount");
            long first = (Long) request.getAttribute("first");
            long last = (Long) request.getAttribute("last");
            int addition = (totalAmount % DEFAULT_PAGE_SIZE) == 0 ? 0 : 1;
            //out.print("total = " + totalAmount + "<br>");
            //out.print("DEFAULT_PAGE_SIZE = " + DEFAULT_PAGE_SIZE + "<br>");
            long currentPage = (first - 1) / DEFAULT_PAGE_SIZE + 1;
            if (currentPage != 1) {
                long curFirst = first - DEFAULT_PAGE_SIZE;
                long curLast = curFirst +DEFAULT_PAGE_SIZE - 1;
                %>
                <a class="other" href="<%= ROOT%>/books?first=<%= curFirst%>&last=<%= curLast%>&total_amount=<%= totalAmount%>">
                <<</a>
                <%
            }
            long maxPage = (totalAmount / DEFAULT_PAGE_SIZE + addition);
            for (long i = 1; i <= maxPage; i++) {
                long curFirst = 1 + (i - 1) * DEFAULT_PAGE_SIZE;
                long curLast = curFirst + DEFAULT_PAGE_SIZE - 1;
                long pageNum = i;
                if (pageNum != currentPage) {%>
        <a class="other" href="<%= ROOT%>/books?first=<%= curFirst%>&last=<%= curLast%>&total_amount=<%= totalAmount%>">
            <%= pageNum%></a>
            <%
            } else {
            %>
            <%= pageNum%>
            <%
                    }
                }
            if (currentPage != maxPage) {
                long curFirst = first + DEFAULT_PAGE_SIZE;
                long curLast = Math.min(curFirst +DEFAULT_PAGE_SIZE - 1, totalAmount);
                %>
                <a class="other" href="<%= ROOT%>/books?first=<%= curFirst%>&last=<%= curLast%>&total_amount=<%= totalAmount%>">
                >></a>
                <%
            }
            %>

    </center>
</body>
</html>
