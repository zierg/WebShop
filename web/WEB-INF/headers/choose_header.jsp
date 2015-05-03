<%-- 
    Document   : choose_header
    Created on : 03.05.2015, 21:21:34
    Author     : Иван
--%>

<%@page import="common.HTMLHelper"%>
<%@page import="objects.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    User user = (User) request.getSession(false).getAttribute("user");
    String enteredSearchText = HTMLHelper.fromNull(request.getParameter("search_text"));
        boolean inDescription = !HTMLHelper.fromNull(request.getParameter("in_description")).isEmpty();
    boolean showUserButtons = false;
    if (user != null && !user.getIsAdmin()) {
        showUserButtons = true;
    }
%>
<% String ROOT = request.getContextPath();%>
<%= HTMLHelper.includeCSS(ROOT)%>
<html>
    <body>
        <%= showUserButtons %>
        <table border="0" width="100%" style="border-collapse: collapse;">
            <tr>
                <td class="header" width="50%" style="text-align: left;" colspan="2">
                    <a class="header" href="<%= ROOT %>/login">Войти</a>
                    <a class="header" href="<%= ROOT %>/register">Регистрация</a>
                </td>
                <td class="header" width="50%" style="text-align: right;" colspan="2">
                    <a class="header" href="<%= ROOT %>/cart">Корзина</a>
                </td>
            </tr>
            <tr>
                <td class="header" width="50%" style="text-align: center;" colspan="2">
                    Книжный магазин
                </td>
                <td class="header" width="50%" style="text-align: center;" colspan="2">
                    <form action="<%= ROOT%>/books" method="GET">
            <table border="0">
                <tr>
                    <td rowspan="2" width="65%">
                        &nbsp;
                    </td>
                    <td width="40%">
                        <input class="search" style="width: 99%;" type="text"
                               name="search_text" value="<%= enteredSearchText%>" />
                    </td>
                    <td style="vertical-align: top;">
                        <input type="submit" value="Найти" />   
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="text-align: left;">
                        <input type="checkbox" name="in_description" <%= inDescription ? "checked" : "" %>/>
                        <font size ="2">Искать в описаниях</font>
                    </td>
                </tr>
            </table>            
        </form>
                </td>
            </tr>
            <tr>
                <td width="30%">
                    &nbsp;
                </td>
                <td class="header" width="20%" style="text-align: center;">
                    <a class="header" href="<%= ROOT %>">Главная страница</a>
                </td>
                <td class="header" width="20%" style="text-align: center;">
                    <a class="header" href="<%= ROOT %>/books">Каталог</a>
                </td>
                <td width="30%">
                    &nbsp;
                </td>
            </tr>
        </table>
                <br />
    </body>
</html>