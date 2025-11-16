<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu - FlexiFile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/forgot-password.css">
</head>
<body>
    <div class="forgot-container">
        <div class="forgot-header">
            <h1>Quên mật khẩu</h1>
            <p>Nhập email để nhận mã xác thực</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form id="forgotForm" method="POST" action="${pageContext.request.contextPath}/forgot-password">
            <input type="hidden" name="action" value="send-otp">
            
            <div class="form-group">
                <label for="email">Email</label>
                <input 
                    type="email" 
                    id="email" 
                    name="email" 
                    placeholder="Nhập email của bạn"
                    required
                    autocomplete="email"
                >
                <span class="error-message" id="emailError"></span>
            </div>

            <button type="submit" class="submit-button">
                Gửi mã xác thực
            </button>
        </form>

        <div class="back-link">
            <a href="${pageContext.request.contextPath}/login">← Quay lại đăng nhập</a>
        </div>
    </div>

    <script>
        const form = document.getElementById('forgotForm');
        const emailInput = document.getElementById('email');
        const emailError = document.getElementById('emailError');

        form.addEventListener('submit', function(e) {
            emailError.classList.remove('show');

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailInput.value.trim()) {
                emailError.textContent = 'Vui lòng nhập email';
                emailError.classList.add('show');
                e.preventDefault();
            } else if (!emailRegex.test(emailInput.value.trim())) {
                emailError.textContent = 'Email không hợp lệ';
                emailError.classList.add('show');
                e.preventDefault();
            }
        });

        emailInput.addEventListener('input', function() {
            emailError.classList.remove('show');
        });
    </script>
</body>
</html>