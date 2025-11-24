package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/upload", "/api/job-status", "/profile", "/homeServlet" })
public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        // Kiểm tra user đã đăng nhập chưa
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        String requestURI = httpRequest.getRequestURI();

        if (!isLoggedIn) {
            // Lưu URL người dùng đang cố truy cập để redirect sau khi login
            session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", requestURI);

            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=unauthorized");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter destroyed");
    }
}
