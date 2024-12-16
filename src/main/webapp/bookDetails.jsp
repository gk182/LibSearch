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
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">


    <title>Book Details</title>
</head>
<body>
    <%
        String bookId = request.getParameter("id");
        BookDAO bookDAO = new BookDAO();
        ShelfDAO shelfDAO = new ShelfDAO();


        Book book = bookDAO.getBookById(bookId); // Lấy thông tin sách
        Shelf shelf = shelfDAO.getShelfByBookId(bookId); // Lấy thông tin kệ sách

        if (book != null) {
    %>

    <div class="w-full mt-[50px] flex flex-row items-center  justify-center gap-[250px]">
        <img src="<%= book.getImageLink() %>" alt="<%= book.getTitle() %>" style="width:200px;height:auto;">
        <div class="flex flex-col space-y-2">
            <h1 class="text-2xl font-bold"><%= book.getTitle() %></h1>
            <p class="flex gap-[50px]">
                <span class="font-bold">Author:</span>
                <span class="ml-2"><%= book.getAuthor() %></span>
            </p>
            <p class="flex gap-[50px]">
                <span class="font-bold">Mã sách:</span>
                <span class="ml-2"><%= book.getId() %></span>
            </p>
            <p class="flex gap-[50px]">
                <span class="font-bold">Thể loại:</span>
                <span class="ml-2"><%= book.getCategory() %></span>
            </p>
            <p class="flex gap-[50px]">
                <span class="font-bold">Giá thành:</span>
                <span class="ml-2"><%= book.getPrice() %></span>
            </p>
            <p class="flex gap-[50px]">
                <span class="font-bold">NXB:</span>
                <span class="ml-2"><%= book.getPublisher() %></span>
            </p>
            <p class="flex gap-[50px]">
                <span class="font-bold">Năm XB:</span>
                <span class="ml-2"><%= book.getPublishYear() %></span>
            </p>
            <p class="flex gap-[50px]">
                <span class="font-bold">Ở kệ sách:</span>
                <span class="ml-2">
                       <%
                           if (shelf != null) {
                       %>
             <%= shelf.getShelfName() %>
        <%
        } else {
        %>
            <p>Kệ sách: Chưa được gắn.</p>
        <%
            }
        %>
                </span>
            </p>
            <div class="flex flex-row items-center">
                <button type="submit" class="btn btn-primary">Buy Now</button>
                <button type="submit" class="btn border border-black bg-white text-black px-[4px] py-[2px] rounded hover:bg-gray-200">
                    Add to cart
                </button>
            </div>
        </div>

    </div>
    <div class="mt-[30px]">
    <h3 class="text-[20px] font-bold">Description</h3>
    <p class="mx-[20px]" id="description">
        <%= book.getDescription() %>
    </p>
    <button id="toggleButton" class="mt-[10px] bg-blue-500 text-white px-[10px] py-[5px] rounded" onclick="toggleDescription()">Đọc thêm</button>
    </div>



        

    <%
        } else {
    %>
        <p>Book not found.</p>
    <%
        }
    %>
    <script>
        function toggleDescription() {
            var description = document.getElementById("description");
            var button = document.getElementById("toggleButton");

            if (description.style.maxHeight && description.style.maxHeight !== "100px") {
                description.style.maxHeight = "100px"; // Thiết lập lại chiều cao ban đầu
                button.innerText = "Đọc thêm";
            } else {
                description.style.maxHeight = description.scrollHeight + "px"; // Mở rộng để hiển thị toàn bộ
                button.innerText = "Ẩn bớt";
            }
        }

        // Thiết lập ban đầu để giới hạn chiều cao của đoạn văn bản
        window.onload = function() {
            var description = document.getElementById("description");
            if (description.scrollHeight > 100) { // Giới hạn chiều cao ban đầu (100px)
                description.style.maxHeight = "100px";
                description.style.overflow = "hidden";
                document.getElementById("toggleButton").style.display = "block";
            } else {
                document.getElementById("toggleButton").style.display = "none";
            }
        }
    </script>
</body>
</html>
