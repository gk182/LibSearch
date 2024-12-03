<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Book" %>
<%@ page import="model.BookDAO" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Details</title>
</head>
<body>
    <%
        String bookId = request.getParameter("bookId");
        BookDAO bookDAO = new BookDAO();
        Book book = bookDAO.getBookById(Integer.parseInt(bookId));
        if (book != null) {
    %>
        <h1><%= book.getTitle() %></h1>
        <p>Author: <%= book.getAuthor() %></p>
        <p>Category: <%= book.getCategory() %></p>
        <p>Price: $<%= book.getPrice() %></p>
        <img src="<%= book.getImageLink() %>" alt="<%= book.getTitle() %>" style="width:200px;height:auto;">
        <p>Description: <%= book.getDescription() %></p>
    <%
        } else {
    %>
        <p>Book not found.</p>
    <%
        }
    %>
</body>
</html>
