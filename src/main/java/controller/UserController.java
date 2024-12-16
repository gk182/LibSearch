package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.UserDAO;

import java.io.IOException;

import util.*;


/**
 * Servlet implementation class UserController
 */
@WebServlet("/user")
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
		if (action != null) {
			switch (action) {
				case "login":
					Login(request, response);
					break;
				case "register":
					Register(request, response);
					break;
				case "update":
					update(request, response);
					break;
				case "delete":
					delete(request, response);
					break;
				case "forgot-password":
					ForgotPassword(request, response);
					break;
				case "reset-password":
					ResetPassword(request, response);
					break;
				default:
					response.sendRedirect("Login.jsp"); // Redirect to login if action is unknown
					break;
			}
		} else {
			response.sendRedirect("Login.jsp"); // Redirect to login if action is null
		}
	}

	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");

	    if (username != null) {
	        if (UserDAO.deleteUser(username)) {
	            request.setAttribute("successMessage", "User deleted successfully.");
	            request.getRequestDispatcher("edit_staff.jsp").forward(request, response); 
	        } else {
	            request.setAttribute("errorMessage", "Failed to delete user.");
	            request.getRequestDispatcher("edit_staff.jsp").forward(request, response); 
	        }
	    }
		
	}

	private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
	    String email = request.getParameter("email");
	    String role = request.getParameter("role");

	    if (UserDAO.updateUser(username, email, role)) {
	        request.setAttribute("successMessage", "User updated successfully!");
	        request.getRequestDispatcher("edit_staff.jsp").forward(request, response);  // Hiển thị thông báo thành công
	    } else {
	        request.setAttribute("errorMessage", "Failed to update user. Please try again.");
	        request.getRequestDispatcher("edit_staff.jsp").forward(request, response);  // Hiển thị thông báo lỗi
	    }
		
	}

	private void ResetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = (String) request.getSession().getAttribute("username");
		String newPassword = request.getParameter("newPassword");
		String confirmPassword = request.getParameter("confirmPassword");

		if (newPassword.equals(confirmPassword)) {
			if (UserDAO.updatePassword(username, newPassword)) {
				response.sendRedirect("Login.jsp"); // Đặt lại mật khẩu thành công
			} else {
				request.setAttribute("errorMessage", "Failed to reset password. Please try again.");
				request.getRequestDispatcher("ResetPassword.jsp").forward(request, response);
			}
		} else {
			request.setAttribute("errorMessage", "Passwords do not match.");
			request.getRequestDispatcher("ResetPassword.jsp").forward(request, response);
		}
	}

	private void ForgotPassword(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String email = request.getParameter("email");
		String username = UserDAO.getUsernameByEmail(email);
		if (username != null) {
			String otp = OTPGenerator.generateOTP();
			UserDAO.saveOTP(username, otp);
			EmailSender.sendEmail(email, otp);
			request.setAttribute("message", "An OTP has been sent to your email.");
			request.getSession().setAttribute("email", email); 
	        request.getSession().setAttribute("username", username); 
			request.getRequestDispatcher("VerifyOTP.jsp").forward(request, response);
		} else {
			request.setAttribute("errorMessage", "Email not found.");
			request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
		}
	}

	private void Login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
	    String pass = request.getParameter("password");

	    if (UserDAO.login(username, pass)) { // Đăng nhập thành công
	    	
	        String role = UserDAO.getRoleByUsername(username); 

	        request.getSession().setAttribute("username", username);
	        request.getSession().setAttribute("role", role);


			if ("staff".equals(role)) {
				response.sendRedirect("StaffPage.jsp");
	        } else if ("manager".equals(role)) {
	            response.sendRedirect("ManagerPage.jsp");
	        }
	        
	    } else {
	        request.setAttribute("errorMessage", "Incorrect username or password!");
	        request.getRequestDispatcher("Login.jsp").forward(request, response);
	    }
	}
	private void Register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String pass = request.getParameter("password");
		String role = request.getParameter("role");
		if (UserDAO.checkUserExists(username)) {
            request.setAttribute("errorMessage", "Username already exists. Please choose another one.");
            request.getRequestDispatcher("edit_staff.jsp").forward(request, response);  
        } else {
        	UserDAO.register(username, email, pass,role);
        	request.setAttribute("successMessage", "Registration successful!");
        	request.getRequestDispatcher("edit_staff.jsp").forward(request, response);
        }
	}
}
