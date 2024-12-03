<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header>
        <h1>Staff Dashboard</h1>
        <a href="Logout.jsp" class="logout">Logout</a>
    </header>

    <main>
        <!-- Book Management Section -->
        <section id="book-management">
            <h2>Book Management</h2>
            <a href="addBook.jsp">Add New Book</a>
            <table>
                <thead>
                    <tr>
                        <th>Title</th>
                        <th>Author</th>
                        <th>Category</th>
                        <th>Quantity</th>
                        <th>Price</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="book" items="${books}">
                        <tr>
                            <td>${book.title}</td>
                            <td>${book.author}</td>
                            <td>${book.category}</td>
                            <td>${book.quantity}</td>
                            <td>$${book.price}</td>
                            <td>
                                <a href="editBook.jsp?id=${book.id}">Edit</a> |
                                <a href="deleteBook?id=${book.id}" onclick="return confirm('Are you sure?')">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </section>
    </main>

    <footer>
        <p>&copy; 2024 Bookstore Staff Dashboard</p>
    </footer>
</body>
</html>
