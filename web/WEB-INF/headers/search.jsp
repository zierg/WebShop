<%-- 
    Document   : search
    Created on : 02.05.2015, 22:24:56
    Author     : Иван 
--%>

<%@page import="common.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <% 
        String ROOT = request.getContextPath();
        String enteredSearchText = HTMLHelper.fromNull(request.getParameter("search_text"));
        boolean inDescription = !HTMLHelper.fromNull(request.getParameter("in_description")).isEmpty();
    %>
    <%= HTMLHelper.includeCSS(ROOT)%>
    <body>
        <form action="<%= ROOT%>/books" method="GET">
            <table border=0>
                <tr>
                    <td>
                        <input width="1000"  type="text" name="search_text" value="<%= enteredSearchText%>" />
                        <br />
                        <input type="checkbox" name="in_description" <%= inDescription ? "checked" : "" %>/> <font size ="2">Искать в описаниях</font>
                    </td>
                    <td style="vertical-align: top;">
                        <input type="submit" value="Найти" />   
                    </td>
                </tr>
            </table>
            
            
        </form>
    </body>
</html>
