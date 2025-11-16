<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lại mật khẩu - FlexiFile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/forgot-password.css">
</head>
<body>
    <div class="forgot-container">
        <div class="forgot-header">
            <h1>Đặt lại mật khẩu</h1>
            <p>Nhập mật khẩu mới cho tài khoản của bạn</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form id="resetForm" method="POST" action="${pageContext.request.contextPath}/forgot-password">
            <input type="hidden" name="action" value="reset-password">
            
            <div class="form-group">
                <label for="newPassword">Mật khẩu mới</label>
                <div class="input-wrapper">
                    <input 
                        type="password" 
                        id="newPassword" 
                        name="newPassword" 
                        placeholder="Nhập mật khẩu mới"
                        required
                        autocomplete="new-password"
                    >
                    <button type="button" class="toggle-password" data-target="newPassword">
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
                        placeholder="Nhập lại mật khẩu mới"
                        required
                        autocomplete="new-password"
                    >
                    <button type="button" class="toggle-password" data-target="confirmPassword">
                        Hiện
                    </button>
                </div>
                <span class="error-message" id="confirmError"></span>
            </div>

            <button type="submit" class="submit-button">
                Đặt lại mật khẩu
            </button>
        </form>
    </div>

    <script>
        const toggleButtons = document.querySelectorAll('.toggle-password');
        toggleButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                const targetId = this.getAttribute('data-target');
                const input = document.getElementById(targetId);
                
                if (input.type === 'password') {
                    input.type = 'text';
                    this.textContent = 'Ẩn';
                } else {
                    input.type = 'password';
                    this.textContent = 'Hiện';
                }
            });
        });

        const form = document.getElementById('resetForm');
        const newPassword = document.getElementById('newPassword');
        const confirmPassword = document.getElementById('confirmPassword');
        const passwordError = document.getElementById('passwordError');
        const confirmError = document.getElementById('confirmError');

        form.addEventListener('submit', function(e) {
            let isValid = true;
            passwordError.classList.remove('show');
            confirmError.classList.remove('show');

            if (newPassword.value.length < 8) {
                passwordError.textContent = 'Mật khẩu phải có ít nhất 8 ký tự';
                passwordError.classList.add('show');
                isValid = false;
            }

            if (newPassword.value !== confirmPassword.value) {
                confirmError.textContent = 'Mật khẩu xác nhận không khớp';
                confirmError.classList.add('show');
                isValid = false;
            }

            if (!isValid) {
                e.preventDefault();
            }
        });

        newPassword.addEventListener('input', function() {
            passwordError.classList.remove('show');
        });

        confirmPassword.addEventListener('input', function() {
            confirmError.classList.remove('show');
        });
    </script>
</body>
</html>