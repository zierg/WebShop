<%-- 
    Document   : index
    Created on : 14.02.2015, 11:13:00
    Author     : Иван
--%>

<%@page import="common.HTMLHelper"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Книжный магазин</title>
    </head>
    <% String ROOT = request.getContextPath();%>
    <%= HTMLHelper.includeCSS(ROOT)%>
    <jsp:include page="/WEB-INF/headers/choose_header.jsp" flush="true"/>
    <body>
        <table class="main_table">
            <tr>
                <th width="20%" class="news" style="border-width: 2px 2px 1px 2px;">
                    Новости
                </th>
                <td rowspan="2">
            <center>
                    <%
                        char[] colorCharacters = "0123456789abcdef".toCharArray();
                        char[] currentColor = new char[6];
                        char[] text = "Здесь может быть ваша реклама".toCharArray();
                        for(char c : text) {
                            for (int i = 0; i < 6; i++) {
                                int charNum = (int) ((Math.random()*0.99) * colorCharacters.length);
                                currentColor[i] = colorCharacters[charNum];
                            }
                            int size = (int) (Math.random() * 7);
                            %>
                            <font size="<%= size %>" color="<%= new String(currentColor) %>"><%= c %></font>
                            <%
                        }
                    %>
                    </center>
                </td>
            </tr>
            <tr>
                <td class="news" style="border-width: 1px 2px 2px 2px;">
                    Новость
                </td>
            </tr>
        </table>
    </body>
</html>
