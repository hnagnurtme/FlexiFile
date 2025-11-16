package model.bo;

import java.util.Map;

import model.bean.User;
import model.dao.AuthDAO;
import util.EmailUtil;
import util.ValidationData;

public class AuthBO {
    private static final AuthDAO authDAO = new AuthDAO();
    private static final ValidationData validationData = new ValidationData();

    public boolean registerBO(User user) {
        // validation data 
        Map<String, String> errors = validationData.validateRegistration(
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getFullName()
        );
        if (!errors.isEmpty()) {
            errors.forEach((field, error) -> {
                System.out.println("Validation error in " + field + ": " + error);
            });
            return false;
        }
        boolean isRegistered = authDAO.register(user);

        if (isRegistered) {
            EmailUtil.sendWelcomeEmail(user.getEmail(), user.getFullName());
        }
        return isRegistered;
    }

    public User loginBO(String email, String password) {
        // validation data 
        Map<String, String> errors = validationData.validateLogin(email, password);
        if (!errors.isEmpty()) {
            errors.forEach((field, error) -> {
                System.out.println("Validation error in " + field + ": " + error);
            });
            return null;
        }
        return authDAO.login(email, password);
    }

    // check email exist
    public boolean checkEmailExistBO(String email) {
        // validation data
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return authDAO.isEmailExist(email);
    }

    // generate OTP 
    public String generateOTPBO(String email) {
        return authDAO.generateOTP(email);
    }

    // Send OTP 
    public boolean sendOTPBO(String email) {
        // validation data
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        if (!checkEmailExistBO(email)) {
            return false;
        }

        String otpCode = generateOTPBO(email);

        if (otpCode == null) {
            return false;
        }

        return EmailUtil.sendOTP(email, otpCode);
    }

    // verify OTP
    public boolean verifyOTPBO(String email, String otp) {
        // validation data
        if (email == null || email.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
            return false;
        }

        if (!otp.matches("\\d{6}")) {
            return false;
        }
        return authDAO.verifyOTP(email, otp);
    }

    // reset password
    public boolean resetPasswordBO(String email, String newPassword) {
        // Validation
    if (email == null || email.trim().isEmpty()) {
        System.out.println("Email is empty");
        return false;
    }

    if (newPassword == null || newPassword.isEmpty()) {
        System.out.println("Password is empty");
        return false;
    }
    
    if (newPassword.length() < 8) {
        System.out.println("Password must be at least 8 characters");
        return false;
    }
    
    return authDAO.resetPassword(email, newPassword);
    }
}
