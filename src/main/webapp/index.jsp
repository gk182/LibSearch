<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Book" %>
<%@ page import="model.BookDAO" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Index</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <header>
        <h1>Welcome to Our Bookstore</h1>
        <p><a href="/LibSearch/Login.jsp">Login for staff?</a></p>
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
                        int pages = 0;
                        int pageSize = 10;
                        String pageStr = request.getParameter("pages");
                        if (pageStr != null) {
                            try {
                            	pages = Integer.parseInt(pageStr);
                            } catch (NumberFormatException e) {
                            	pages = 0; // Default to page 0 on parsing error
                            }
                        }
                        BookDAO bookDAO = new BookDAO();
                        List<Book> books = bookDAO.getBooks(pages, pageSize);
                        if (books == null) {
                            books = (List<Book>) getServletContext().getAttribute("books");
                        }
                        int totalBooks = bookDAO.countBooks();
                        int numPages = (int) Math.ceil((double) totalBooks / pageSize);

                        if (books != null && !books.isEmpty()) {
                            for (Book book : books) {
                            	System.out.println("Book: " + book);
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
                <nav aria-label="Page navigation example">
			        <ul class="pagination">
			            <li class="page-item <%= (pages == 0) ? "disabled" : "" %>"><a class="page-link" href="?pages=<%= pages - 1 %>">Previous</a></li>
			            <% for (int i = 0; i < numPages; i++) { %>
			                <li class="page-item <%= (pages == i) ? "active" : "" %>"><a class="page-link" href="?pages=<%= i %>"><%= i + 1 %></a></li>
			            <% } %>
			            <li class="page-item <%= (pages == numPages - 1) ? "disabled" : "" %>"><a class="page-link" href="?pages=<%= pages + 1 %>">Next</a></li>
			        </ul>
			    </nav>
            </div>
        </section>
    </main>
</body>
</html>
