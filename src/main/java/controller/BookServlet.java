package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Book;
import model.BookDAO;

/**
 * Servlet implementation class BookServlet
 */
@WebServlet("/books")
public class BookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BookServlet() {
		super();
		// TODO Auto-generated constructor stub
	}



	/**
	 * @throws ServletException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String query = request.getParameter("query"); // Lấy từ khóa tìm kiếm
	    String pageParam = request.getParameter("currentPage");
	    int currentPage = (pageParam != null) ? Integer.parseInt(pageParam) : 0;
	    int booksPerPage = 10;

	    BookDAO bookDAO = new BookDAO();
	    List<Book> books = null;
	    int totalBooks = 0;

	    if (query != null && !query.isEmpty()) {
	        // Nếu có từ khóa tìm kiếm
	        try {
				books = bookDAO.searchBooks(query, currentPage, booksPerPage);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        try {
				totalBooks = bookDAO.countBooksByQuery(query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } else {
	        // Nếu không tìm kiếm
	        books = bookDAO.getBooks(currentPage, booksPerPage);
	        totalBooks = bookDAO.countBooks();
	    }

	    int numPages = (int) Math.ceil((double) totalBooks / booksPerPage);

	    // Đặt dữ liệu vào request
	    request.setAttribute("books", books);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("numPages", numPages);
	    request.setAttribute("query", query);

	    // Forward tới JSP
	    request.getRequestDispatcher("BookManager.jsp").forward(request, response);
		response.getWriter().append("Served at: ").append(request.getContextPath());

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		String action = request.getParameter("action");
		System.out.print(action);
		if(action != null){
			switch (action){
				case "update":
					update(request, response);
					break;
				case "delete":
				try {
					delete(request, response);
				} catch (ServletException | IOException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					break;
				default:
					response.sendRedirect("BookManager"); // Redirect to login if action is unknown
					break;
			}
		}else{
			response.sendRedirect("BookManager.jsp"); // Redirect to login if action is null

		}
	}




	public void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException{
	    String id = request.getParameter("id");
	    List<Book> books = null;
	    BookDAO BookDao = new BookDAO();

	    // Kiểm tra nếu id tồn tại
	    if (id != null) {
	        // Nếu xóa thành công
	        if (BookDao.deleteBook(id)) {
	            // Thêm thông báo thành công vào request
	            request.setAttribute("successMessage", "Book deleted successfully.");
	        } else {
	            // Thêm thông báo lỗi vào request nếu không xóa được
	            request.setAttribute("errorMessage", "Failed to delete book.");
	        }

	        // Lấy danh sách sách sau khi thao tác
	        books = BookDao.getAllBooks();
	        request.setAttribute("books", books);

	        // Chuyển hướng đến trang BookManager.jsp
	        request.getRequestDispatcher("BookManager.jsp").forward(request, response);
	    }
	}


	private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
		String title = request.getParameter("title");
		String author = request.getParameter("author");
		String category = request.getParameter("category");
		int quantity = Integer.parseInt(request.getParameter("quantity"));
		double price = Double.parseDouble(request.getParameter("price"));
		String description = request.getParameter("description");
		BookDAO BookDao = new BookDAO();

        try {
            if (BookDao.updateBook(title,author,category,quantity,price,description,id)) {
                request.setAttribute("successMessage", "Book updated successfully!");
                request.getRequestDispatcher("BookManager.jsp").forward(request, response);  // Hiển thị thông báo thành công
            } else {
                request.setAttribute("errorMessage", "Failed to update Book. Please try again.");
                request.getRequestDispatcher("BookManager.jsp").forward(request, response);  // Hiển thị thông báo lỗi
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }




}
