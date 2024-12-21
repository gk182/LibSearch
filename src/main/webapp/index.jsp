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
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.1/css/all.min.css" integrity="sha512-5Hs3dF2AEPkpNAR7UiOHba+lRSJNeM2ECkwxUIxC1Q/FLycGTbNapWXB4tP889k5T5Ju8fs4b1P5z/iB4nMfSQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="bg-white p-4 flex justify-between items-center w-full h-full text-black">
    <div class="flex items-center w-30 cursor-pointer hover:opacity-70">
        <img src="./assets/losdac.png" alt="logo" class="w-10 h-10">
        <h1 class="ml-2 text-lg font-medium">Book Store</h1>
    </div>
    <div class="flex justify-around w-5/6">
        <div class="w-2/3">
            <form action="/LibSearch_war_exploded" method="get" class="flex items-center relative w-full">
                <input type="text" name="query" class="form-control me-2"
                       value="<%= request.getParameter("query") != null ? request.getParameter("query") : "" %>"
                       placeholder="Enter book title, author, or category">
                <button type="submit" class="btn btn-primary">Search</button>
            </form>
        </div>
        <div class="flex items-center">
            <div class="mr-6 mt-2 text-lg cursor-pointer hover:opacity-70">
                <a  href="/LibSearch_war_exploded/Login.jsp">
                    <div class="flex flex-col items-center">

                        <i class="fa-regular fa-user"></i>
                        <span class="text-xs">Login for staff</span>
                    </div>
                </a>

            </div>

        </div>
    </div>
</div>
<div class="border-t border-black my-4"></div>


<main class="container my-4">
        <section id="book-search">
            <!-- Display search results -->
            <div id="search-results">

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
                                    <a href="/LibSearch_war_exploded/bookDetails.jsp?id=<%= book.getId() %>" class="d-flex align-items-center text-decoration-none">
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
