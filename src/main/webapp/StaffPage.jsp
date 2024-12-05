<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Dashboard</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <!-- Custom CSS -->
    <style>
        .bg-dark {
            background-color: #343a40 !important;
        }
        .nav-link {
            color: rgba(255, 255, 255, 0.75);
        }
        .nav-link:hover {
            color: white;
        }
        .dropdown-menu-dark {
            background-color: #343a40;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row flex-nowrap">
            <!-- Sidebar -->
            <div class="col-auto col-md-3 col-xl-2 px-sm-2 px-0 bg-dark">
                <div class="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2 text-white min-vh-100">
                    <a href="#" class="d-flex align-items-center pb-3 mb-md-0 me-md-auto text-white text-decoration-none">
                        <span class="fs-5 d-none d-sm-inline">Manager Menu</span>
                    </a>
                    <ul class="nav nav-pills flex-column mb-sm-auto mb-0 align-items-center align-items-sm-start" id="menu">
                        <li class="nav-item">
                            <a href="BookManager.jsp" class="nav-link align-middle px-0">
                                <i class="fs-4 bi-book"></i> <span class="ms-1 d-none d-sm-inline">Danh sách sách</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="Logout.jsp" class="nav-link-logout align-middle px-0">
                                <i class="fs-4 bi-box-arrow-right"></i> <span class="ms-1 d-none d-sm-inline">Logout</span>
                            </a>
                        </li>
                    </ul>
                    
                </div>
            </div>
            <!-- Main Content -->
            <div class="col py-3 main-content">
                <h3>Welcome to the Manager Dashboard</h3>
                <p>Use the sidebar to navigate through the options.</p>
            </div>
        </div>
    </div>
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- JavaScript for Modal Interaction -->
    <script>
        function populateEditModal(id, title, author, category, quantity, price) {
            document.getElementById("editBookId").value = id;
            document.getElementById("editTitle").value = title;
            document.getElementById("editAuthor").value = author;
            // Populate other fields as needed
        }

        function setDeleteBookId(id) {
            document.getElementById("deleteBookId").value = id;
        }
    </script>
    <script>
    document.addEventListener("DOMContentLoaded", function() {
        const links = document.querySelectorAll('.nav-link');
        links.forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault(); // Ngăn không cho trình duyệt tải lại trang
                const url = this.getAttribute('href'); // Lấy URL từ thuộc tính href của liên kết
                fetch(url)
                    .then(response => response.text())
                    .then(html => {
                        document.querySelector('.main-content').innerHTML = html;
                    })
                    .catch(error => console.error('Error loading the page: ', error));
            });
        });
    });
    </script>
</body>
</html>
