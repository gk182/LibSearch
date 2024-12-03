<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Book" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Search</title>
</head>
<body>
    <header>
        <h1>Welcome to Our Bookstore</h1>
        <p><a href="Login.jsp">Login for staff?</a></p>
    </header>

    <main>
        <section id="book-search">
            <h2>Search for Books</h2>
            <form action="/LibSearch/api/searchBooks" method="get">
                <input type="text" name="query" placeholder="Enter book title, author, or category" >
                <button type="submit">Search</button>
            </form>

            <!-- Display search results -->
            <div id="search-results">
                <h3>All Books:</h3>
                <ul>
                    <%
                        List<Book> books = (List<Book>) request.getAttribute("books");
                        if (books == null) {
                            books = (List<Book>) getServletContext().getAttribute("books");
                        }
                        if (books != null && !books.isEmpty()) {
                            for (Book book : books) {
                    %>
                            <li>
                                <a href="/LibSearch/bookDetails.jsp?bookId=<%= book.getId() %>">
                                    <img src="<%= book.getImageLink() %>" alt="<%= book.getTitle() %>" style="width:100px;height:auto;">
                                    <strong><%= book.getTitle() %></strong> by <%= book.getAuthor() %>
                                </a>
                            </li>
                    <%
                            }
                        } else {
                    %>
                        <li>No books found.</li>
                    <%
                        }
                    %>
                </ul>
            </div>
        </section>
    </main>
</body>
</html>
