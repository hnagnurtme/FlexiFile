<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - FlexiFile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/register.css">
</head>
<body>
    <div class="register-container">
        <div class="register-header">
            <h1>Đăng ký</h1>
            <p>Tạo tài khoản FlexiFile miễn phí</p>
        </div>

        <!-- Hiển thị thông báo lỗi -->
        <% if (request.getAttribute("errorRegister") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("errorRegister") %>
            </div>
        <% } %>

        <form id="registerForm" method="POST" action="${pageContext.request.contextPath}/register">
            <div class="form-group">
                <label for="username">Tên đăng nhập</label>
                <input 
                    type="text" 
                    id="username" 
                    name="username" 
                    placeholder="Nhập tên đăng nhập"
                    value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>"
                    required
                    autocomplete="username"
                >
                <span class="error-message" id="usernameError"></span>
            </div>

            <div class="form-group">
                <label for="fullName">Họ và tên</label>
                <input 
                    type="text" 
                    id="fullName" 
                    name="fullName" 
                    placeholder="Nhập họ và tên"
                    value="<%= request.getParameter("fullName") != null ? request.getParameter("fullName") : "" %>"
                    required
                    autocomplete="name"
                >
                <span class="error-message" id="fullNameError"></span>
            </div>

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
                        autocomplete="new-password"
                    >
                    <button type="button" class="toggle-password" data-target="password">
                        Hiện
                    </button>
                </div>
                <span class="error-message" id="passwordError"></span>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Xác nhận mật khẩu</label>
                <div class="input-wrapper">
                    <input 
                        type="password" 
                        id="confirmPassword" 
                        name="confirmPassword" 
                        placeholder="Nhập lại mật khẩu"
                        required
                        autocomplete="new-password"
                    >
                    <button type="button" class="toggle-password" data-target="confirmPassword">
                        Hiện
                    </button>
                </div>
                <span class="error-message" id="confirmPasswordError"></span>
            </div>

            <button type="submit" class="register-button">
                Đăng ký
            </button>
        </form>

        <div class="login-link">
            <p>Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay</a></p>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/register.js"></script>
</body>
</html>
