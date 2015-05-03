<%-- 
    Document   : book
    Created on : 03.05.2015, 14:31:08
    Author     : Иван
--%>

<%@page import="objects.BookParam"%>
<%@page import="objects.Author"%>
<%@page import="common.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <% String ROOT = request.getContextPath();
        Author author = (Author) request.getAttribute("author");
        if (author == null) {
            out.print("fatal error");
            return;
        }
        boolean hasImage = !author.getImageLink().isEmpty();
        String authorString = (author.getSurname() + " "
                + author.getName()
                + " " + author.getMiddlename()).trim();
    %>
    <%= HTMLHelper.includeCSS(ROOT)%>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%= authorString %></title>
    </head>
    <jsp:include page="/WEB-INF/headers/search.jsp" flush="true"/>
    <body>
        <table border=1 class="item_table">
            <tr>
                <td class="item">
                    <font size="5pt"><%= authorString %></font>
                </td>
            </tr>
            
            <tr>
                <td class="item">
                    <a class="other" href="<%= ROOT %>/books?author_id=<%= author.getAuthorId() %>">
                        Все книги автора
                    </a>
                </td>
            </tr>
            <tr>
                <td class="item">
                    <% if (hasImage) {
                        String imgSrc = ROOT + "/" + HTMLHelper.BOOK_IMAGES + "/" + author.getImageLink();
                    %>
                    <a href="<%= imgSrc %>" target="blank">
                    <img class="author_image" src="<%= imgSrc %>" />
                    </a>
                    <%
                }%>
                    <%= HTMLHelper.getMultilineStringForHTML(author.getBiography())%>
                </td>
            </tr>
        </table>


    </body>
</html>
