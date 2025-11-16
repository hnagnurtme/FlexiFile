<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác thực OTP - FlexiFile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/forgot-password.css">
</head>
<body>
    <div class="forgot-container">
        <div class="forgot-header">
            <h1>Xác thực OTP</h1>
            <p>Nhập mã OTP đã gửi về email: <strong><%= session.getAttribute("resetEmail") %></strong></p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form id="otpForm" method="POST" action="${pageContext.request.contextPath}/forgot-password">
            <input type="hidden" name="action" value="verify-otp">
            
            <div class="form-group">
                <label for="otp">Mã OTP</label>
                <input 
                    type="text" 
                    id="otp" 
                    name="otp" 
                    placeholder="Nhập mã OTP 6 chữ số"
                    maxlength="6"
                    pattern="\d{6}"
                    required
                    autocomplete="off"
                >
                <span class="error-message" id="otpError"></span>
                <p class="hint">Mã OTP có hiệu lực trong 5 phút</p>
            </div>

            <button type="submit" class="submit-button">
                Xác thực
            </button>
        </form>

        <div class="back-link">
            <a href="${pageContext.request.contextPath}/forgot-password">← Gửi lại mã OTP</a>
        </div>
    </div>

    <script>
        const form = document.getElementById('otpForm');
        const otpInput = document.getElementById('otp');
        const otpError = document.getElementById('otpError');

        // Chỉ cho nhập số
        otpInput.addEventListener('input', function(e) {
            this.value = this.value.replace(/[^0-9]/g, '');
            otpError.classList.remove('show');
        });

        form.addEventListener('submit', function(e) {
            otpError.classList.remove('show');

            if (otpInput.value.length !== 6) {
                otpError.textContent = 'Mã OTP phải có 6 chữ số';
                otpError.classList.add('show');
                e.preventDefault();
            }
        });
    </script>
</body>
</html>