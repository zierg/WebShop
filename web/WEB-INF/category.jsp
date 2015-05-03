<%-- 
    Document   : book
    Created on : 03.05.2015, 14:31:08
    Author     : Иван
--%>

<%@page import="objects.Category"%>
<%@page import="objects.BookParam"%>
<%@page import="common.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <% String ROOT = request.getContextPath();
        Category category = (Category) request.getAttribute("category");
        if (category == null) {
            out.print("fatal error");
            return;
        }
    %>
    <%= HTMLHelper.includeCSS(ROOT)%>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%= category.getTitle() %></title>
    </head>
    <jsp:include page="/WEB-INF/headers/search.jsp" flush="true"/>
    <body>
        <table border=1 class="item_table">
            <tr>
                <td class="item">
                    <font size="5pt"><%= category.getTitle() %></font>
                </td>
            </tr>
            
            <tr>
                <td class="item">
                    <a class="other" href="<%= ROOT %>/books?category_id=<%= category.getCategoryId() %>">
                        Все книги категории
                    </a>
                </td>
            </tr>
            <tr>
                <td class="item">
                    <%= HTMLHelper.getMultilineStringForHTML(category.getDescription())%>
                </td>
            </tr>
        </table>


    </body>
</html>
