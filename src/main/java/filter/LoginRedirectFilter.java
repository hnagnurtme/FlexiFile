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

/**
 * Filter để redirect user đã đăng nhập khi truy cập login/register
 */
@WebFilter(urlPatterns = {"/login", "/register"})
public class LoginRedirectFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("LoginRedirectFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Lấy session (không tạo mới)
        HttpSession session = httpRequest.getSession(false);
        
        // Kiểm tra user đã đăng nhập chưa
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        if (isLoggedIn) {
            // Đã đăng nhập -> Redirect về upload
            System.out.println("Logged in user tried to access login/register page");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/upload");
        } else {
            // Chưa đăng nhập -> Cho phép truy cập
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("LoginRedirectFilter destroyed");
    }
}