package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Book;
import model.BookDAO;
import model.BookHistory;
import model.BookHistoryDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet implementation class BookHistoryServlet
 */
@WebServlet("/bookhistory")
public class BookHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookHistoryServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchQuery = request.getParameter("search");  // Lấy từ khóa tìm kiếm
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            searchQuery = "";  // Gán giá trị mặc định nếu không có từ khóa tìm kiếm
        }
        int pageSize = 10;  // Số bản ghi mỗi trang
        int page = 1;  // Mặc định là trang đầu tiên

        // Lấy số trang hiện tại từ request
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        // Tính toán chỉ số bắt đầu của bản ghi trong cơ sở dữ liệu
        int start = (page - 1) * pageSize;

        // Lấy danh sách các bản ghi với phân trang và tìm kiếm
        BookHistoryDAO historydao = new BookHistoryDAO();
        List<BookHistory> historyList = historydao.getHistoryWithPaginationAndSearch(start, pageSize, searchQuery);

        // Lấy tổng số bản ghi
        int totalRecords = historydao.getTotalRecordsQuery(searchQuery);

        // Tính số trang
        int totalPages = (int) Math.ceil(totalRecords * 1.0 / pageSize);

        // Gửi dữ liệu về JSP
        request.setAttribute("historyList", historyList);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("search", searchQuery);

        request.getRequestDispatcher("ChangeLog.jsp").forward(request, response);
    }


    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}