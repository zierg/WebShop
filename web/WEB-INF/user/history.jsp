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
        <title>Покупки</title>
    </head>
    <% String ROOT = request.getContextPath();%>
    <%
        List<Book> books = (List<Book>) request.getAttribute("books");
        List<Long> purchased = (List<Long>) request.getAttribute("purchased");
        if (books == null) {
            out.print("fatal error");
            return;
        }
    %>
    <%= HTMLHelper.includeCSS(ROOT)%>
    <jsp:include page="/WEB-INF/headers/choose_header.jsp" flush="true"/>
    <center>
        Покупки <br /></center>
        <%= HTMLHelper.makeBookTable(books, purchased, request, ROOT + "/cart") %>
</html>
