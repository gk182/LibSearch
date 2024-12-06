<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.BookHistory" %>
<%@ page import="model.Book" %>
<%@ page import="model.BookHistoryDAO" %>
<%@ page import="model.BookDAO" %>
<%@ page import="java.sql.SQLException" %>
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
    <title>Change Log</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <style>
        .content {
            margin-left: 260px;
            padding: 20px;
        }
        .table {
            background-color: #fff;
        }
        body {
            background-color: #f8f9fa;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
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
            <div class="container-fluid">
                <h1 class="mb-4">Change Log</h1>

                <!-- Change History Section -->
                <h2>Change History</h2>
                <%-- Thanh tìm kiếm --%>
				<form method="get" action="bookhistory" class="mb-4">
				    <input type="text" name="search" class="form-control" placeholder="Search for a book..." value="<%= request.getAttribute("search") %>">
				    <button type="submit" class="btn btn-primary mt-2">Search</button>
				</form>
				
				<%-- Bảng hiển thị lịch sử thay đổi --%>
				<table class="table table-striped table-bordered">
				    <thead class="table-light">
				        <tr>
				            <th>ID</th>
				            <th>Book ID</th>
				            <th>Action</th>
				            <th>Field Name</th>
				            <th>Old Value</th>
				            <th>New Value</th>
				            <th>UserName</th>
				            <th>Timestamp</th>
				        </tr>
				    </thead>
				    <tbody>
				        <%
				            List<BookHistory> historyList = (List<BookHistory>) request.getAttribute("historyList");
				        	
				        	if(historyList == null){
				        		BookHistoryDAO historydao = new BookHistoryDAO();
					        	historyList = historydao.getAllHistory();
				        	}
				            if (historyList != null && !historyList.isEmpty()) {
				                for (BookHistory history : historyList) {
				        %>
				            <tr>
				                <td><%= history.getId() %></td>
				                <td><%= history.getBookId() %></td>
				                <td><%= history.getAction() %></td>
				                <td><%= history.getFieldName() %></td>
				                <td><%= history.getOldValue() %></td>
				                <td><%= history.getNewValue() %></td>
				                <td><%= history.getUserName() %></td>
				                <td><%= history.getTimestamp() %></td>
				            </tr>
				        <%
				                }
				            } else {
				        %>
				            <tr>
				                <td colspan="8" class="text-center">No history found.</td>
				            </tr>
				        <%
				            }
				        %>
				    </tbody>
				</table>
				
				<%-- Phân trang --%>
				<div class="pagination">
				    <%
				    	BookHistoryDAO bk = new BookHistoryDAO();
				        int totalPages = bk.getTotalRecords();
					    int currentPage = request.getParameter("currentPage") != null ? 
	                            Integer.parseInt(request.getParameter("currentPage")) : 0;
				        for (int i = 1; i <= totalPages; i++) {
				    %>
				        <a href="?page=<%= i %>&search=<%= request.getAttribute("search") %>" class="btn btn-link <%= (i == currentPage) ? "active" : "" %>"><%= i %></a>
				    <%
				        }
				    %>
				</div>
				                
            </div>
        </div>
    </div>
</body>
</html>
