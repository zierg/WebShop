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
 
        <%
            HTMLHelper.FormTextField username = new HTMLHelper.FormTextField(1);
            HTMLHelper.FormTextField password1 = new HTMLHelper.FormTextField(2);
            HTMLHelper.FormTextField password2 = new HTMLHelper.FormTextField(3);
            username.setAutofocus(true)
                    .setName("username").setValue(enteredUsername)
                    .setTitle("Имя пользователя:");
            password1.setName("password1")
                    .setTitle("Пароль:").setType("password");
            password2.setName("password2")
                    .setTitle("Повторите пароль:").setType("password");
            HTMLHelper.FormTextField[] fields = {username, password1, password2};
        %>
        <%= HTMLHelper.makeFormWithFields(fields, "register", ROOT + "/register",
                "Зарегистрироваться", "POST") %>
        </center>
    </body>
</html>
