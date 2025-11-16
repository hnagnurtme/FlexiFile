package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.User;
import model.bo.AuthBO;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private final AuthBO authBO = new AuthBO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = authBO.loginBO(email, password);
            if (user == null) {
                // Đăng nhập thất bại
                request.setAttribute("error", "Invalid email or password");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }

            // Lưu thông tin người dùng vào session
            HttpSession session = request.getSession(true);
            
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("email", email);
            session.setAttribute("loginTime", System.currentTimeMillis());
            session.setMaxInactiveInterval(30 * 60); // 30 phút

            response.sendRedirect("homeServlet");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during login. Please try again.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }
}
