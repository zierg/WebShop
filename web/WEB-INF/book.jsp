<%-- 
    Document   : book
    Created on : 03.05.2015, 14:31:08
    Author     : Иван
--%>

<%@page import="objects.BookParam"%>
<%@page import="objects.Author"%>
<%@page import="objects.Book"%>
<%@page import="common.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <% String ROOT = request.getContextPath();
        Book book = (Book) request.getAttribute("book");
        if (book == null) {
            out.print("fatal error");
            return;
        }
        boolean hasImage = !book.getImageLink().isEmpty();
    %>
    <%= HTMLHelper.includeCSS(ROOT)%>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%= book.getTitle()%></title>
    </head>
    <jsp:include page="/WEB-INF/headers/search.jsp" flush="true"/>
    <body>
        <table border=1 class="item_table">
            <tr>
                <td class="item">
                    <font size="5pt"><%= book.getTitle()%></font>
                    <%= HTMLHelper.getInCartButtonCode(book) %>
                </td>
                <td  class="item_parameters" rowspan="3">
                    <%
                        for (BookParam p : book.getParameters()) {
                            if (!p.getValue().isEmpty()) {
                    %>
                    <%= p.getAttr().getName()%>: <%= p.getValue()%> <br />
                    <%
                            }
                        }
                    %>
                </td>
            </tr>
            
            <tr>
                <td class="item">
                    Автор<%= book.getAuthors().size() > 1 ? "ы" : "" %>:
                    <%= HTMLHelper.getBookAuthorsLinks(book, ROOT, "other")%>
                </td>
            </tr>
            <tr>
                <td class="item">
                    <% if (hasImage) {
                        String imgSrc = ROOT + "/" + HTMLHelper.BOOK_IMAGES + "/" + book.getImageLink();
                    %>
                    <a href="<%= imgSrc %>" target="blank">
                    <img class="book_image" src="<%= imgSrc %>" />
                    </a>
                    <%
                }%>
                    <%= HTMLHelper.getMultilineStringForHTML(book.getDescription())%>
                </td>
            </tr>
        </table>


    </body>
</html>