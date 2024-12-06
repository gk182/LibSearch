<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Book" %>
<%@ page import="model.BookDAO" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    HttpSession session1 = request.getSession();
    String role = (String) session1.getAttribute("role");

    if (role == null) {
        response.sendRedirect("Login.jsp"); // Chuyển hướng về trang đăng nhập nếu không có vai trò
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Management</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Arial', sans-serif;
        }
        h1 {
            text-align: center;
            margin-top: 20px;
            color: #343a40;
        }
        .table {
            margin: 30px auto;
            width: 90%;
            border: 1px solid #ddd;
        }
        .table th, .table td {
            padding: 10px;
            text-align: center;
        }
        .pagination {
            justify-content: center;
            margin-top: 20px;
        }
        .pagination .page-item.active .page-link {
            background-color: #007bff;
            border-color: #007bff;
        }
        .modal-dialog {
            max-width: 800px;
        }
    </style>
</head>
<body>
	<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <% if ("manager".equals(role)) { %>
            <jsp:include page="sidebar.jsp"/>
        <% } else if ("staff".equals(role)) { %>
            <jsp:include page="sidebarstaff.jsp"/>
        <% } %>
        <!-- Main Content -->
        <div class="col py-3">
            <h1>Book Management</h1>

    <!-- Search Form -->
    <form method="get" class="d-flex mb-4" action="BookManager.jsp">
        <input type="text" class="form-control" name="search" placeholder="Search by Title, Author, or Category" value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
        <button type="submit" class="btn btn-outline-secondary ms-2">Search</button>
    </form>

    <%
        String searchQuery = request.getParameter("search");
        if (searchQuery == null) {
            searchQuery = ""; // Default search query is empty
        }

        int pages = 0;
        int pageSize = 10;
        String pageStr = request.getParameter("pages");
        if (pageStr != null) {
            pages = Integer.parseInt(pageStr);
        }

        BookDAO bookDAO = new BookDAO();
        List<Book> books = bookDAO.searchBooks(searchQuery, pages, pageSize);
        int totalBooks = bookDAO.countBooksByQuery(searchQuery); // Use search query to count books
        int numPages = (int) Math.ceil((double) totalBooks / pageSize);
    %>

    <table class="table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Image</th>
                <th>Title</th>
                <th>Author</th>
                <th>Category</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <% for (Book book : books) { %>
                <tr>
                    <td><%= book.getId() %></td>
                    <td><img src="<%= book.getImageLink() %>" alt="Book Image" style="width:100px; height:auto;"></td>
                    <td><%= book.getTitle() %></td>
                    <td><%= book.getAuthor() %></td>
                    <td><%= book.getCategory() %></td>
                    <td>
                        <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#editBookModal" onclick="setEditFormData('<%= book.getId() %>', '<%= book.getTitle() %>', '<%= book.getAuthor() %>', '<%= book.getCategory() %>', '<%= book.getQuantity() %>', '<%= book.getPrice() %>', '<%= book.getPublisher() %>', '<%= book.getPublishYear() %>', '<%= book.getDescription().replace("'", "&#39;").replace("\"", "&quot;") %>')">Edit</button>
                        <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#deleteConfirmModal" data-book-id="<%= book.getId() %>">Delete</button>
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>

    <!-- Pagination -->
    <nav aria-label="Page navigation example">
        <ul class="pagination">
            <li class="page-item <%= (pages == 0) ? "disabled" : "" %>">
                <a class="page-link" href="?search=<%= searchQuery %>&pages=<%= pages - 1 %>">Previous</a>
            </li>
            <% for (int i = 0; i < numPages; i++) { %>
                <li class="page-item <%= (pages == i) ? "active" : "" %>">
                    <a class="page-link" href="?search=<%= searchQuery %>&pages=<%= i %>"><%= i + 1 %></a>
                </li>
            <% } %>
            <li class="page-item <%= (pages == numPages - 1) ? "disabled" : "" %>">
                <a class="page-link" href="?search=<%= searchQuery %>&pages=<%= pages + 1 %>">Next</a>
            </li>
        </ul>
    </nav>

    <!-- Add Book Modal -->
    <div class="modal fade" id="addBookModal" tabindex="-1" aria-labelledby="addBookModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addBookModalLabel">Add New Book</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="addBookForm">
                        <!-- Add Book Form Content -->
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" onclick="submitAddForm()">Add Book</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Delete Book Modal -->
    <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteConfirmModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to delete this book?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-danger" onclick="deleteBook()">Delete</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Book Modal -->
    <div class="modal fade" id="editBookModal" tabindex="-1" aria-labelledby="editBookModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editBookModalLabel">Edit Book</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="editBookForm">
                        <input type="hidden" id="editId" name="id">
                        <div class="mb-3">
                            <label for="editTitle" class="form-label">Title</label>
                            <input type="text" class="form-control" id="editTitle" name="title">
                        </div>
                        <div class="mb-3">
                            <label for="editAuthor" class="form-label">Author</label>
                            <input type="text" class="form-control" id="editAuthor" name="author">
                        </div>
                        <div class="mb-3">
                            <label for="editCategory" class="form-label">Category</label>
                            <input type="text" class="form-control" id="editCategory" name="category">
                        </div>
                        <div class="mb-3">
                            <label for="editQuantity" class="form-label">Quantity</label>
                            <input type="number" class="form-control" id="editQuantity" name="quantity">
                        </div>
                        <div class="mb-3">
                            <label for="editPrice" class="form-label">Price</label>
                            <input type="text" class="form-control" id="editPrice" name="price">
                        </div>
                        <div class="mb-3">
                            <label for="editPublisher" class="form-label">Publisher</label>
                            <input type="text" class="form-control" id="editPublisher" name="publisher">
                        </div>
                        <div class="mb-3">
                            <label for="editPublishYear" class="form-label">Publish Year</label>
                            <input type="number" class="form-control" id="editPublishYear" name="publishYear">
                        </div>
                        <div class="mb-3">
                            <label for="editDescription" class="form-label">Description</label>
                            <textarea class="form-control" id="editDescription" name="description"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" onclick="submitEditForm()">Save changes</button>
                </div>
            </div>
        </div>
    </div>
        </div>
    </div>
</div>
    <!-- Bootstrap JS and Dependencies -->
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.min.js"></script>

    <script>
        function setEditFormData(id, title, author, category, quantity, price, publisher, publishYear, description) {
            document.getElementById("editId").value = id;
            document.getElementById("editTitle").value = title;
            document.getElementById("editAuthor").value = author;
            document.getElementById("editCategory").value = category;
            document.getElementById("editQuantity").value = quantity;
            document.getElementById("editPrice").value = price;
            document.getElementById("editPublisher").value = publisher;
            document.getElementById("editPublishYear").value = publishYear;
            document.getElementById("editDescription").value = description;
        }

        function submitEditForm() {
            // Submit logic for editing book
        }

        function deleteBook() {
            // Delete book logic
        }

        function submitAddForm() {
            // Submit logic for adding new book
        }
    </script>

</body>
</html>
