<%-- 
    Document   : register
    Created on : 04.05.2015, 0:05:00
    Author     : Иван
--%>

<%@page import="common.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Регистрация</title>
    </head>
    <body>
        <% 
            String ROOT = request.getContextPath();
            String enteredUsername = HTMLHelper.fromNull(request.getParameter("username"));
        %>
        <%= HTMLHelper.includeCSS(ROOT)%>
        <jsp:include page="/WEB-INF/headers/choose_header.jsp" flush="true"/>
        <center>
        <form action="<%= ROOT%>/register" method="POST">
            <table style="text-align: left;">
                <tr>
                    <td>
                        Имя пользователя:
                    </td>
                    <td>
                        <input type="text" class="other" name="username" value="<%= enteredUsername %>" autofocus/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Пароль:
                    </td>
                    <td>
                        <input type="password" class="other" name="password1" />
                    </td>
                </tr>
                <tr>
                    <td>
                        Повторите пароль:
                    </td>
                    <td>
                        <input type="password" class="other" name="password2" />
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="text-align: center;">
                        <input type="submit" value="Зарегистрироваться" />
                    </td>
                </tr>
            </table>
        </form>
        </center>
    </body>
</html>
