<%-- 
    Document   : index
    Created on : 14.02.2015, 11:13:00
    Author     : Иван
--%>


<%@page import="objects.BookParam"%>
<%@page import="dao.BookParamsDao"%>
<%@page import="objects.BookAttr"%>
<%@page import="dao.BookAttrsDao"%>
<%@page import="oracle.OracleDaoFactory"%>
<%@page import="dao.DaoFactory"%>
<%@page import="common.DatabaseHelper"%>
<%@page import="common.HTMLHelper"%>
<%@page import="objects.Book"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
    </body>
    <%
        DaoFactory f = new OracleDaoFactory();
        BookParamsDao dao = f.getBookParamsDao();
        for (BookParam a : dao.getParamsByBookId(1)) {
            out.print(a.getBookId() + ": ");
            out.print(a.getAttr().getName() + " = " + a.getValue());
            out.print("<br>");
        }
    %>
    <a href="/youraction" data-paypal-button="true">
  <img src="//www.paypalobjects.com/en_US/i/btn/btn_xpressCheckout.gif" alt="Check out with PayPal" />
</a>
</html>
