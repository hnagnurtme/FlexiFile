package model.bean;

import java.util.Date;

public class PaymentHistory {
    private String id;          // Firestore document ID
    private String userId;      // liên kết User
    private double amount;
    private String status;      // SUCCESS / FAILED / PENDING
    private String vnpTransactionNo; // VNPay transaction code
    private Date paymentDate;

    // Constructors, getters, setters
}