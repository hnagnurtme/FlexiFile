// Toggle hiển thị/ẩn password
// Lấy nút (button)
const togglePassword = document.getElementById('togglePassword');
// Lấy ô nhập (input)
const passwordInput = document.getElementById('password');

if (togglePassword && passwordInput) {
    togglePassword.addEventListener('click', function(e) {
        // Ngăn hành vi mặc định của button (nếu nó là type="submit")
        e.preventDefault(); 
        
        // 1. Chuyển đổi loại input (giống code của bạn)
        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            this.textContent = 'Ẩn';
        } else {
            passwordInput.type = 'password';
            this.textContent = 'Hiện';
        }
    });
}

// Validation form
const loginForm = document.getElementById('loginForm');
const emailInput = document.getElementById('email');
const emailError = document.getElementById('emailError');
const passwordError = document.getElementById('passwordError');

if (loginForm) {
    loginForm.addEventListener('submit', function(e) {
        let isValid = true;

        // Reset errors
        emailError.classList.remove('show');
        passwordError.classList.remove('show');

        // Validate email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailInput.value.trim()) {
            emailError.textContent = 'Vui lòng nhập email';
            emailError.classList.add('show');
            isValid = false;
        } else if (!emailRegex.test(emailInput.value.trim())) {
            emailError.textContent = 'Email không hợp lệ';
            emailError.classList.add('show');
            isValid = false;
        }

        // Validate password
        if (!passwordInput.value.trim()) {
            passwordError.textContent = 'Vui lòng nhập mật khẩu';
            passwordError.classList.add('show');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });

    // Xóa error khi người dùng nhập
    emailInput.addEventListener('input', function() {
        emailError.classList.remove('show');
    });

    passwordInput.addEventListener('input', function() {
        passwordError.classList.remove('show');
    });
}