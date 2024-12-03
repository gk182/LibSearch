package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.UserDAO;
import util.EmailSender;
import util.OTPGenerator;

import java.io.IOException;


/**
 * Servlet implementation class VerifyOTPServlet
 */
@WebServlet("/verify-otp")
public class VerifyOTPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyOTPServlet() {
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
		String username = (String) request.getSession().getAttribute("username");
		String otp = "";
		
		// Ghép các số OTP từ form
		for (int i = 1; i <= 6; i++) {
			otp += request.getParameter("otp-" + i);
		}

		String action = request.getParameter("action");

		if ("resend-otp".equals(action)) {
			// Kiểm tra thời gian gửi lại OTP
			if (UserDAO.canResendOTP(username)) {
				String email = (String) request.getSession().getAttribute("email");
				otp = OTPGenerator.generateOTP();
				EmailSender.sendEmail(email, otp);
				UserDAO.saveOTP(username, otp);
				request.setAttribute("message", "A new OTP has been sent to your email.");
			} else {
				request.setAttribute("errorMessage", "You can only request a new OTP after 1 minute.");
			}
			request.getRequestDispatcher("VerifyOTP.jsp").forward(request, response);
			return;
		}
		System.out.print(otp);
		System.out.print(username);
		int verificationResult = UserDAO.verifyOTP(username, otp);
		
		if (verificationResult == 1) {
			response.sendRedirect("ResetPassword.jsp");
		} else if (verificationResult == 0) {
			// OTP không đúng
			request.setAttribute("errorMessage", "Invalid OTP. Please try again.");
			request.getRequestDispatcher("VerifyOTP.jsp").forward(request, response);
		} else {
			// OTP đã hết hạn
			request.setAttribute("errorMessage", "OTP has expired. Please request a new one.");
			request.getRequestDispatcher("VerifyOTP.jsp").forward(request, response);
		}
	}

	

}
