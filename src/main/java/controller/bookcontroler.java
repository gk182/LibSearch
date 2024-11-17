package controller;

import java.io.*;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Book;
import model.BookDAO;

@WebServlet("/BookServlet")
public class bookcontroler extends HttpServlet {
    private BookDAO bookdao ;
    @Override
    public void init() {
        bookdao = new BookDAO();
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Mặc định là hiển thị danh sách sách
        }

        switch (action) {
            case "list":
                try {
                    listBooks(request, response);
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "search":
                try {
                    searchBooks(request, response);
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "add":
                addBook(request, response);
                break;
            case "update":
                updateBook(request, response);
                break;
            case "delete":
                deleteBook(request, response);
                break;
        }
    }
    private void listBooks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Book> books = bookdao.getAllBooks();
        request.setAttribute("books", books);
        request.getRequestDispatcher("list_books.jsp").forward(request, response);
    }

    private void searchBooks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        List<Book> books = bookdao.searchBooksByTitle(title);
        request.setAttribute("books", books);
        request.getRequestDispatcher("list_books.jsp").forward(request, response);
    }
    private void addBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String category = request.getParameter("category");
        String supplier = request.getParameter("supplier");
        String author = request.getParameter("author");
        String publisher = request.getParameter("publisher");
        int publishYear = Integer.parseInt(request.getParameter("publishYear"));
        int pageCount = Integer.parseInt(request.getParameter("pageCount"));
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");
        int shelfId = Integer.parseInt(request.getParameter("shelfId"));

        Book newBook = new Book(0, title, category, supplier, author, publisher, publishYear, pageCount, description, imageUrl, shelfId);
        boolean added = bookdao.addBook(newBook);

        if (added) {
            response.sendRedirect("BookServlet?action=list");
        } else {
            response.sendRedirect("error.jsp");
        }
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        String category = request.getParameter("category");
        String supplier = request.getParameter("supplier");
        String author = request.getParameter("author");
        String publisher = request.getParameter("publisher");
        int publishYear = Integer.parseInt(request.getParameter("publishYear"));
        int pageCount = Integer.parseInt(request.getParameter("pageCount"));
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");
        int shelfId = Integer.parseInt(request.getParameter("shelfId"));

        Book book = new Book(id, title, category, supplier, author, publisher, publishYear, pageCount, description, imageUrl, shelfId);
        boolean updated = bookdao.updateBook(book);

        if (updated) {
            response.sendRedirect("BookServlet?action=list");
        } else {
            response.sendRedirect("error.jsp");
        }
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean deleted = bookdao.deleteBook(id);

        if (deleted) {
            response.sendRedirect("BookServlet?action=list");
        } else {
            response.sendRedirect("error.jsp");
        }
    }
    public void destroy() {
    }
}