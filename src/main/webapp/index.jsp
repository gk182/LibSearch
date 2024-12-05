<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Book" %>
<%@ page import="model.BookDAO" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bookstore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <header class="bg-primary text-white p-3">
        <h1>Welcome to Our Bookstore</h1>
        <p><a href="/LibSearch/Login.jsp" class="text-white">Login for staff?</a></p>
    </header>

    <main class="container my-4">
        <section id="book-search">
            <h2>Search for Books</h2>
            <form action="/LibSearch/api/searchBooks" method="get" class="d-flex mb-4">
                <input type="text" name="query" class="form-control me-2" 
                       value="<%= request.getParameter("query") != null ? request.getParameter("query") : "" %>" 
                       placeholder="Enter book title, author, or category">
                <button type="submit" class="btn btn-primary">Search</button>
            </form>

            <!-- Display search results -->
            <div id="search-results">
                <h3>All Books:</h3>
                <ul class="list-unstyled">
                    <% 
                        BookDAO bookDAO = new BookDAO();
                        String query = request.getParameter("query") != null ? request.getParameter("query") : "";
                        int currentPage = request.getParameter("currentPage") != null ? 
                                          Integer.parseInt(request.getParameter("currentPage")) : 0;
                        int booksPerPage = 10;

                        List<Book> books;
                        int totalBooks;
                        int numPages;

                        if (!query.isEmpty()) {
                            books = bookDAO.searchBooks(query, currentPage, booksPerPage);
                            totalBooks = bookDAO.countBooksByQuery(query);
                        } else {
                            books = bookDAO.getBooks(currentPage, booksPerPage);
                            totalBooks = bookDAO.countBooks();
                        }

                        numPages = (int) Math.ceil((double) totalBooks / booksPerPage);

                        if (books != null && !books.isEmpty()) {
                            for (Book book : books) {
                    %>
                                <li class="mb-3">
                                    <a href="/LibSearch/bookDetails.jsp?bookId=<%= book.getId() %>" class="d-flex align-items-center text-decoration-none">
                                        <img src="<%= book.getImageLink() %>" alt="<%= book.getTitle() %>" class="me-3" style="width:100px;height:auto;">
                                        <div>
                                            <strong><%= book.getTitle() %></strong>
                                            <p class="mb-0">by <%= book.getAuthor() %></p>
                                        </div>
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

                <!-- Pagination -->
                <nav aria-label="Page navigation">
                    <ul class="pagination">
                        <% if (currentPage > 0) { %>
                            <li class="page-item">
                                <a class="page-link" href="?query=<%= query %>&currentPage=<%= currentPage - 1 %>">Previous</a>
                            </li>
                        <% } else { %>
                            <li class="page-item disabled">
                                <span class="page-link">Previous</span>
                            </li>
                        <% } %>
                        <% for (int i = 0; i < numPages; i++) { %>
                            <li class="page-item <%= (currentPage == i) ? "active" : "" %>">
                                <a class="page-link" href="?query=<%= query %>&currentPage=<%= i %>"><%= i + 1 %></a>
                            </li>
                        <% } %>
                        <% if (currentPage < numPages - 1) { %>
                            <li class="page-item">
                                <a class="page-link" href="?query=<%= query %>&currentPage=<%= currentPage + 1 %>">Next</a>
                            </li>
                        <% } else { %>
                            <li class="page-item disabled">
                                <span class="page-link">Next</span>
                            </li>
                        <% } %>
                    </ul>
                </nav>
            </div>
        </section>
    </main>
</body>
</html>
