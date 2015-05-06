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
    <jsp:include page="/WEB-INF/headers/choose_header.jsp" flush="true"/>
    <body>
        <%
            List<Book> books = (List<Book>) request.getAttribute("books");
            List<Long> purchased = (List<Long>) request.getAttribute("purchased");
            if (books == null || purchased == null) {
                out.print("fatal error");
                return;
            }
            String searchText = request.getParameter("search_text");
            String authorIdString = request.getParameter("author_id");
            String categoryIdString = request.getParameter("category_id");
            String navigationParameter;
            if (searchText != null && !searchText.isEmpty()) {
                navigationParameter = "&search_text=" + searchText;
            } else if (authorIdString != null && !authorIdString.isEmpty()) {
                navigationParameter = "&author_id=" + authorIdString;
            } else if (categoryIdString != null && !categoryIdString.isEmpty()) {
                navigationParameter = "&category_id=" + categoryIdString;
            } else {
                navigationParameter = "";
            }
            
            String queryString = request.getQueryString();
                if (queryString != null && !queryString.isEmpty()) {
                    queryString = "?" + queryString;
                } else {
                    queryString = "";
                }
                String currentPath = ROOT + "/books" + queryString;
        %> 
        <%= HTMLHelper.makeBookTable(books, purchased, request, currentPath) %>
        
    <center>
        <%
            long totalAmount = (Long) request.getAttribute("total_amount");
            long first = (Long) request.getAttribute("first");
            int addition = (totalAmount % DEFAULT_PAGE_SIZE) == 0 ? 0 : 1;
            long currentPage = (first - 1) / DEFAULT_PAGE_SIZE + 1;
            if (currentPage != 1) {
                long curFirst = first - DEFAULT_PAGE_SIZE;
        %>
        <a class="other" href="<%= ROOT%>/books?first=<%= curFirst%><%= navigationParameter%>">
            <<</a>
            <%
                }
                long maxPage = (totalAmount / DEFAULT_PAGE_SIZE + addition);
                for (long i = 1; i <= maxPage; i++) {
                    long curFirst = 1 + (i - 1) * DEFAULT_PAGE_SIZE;
                    long pageNum = i;
                    if (pageNum != currentPage) {%>
        <a class="other" href="<%= ROOT%>/books?first=<%= curFirst%><%= navigationParameter%>">
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
            %>
        <a class="other" href="<%= ROOT%>/books?first=<%= curFirst%><%= navigationParameter%>">
            >></a>
            <%
                }
            %>

    </center>
</body>
</html>
