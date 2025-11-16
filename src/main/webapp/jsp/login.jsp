<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - FlexiFile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/login.css">
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h1>Đăng nhập</h1>
            <p>Chào mừng bạn trở lại FlexiFile</p>
        </div>

        <!-- Hiển thị thông báo lỗi từ server -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <!-- Hiển thị thông báo thành công (nếu có) -->
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>

        <form id="loginForm" method="POST" action="${pageContext.request.contextPath}/login">
            <div class="form-group">
                <label for="email">Email</label>
                <input 
                    type="email" 
                    id="email" 
                    name="email" 
                    placeholder="Nhập email của bạn"
                    value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>"
                    required
                    autocomplete="email"
                >
                <span class="error-message" id="emailError"></span>
            </div>

            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <div class="input-wrapper">
                    <input 
                        type="password" 
                        id="password" 
                        name="password" 
                        placeholder="Nhập mật khẩu"
                        required
                        autocomplete="current-password"
                    >
                    <button type="button" class="toggle-password" id="togglePassword">
                        Hiện
                    </button>
                </div>
                <span class="error-message" id="passwordError"></span>
            </div>

            <div class="forgot-password">
                <a href="${pageContext.request.contextPath}/jsp/forgot-password.jsp">Quên mật khẩu?</a>
            </div>

            <button type="submit" class="login-button">
                Đăng nhập
            </button>
        </form>

        <div class="register-link">
            <p>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a></p>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/login.js"></script>
</body>
</html>