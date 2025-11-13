package model.bean;


import java.util.Date;
import java.util.List;

public class User {
    private String id;             // UUID hoặc Firestore document ID
    private String username;       // Tên hiển thị
    private String email;
    private String password;       // Hash password
    private String role;           // ADMIN / USER / USERPRO
    private int remainingConverts; // số lượt convert còn lại
    private Date createdAt;
    private Date updatedAt;

    // Optional profile info
    private String fullName;
    private String avatarUrl;

    // Liên kết
    private List<PaymentHistory> paymentHistoryList;
    private List<FileJob> fileJobHistoryList;

    // Constructors, getters, setters
}
