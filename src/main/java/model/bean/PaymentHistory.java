package model.bean;

import java.util.Date;

public class PaymentHistory {
    private String id;                  // Firestore document ID
    private String userId;              // liên kết User
    private String orderId;             // VNPay order ID (vnp_TxnRef)
    private double amount;              // Số tiền thanh toán
    private String status;              // SUCCESS / FAILED / PENDING
    private String vnpTransactionNo;    // VNPay transaction code
    private String vnpBankCode;         // Mã ngân hàng
    private String vnpBankTranNo;       // Mã giao dịch ngân hàng
    private String vnpResponseCode;     // Mã phản hồi từ VNPay
    private String orderInfo;           // Thông tin đơn hàng (gói dịch vụ)
    private String planType;            // BASIC / PREMIUM / PRO
    private int additionalConverts;     // Số lượt convert được thêm
    private Date paymentDate;           // Ngày thanh toán
    private Date createdAt;             // Ngày tạo record

    // Constructors
    public PaymentHistory() {
        this.createdAt = new Date();
    }

    public PaymentHistory(String userId, String orderId, double amount, String status) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVnpTransactionNo() {
        return vnpTransactionNo;
    }

    public void setVnpTransactionNo(String vnpTransactionNo) {
        this.vnpTransactionNo = vnpTransactionNo;
    }

    public String getVnpBankCode() {
        return vnpBankCode;
    }

    public void setVnpBankCode(String vnpBankCode) {
        this.vnpBankCode = vnpBankCode;
    }

    public String getVnpBankTranNo() {
        return vnpBankTranNo;
    }

    public void setVnpBankTranNo(String vnpBankTranNo) {
        this.vnpBankTranNo = vnpBankTranNo;
    }

    public String getVnpResponseCode() {
        return vnpResponseCode;
    }

    public void setVnpResponseCode(String vnpResponseCode) {
        this.vnpResponseCode = vnpResponseCode;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public int getAdditionalConverts() {
        return additionalConverts;
    }

    public void setAdditionalConverts(int additionalConverts) {
        this.additionalConverts = additionalConverts;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PaymentHistory{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", planType='" + planType + '\'' +
                ", additionalConverts=" + additionalConverts +
                ", paymentDate=" + paymentDate +
                '}';
    }
}