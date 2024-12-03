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
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		super.init();
		bookDAO = new BookDAO();
		try {
			List<Book> books = bookDAO.getAllBooks();
			getServletContext().setAttribute("books", books);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServletException("Error initializing books", e);
		}
	}

	/**
	 * @throws ServletException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Book> books;
		String query = request.getParameter("query");
		try {
			if (query != null && !query.isEmpty()) {
				 books = new BookDAO().searchBooks(query);
			} else {
				books = (List<Book>) getServletContext().getAttribute("books");
			}
			request.setAttribute("books", books);
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving books");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
