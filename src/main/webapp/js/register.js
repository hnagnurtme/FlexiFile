// Toggle password visibility
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

// Form validation
const registerForm = document.getElementById('registerForm');
const usernameInput = document.getElementById('username');
const fullNameInput = document.getElementById('fullName');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const confirmPasswordInput = document.getElementById('confirmPassword');

const usernameError = document.getElementById('usernameError');
const fullNameError = document.getElementById('fullNameError');
const emailError = document.getElementById('emailError');
const passwordError = document.getElementById('passwordError');
const confirmPasswordError = document.getElementById('confirmPasswordError');

if (registerForm) {
    registerForm.addEventListener('submit', function(e) {
        let isValid = true;

        // Reset all errors
        usernameError.classList.remove('show');
        fullNameError.classList.remove('show');
        emailError.classList.remove('show');
        passwordError.classList.remove('show');
        confirmPasswordError.classList.remove('show');

        // Validate username
        if (!usernameInput.value.trim()) {
            usernameError.textContent = 'Vui lòng nhập tên đăng nhập';
            usernameError.classList.add('show');
            isValid = false;
        } else if (usernameInput.value.trim().length < 3) {
            usernameError.textContent = 'Tên đăng nhập phải có ít nhất 3 ký tự';
            usernameError.classList.add('show');
            isValid = false;
        }

        // Validate full name
        if (!fullNameInput.value.trim()) {
            fullNameError.textContent = 'Vui lòng nhập họ và tên';
            fullNameError.classList.add('show');
            isValid = false;
        } else if (fullNameInput.value.trim().length < 2) {
            fullNameError.textContent = 'Họ và tên phải có ít nhất 2 ký tự';
            fullNameError.classList.add('show');
            isValid = false;
        }

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
        } else if (passwordInput.value.length < 8) {
            passwordError.textContent = 'Mật khẩu phải có ít nhất 8 ký tự';
            passwordError.classList.add('show');
            isValid = false;
        }

        // Validate confirm password
        if (!confirmPasswordInput.value.trim()) {
            confirmPasswordError.textContent = 'Vui lòng xác nhận mật khẩu';
            confirmPasswordError.classList.add('show');
            isValid = false;
        } else if (passwordInput.value !== confirmPasswordInput.value) {
            confirmPasswordError.textContent = 'Mật khẩu xác nhận không khớp';
            confirmPasswordError.classList.add('show');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });

    // Clear errors on input
    usernameInput.addEventListener('input', function() {
        usernameError.classList.remove('show');
    });

    fullNameInput.addEventListener('input', function() {
        fullNameError.classList.remove('show');
    });

    emailInput.addEventListener('input', function() {
        emailError.classList.remove('show');
    });

    passwordInput.addEventListener('input', function() {
        passwordError.classList.remove('show');
    });

    confirmPasswordInput.addEventListener('input', function() {
        confirmPasswordError.classList.remove('show');
    });
}