package model.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

import model.bean.User;
import util.FirebaseUtil;
import util.HashPassword;

public class AuthDAO {

    private static final String COLLECTION_NAME = "users";
    private static final String OTP_COLLECTION_NAME = "otpCodes";
    Firestore db = FirebaseUtil.getFirestore();

    public boolean register(User user) {
        try {
            // Kiểm tra nếu email đã tồn tại trong cơ sở dữ liệu
            QuerySnapshot existUser = db.collection(COLLECTION_NAME)
                    .whereEqualTo("email", user.getEmail())
                    .get()
                    .get();
            
            if (!existUser.isEmpty()) {
                System.out.println("Email is already registered: " + user.getEmail());
                return false;
            }

            // Hash password
            String hashedPassword = HashPassword.hashPassword(user.getPassword());

            // Thêm người dùng mới vào Firestore
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("password", hashedPassword); // Lưu trữ hash password
            userData.put("fullName", user.getFullName());
            userData.put("role", "USER");
            userData.put("remainingConverts", 7); // Mặc định 7 lượt convert
            userData.put("createdAt", new Date()); 
            userData.put("updatedAt", new Date());

            db.collection(COLLECTION_NAME)
                .document() // Tạo document mới với ID tự động
                .set(userData)
                .get(); // Chờ cho đến khi hoàn thành
                
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(String email, String password) {
        try {
            QuerySnapshot foundUser = db.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .get()
                    .get();
            
            if (foundUser.isEmpty()) {
                return null;
            }
            
            boolean passwordMatch = HashPassword.checkPassword(
                    password, 
                    foundUser.getDocuments().get(0).getString("password")
            );
            if (!passwordMatch) {
                return null;
            }
            DocumentSnapshot doc = foundUser.getDocuments().get(0); // Lấy document đầu tiên

            User user = new User();
            user.setId(doc.getId());
            user.setUsername(doc.getString("username"));
            user.setEmail(doc.getString("email"));
            user.setFullName(doc.getString("fullName"));
            user.setRole(doc.getString("role"));

            Long remainingConverts = doc.getLong("remainingConverts");
            user.setRemainingConverts(remainingConverts != null ? remainingConverts.intValue() : 0);

            user.setCreatedAt(doc.getDate("createdAt"));

            System.out.println("Login successfully: " + user.getEmail());
            
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // check existing email
    public boolean isEmailExist(String email) {
        try {
            QuerySnapshot existEmail = db.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .get()
                    .get();
            return !existEmail.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Generate OTP 6 digits
    public String generateOTP(String email) {
        try {
            Random random = new Random();
            int otp = 100000 + random.nextInt(900000);
            String otpCode = String.valueOf(otp);

            Long expiryTime = System.currentTimeMillis() + 5 * 60 * 1000; // 5 minutes

            // Save OTP to Firestore
            Map<String, Object> otpData = new HashMap<>();
            otpData.put("email", email);
            otpData.put("otpCode", otpCode);
            otpData.put("expiryTime", expiryTime);
            otpData.put("verified", false);
            otpData.put("createdAt", new Date());

            // Remove existing OTP for the email (if any)
            QuerySnapshot oldOtps = db.collection(OTP_COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .whereEqualTo("verified", false)
                    .get()
                    .get();
            
            for (DocumentSnapshot doc : oldOtps.getDocuments()) {
                doc.getReference().delete();
            }

            // save new OTP
            db.collection(OTP_COLLECTION_NAME)
                .document() // Tạo document mới với ID tự động
                .set(otpData)
                .get(); // Chờ cho đến khi hoàn thành
                
            return otpCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Verify OTP
    public boolean verifyOTP(String email, String otpCode) {
        try {
            QuerySnapshot otpQuery = db.collection(OTP_COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .whereEqualTo("otpCode", otpCode)
                    .whereEqualTo("verified", false)
                    .get()
                    .get();
            if (otpQuery.isEmpty()) {
                return false; // OTP invalid
            }

            DocumentSnapshot otpDoc = otpQuery.getDocuments().get(0);

            // Check expiry
            Long expiryTime = otpDoc.getLong("expiryTime");
            if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
                return false; // OTP expired
            }

            // Mark OTP as verified
            Map<String, Object> updates = new HashMap<>();
            updates.put("verified", true);
            updates.put("verifiedAt", new Date());
            otpDoc.getReference().update(updates).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Reset password
    public boolean resetPassword(String email, String newPassword) {
        try {
            QuerySnapshot verifiedOtp = db.collection(OTP_COLLECTION_NAME)
                .whereEqualTo("email", email)
                .whereEqualTo("verified", true)
                .get()
                .get();
            
            String hashedPassword = HashPassword.hashPassword(newPassword);

            // Find user by email
            QuerySnapshot userQuery = db.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .get()
                    .get();
            if (userQuery.isEmpty()) {
                System.out.println("User not found with email: " + email);
                return false; // User not found
            }

            String userId = userQuery.getDocuments().get(0).getId();    

            // Update password
            Map<String, Object> updates = new HashMap<>();
            updates.put("password", hashedPassword);
            updates.put("updatedAt", new Date());
            db.collection(COLLECTION_NAME)
                .document(userId)
                .update(updates)
                .get(); // Chờ cho đến khi hoàn thành
            
            // Delete all OTPs for this email
            QuerySnapshot otpQuery = db.collection(OTP_COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .get()
                    .get();
            for (DocumentSnapshot doc : otpQuery.getDocuments()) {
                doc.getReference().delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
