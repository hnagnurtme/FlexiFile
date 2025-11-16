package util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidationData {
    // Regex chuẩn cho email
    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", 
            Pattern.CASE_INSENSITIVE
    );

    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Xác thực dữ liệu đăng ký.
     * @param username Tên đăng nhập
     * @param email Email
     * @param password Mật khẩu
     * @param fullName Họ và tên
     * @return Một Map<String, String> chứa các lỗi. 
     * Key là tên trường (vd: "email"), Value là thông báo lỗi.
     * Nếu Map rỗng, dữ liệu hợp lệ.
     */
    public static Map<String, String> validateRegistration(
            String username, 
            String email, 
            String password, 
            String fullName) {
        
        Map<String, String> errors = new HashMap<>();

        // 1. Kiểm tra Username
        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "Tên đăng nhập không được để trống.");
        }

        // 2. Kiểm tra Email
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email không được để trống.");
        } else if (!EMAIL_REGEX.matcher(email).matches()) {
            errors.put("email", "Email không đúng định dạng.");
        }

        // 3. Kiểm tra FullName
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Họ và tên không được để trống.");
        }

        // 4. Kiểm tra Password
        if (password == null || password.isEmpty()) {
            errors.put("password", "Mật khẩu không được để trống.");
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.put("password", "Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
        }

        return errors;
    }
    
    /**
     * Xác thực dữ liệu đăng nhập.
     * @param email Email
     * @param password Mật khẩu
     * @return Map lỗi
     */
    public static Map<String, String> validateLogin(String email, String password) {
        Map<String, String> errors = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email không được để trống.");
        }
        
        if (password == null || password.isEmpty()) {
            errors.put("password", "Mật khẩu không được để trống.");
        }
        
        return errors;
    }
}
