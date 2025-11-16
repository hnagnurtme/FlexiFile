package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bo.AuthBO;

@WebServlet("/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final AuthBO authBO = new AuthBO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("verify-otp".equals(action)) {
            // Bước 2: Hiển thị trang nhập OTP
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("resetEmail") == null) {
                response.sendRedirect(request.getContextPath() + "/forgot-password");
                return;
            }
            request.getRequestDispatcher("/jsp/verify-otp.jsp").forward(request, response);
            
        } else if ("reset-password".equals(action)) {
            // Bước 3: Hiển thị trang đổi password
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("resetEmail") == null || 
                session.getAttribute("otpVerified") == null) {
                response.sendRedirect(request.getContextPath() + "/forgot-password");
                return;
            }
            request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
            
        } else {
            // Bước 1: Hiển thị trang nhập email (mặc định)
            request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("send-otp".equals(action)) {
            handleSendOTP(request, response);
        } else if ("verify-otp".equals(action)) {
            handleVerifyOTP(request, response);
        } else if ("reset-password".equals(action)) {
            handleResetPassword(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
        }
    }

    /**
     * Bước 1: Gửi OTP về email
     */
    private void handleSendOTP(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");

        try {
            // Kiểm tra email có tồn tại không
            if (!authBO.checkEmailExistBO(email)) {
                request.setAttribute("error", "Email không tồn tại trong hệ thống");
                request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
                return;
            }

            // Gửi OTP
            boolean sent = authBO.sendOTPBO(email);
            
            if (!sent) {
                request.setAttribute("error", "Không thể gửi mã OTP. Vui lòng thử lại sau.");
                request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
                return;
            }

            // Lưu email vào session
            HttpSession session = request.getSession(true);
            session.setAttribute("resetEmail", email);
            session.setMaxInactiveInterval(15 * 60); // 15 phút

            // Chuyển đến trang nhập OTP
            response.sendRedirect(request.getContextPath() + "/forgot-password?action=verify-otp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
        }
    }

    /**
     * Bước 2: Xác thực OTP
     */
    private void handleVerifyOTP(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("resetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String email = (String) session.getAttribute("resetEmail");
        String otp = request.getParameter("otp");

        try {
            // Verify OTP
            boolean verified = authBO.verifyOTPBO(email, otp);

            if (!verified) {
                request.setAttribute("error", "Mã OTP không đúng hoặc đã hết hạn");
                request.getRequestDispatcher("/jsp/verify-otp.jsp").forward(request, response);
                return;
            }

            // Đánh dấu OTP đã verify
            session.setAttribute("otpVerified", true);

            // Chuyển đến trang reset password
            response.sendRedirect(request.getContextPath() + "/forgot-password?action=reset-password");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/jsp/verify-otp.jsp").forward(request, response);
        }
    }

    /**
     * Bước 3: Reset password
     */
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("resetEmail") == null || 
            session.getAttribute("otpVerified") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String email = (String) session.getAttribute("resetEmail");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            // Validate password
            if (newPassword == null || newPassword.length() < 8) {
                request.setAttribute("error", "Mật khẩu phải có ít nhất 8 ký tự");
                request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
                return;
            }

            // Check password match
            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("error", "Mật khẩu xác nhận không khớp");
                request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
                return;
            }

            // Reset password
            boolean reset = authBO.resetPasswordBO(email, newPassword);

            if (!reset) {
                request.setAttribute("error", "Không thể đặt lại mật khẩu. Vui lòng thử lại.");
                request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
                return;
            }

            // Xóa session
            session.removeAttribute("resetEmail");
            session.removeAttribute("otpVerified");

            // Chuyển về trang login với thông báo thành công
            session.setAttribute("successMessage", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
        }
    }
}