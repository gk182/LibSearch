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
@WebServlet(urlPatterns = "/api/searchBooks", loadOnStartup = 1)
public class BookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BookDAO bookDAO;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BookServlet() {
		super();
		bookDAO = new BookDAO();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		super.init();
//		bookDAO = new BookDAO();
//		List<Book> books = bookDAO.getBooks(0, 10);
//		getServletContext().setAttribute("books", books);
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
	    request.getRequestDispatcher("/index.jsp").forward(request, response);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
