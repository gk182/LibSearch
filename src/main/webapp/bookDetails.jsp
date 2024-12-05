<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Book" %>
<%@ page import="model.BookDAO" %>
<%@ page import="model.Shelf" %>
<%@ page import="model.ShelfDAO" %>
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
        ShelfDAO shelfDAO = new ShelfDAO();
        
        Book book = bookDAO.getBookById(bookId); // Lấy thông tin sách
        Shelf shelf = shelfDAO.getShelfByBookId(bookId); // Lấy thông tin kệ sách

        if (book != null) {
    %>
        <h1><%= book.getTitle() %></h1>
        <p>Author: <%= book.getAuthor() %></p>
        <p>Mã sách: <%= book.getId() %></p>
        <p>Thể loại: <%= book.getCategory() %></p>
        <p>Giá thành: <%= book.getPrice() %></p>
        <img src="<%= book.getImageLink() %>" alt="<%= book.getTitle() %>" style="width:200px;height:auto;">
        <p>NXB: <%= book.getPublisher() %></p>
        <p>Năm XB: <%= book.getPublishYear() %></p>
        <p>Mô tả sách: <%= book.getDescription() %></p>
        
        <%
            if (shelf != null) {
        %>
            <p>Kệ sách: <%= shelf.getShelfName() %></p>
        <%
            } else {
        %>
            <p>Kệ sách: Chưa được gắn.</p>
        <%
            }
        %>
    <%
        } else {
    %>
        <p>Book not found.</p>
    <%
        }
    %>
</body>
</html>
