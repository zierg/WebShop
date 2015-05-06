<%-- 
    Document   : profile
    Created on : 05.05.2015, 14:03:23
    Author     : Иван
--%>

<%@page import="objects.User"%>
<%@page import="common.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Личный кабинет</title>
    </head>
    <% String ROOT = request.getContextPath();%>
    <%
        User user = (User) request.getSession(false).getAttribute("user");
        if (user == null) {
            response.sendRedirect(ROOT + "/login.jsp");
            return;
        }
    %>
    <%= HTMLHelper.includeCSS(ROOT)%>
    <jsp:include page="/WEB-INF/headers/choose_header.jsp" flush="true"/>
    <center>
        <table class="center_bordered">
            <tr>
                <td>
                    <a href="<%= ROOT %>/history" >Просмотр покупок</a>
                </td>
            </tr>
            <tr>
                <td style="border-top: 1px solid gray;">
                   Данные пользователя 
                </td>
            </tr>
            <tr>
                <td>
                    <%
            HTMLHelper.FormTextField surname = new HTMLHelper.FormTextField(1);
            HTMLHelper.FormTextField name = new HTMLHelper.FormTextField(2);
            HTMLHelper.FormTextField middlename = new HTMLHelper.FormTextField(3);
            HTMLHelper.FormTextField mail = new HTMLHelper.FormTextField(4);
            surname.setAutofocus(true)
                    .setName("surname").setValue(HTMLHelper.fromNull(user.getSurname()))
                    .setTitle("Фамилия:");
            name
                    .setName("name").setValue(HTMLHelper.fromNull(user.getName()))
                    .setTitle("Имя:");
            middlename
                    .setName("middlename").setValue(HTMLHelper.fromNull(user.getMiddlename()))
                    .setTitle("Отчество:");
            mail
                    .setName("mail").setValue(HTMLHelper.fromNull(user.getMail()))
                    .setTitle("e-mail:");

            HTMLHelper.FormTextField[] userDataFields = {surname, name, middlename, mail};
        %>
        <%= HTMLHelper.makeFormWithFields(userDataFields, "userData", ROOT + "/saveUserData",
                "Сохранить данные", "POST") %>
                </td>
            </tr>
            <tr>
                <td style="border-top: 1px solid gray;">
                    Данные учётной записи
                </td>
            </tr>
            <tr>
                <td>
                    <%
            HTMLHelper.FormTextField oldPassword = new HTMLHelper.FormTextField(1);
            HTMLHelper.FormTextField password1 = new HTMLHelper.FormTextField(2);
            HTMLHelper.FormTextField password2 = new HTMLHelper.FormTextField(3);
            
            oldPassword.setName("old_password").setTitle("Текущий пароль:").setType("password");
            password1.setName("password1").setTitle("Новый пароль:").setType("password");
            password2.setName("password2").setTitle("Повторите новый пароль:").setType("password");
            

            HTMLHelper.FormTextField[] loginDataFields = {oldPassword, password1, password2};
        %>
        <%= HTMLHelper.makeFormWithFields(loginDataFields, "loginData", ROOT + "/saveLoginData",
                "Сохранить данные", "POST") %>
                </td>
            </tr>
        </table>
        
    </center>
</html>
