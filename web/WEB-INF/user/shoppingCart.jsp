<%-- 
    Document   : profile
    Created on : 05.05.2015, 14:03:23
    Author     : Иван
--%>

<%@page import="objects.Book"%>
<%@page import="java.util.List"%>
<%@page import="objects.ShoppingCart"%>
<%@page import="objects.User"%>
<%@page import="common.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Корзина</title>
    </head>
    <% String ROOT = request.getContextPath();%>
    <%
        User user = (User) request.getSession(false).getAttribute("user");
        List<Book> books = (List<Book>) request.getAttribute("books");
        if (books == null) {
            out.print("fatal error");
            return;
        }
    %>
    <%= HTMLHelper.includeCSS(ROOT)%>
    <jsp:include page="/WEB-INF/headers/choose_header.jsp" flush="true"/>
    <center>
        Корзина <br /></center>
        <%= HTMLHelper.makeBookTable(books, null, request, ROOT + "/cart") %>
    <%
        double totalCost = 0;
        for (Book book : books) {
            totalCost += book.getCost();
        }
    %>
    <p style="text-align: right; width: 99%;">Общая стоимость: <%= totalCost %> р.
        <br />
        <a href ="<%= ROOT %>/purchase">Купить</a>
        <br />
        <a href ="<%= ROOT %>/clearCart">Очистить корзину</a>
    </p>
    
</html>
