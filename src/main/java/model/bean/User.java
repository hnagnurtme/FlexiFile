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
    private String planType; // Free, Premium, Pro,...

    // Optional profile info
    private String fullName;
    private String avatarUrl;

    // Liên kết
    private List<PaymentHistory> paymentHistoryList;
    private List<FileJob> fileJobHistoryList;

    // Constructors, getters, setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public int getRemainingConverts() {
        return remainingConverts;
    }
    public void setRemainingConverts(int remainingConverts) {
        this.remainingConverts = remainingConverts;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getPlanType() {
        return planType;
    }
    public void setPlanType(String planType) {
        this.planType = planType;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public List<PaymentHistory> getPaymentHistoryList() {
        return paymentHistoryList;
    }
    public void setPaymentHistoryList(List<PaymentHistory> paymentHistoryList) {
        this.paymentHistoryList = paymentHistoryList;
    }
    public List<FileJob> getFileJobHistoryList() {
        return fileJobHistoryList;
    }
    public void setFileJobHistoryList(List<FileJob> fileJobHistoryList) {
        this.fileJobHistoryList = fileJobHistoryList;
    }
}
