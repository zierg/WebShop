<%-- 
    Document   : login
    Created on : 11.03.2015, 13:48:25
    Author     : ASUS
--%>

<%@page import="common.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Вход</title>
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
            HTMLHelper.FormTextField password = new HTMLHelper.FormTextField(2);
            username.setAutofocus(true)
                    .setName("username").setValue(enteredUsername)
                    .setTitle("Имя пользователя:");
            password.setName("password")
                    .setTitle("Пароль:").setType("password");
            HTMLHelper.FormTextField[] fields = {username, password};
        %>
        <%= HTMLHelper.makeFormWithFields(fields, "login", ROOT + "/login",
                "Войти", "POST") %>
        </center>
    </body>
</html>
