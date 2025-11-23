package model.bo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.bean.PaymentHistory;
import model.dao.PaymentHistoryDAO;
import model.dao.AuthDAO;

public class PaymentBO {

    private PaymentHistoryDAO paymentHistoryDAO;
    private AuthDAO authDAO;

    public PaymentBO() {
        this.paymentHistoryDAO = new PaymentHistoryDAO();
        this.authDAO = new AuthDAO();
    }

    /**
     * Xử lý thanh toán thành công từ VNPay
     * - Lưu payment history
     * - Cập nhật user (plan type, remaining converts)
     * - Trả về payment history đã lưu
     */
    public PaymentHistory processSuccessfulPayment(
            String userId,
            String orderId,
            String amountStr,
            String vnpTransactionNo,
            String vnpBankCode,
            String vnpBankTranNo,
            String vnpResponseCode,
            String orderInfo,
            String payDateStr) {

        try {
            // Kiểm tra xem orderId đã tồn tại chưa (tránh duplicate)
            if (paymentHistoryDAO.isOrderIdExist(orderId)) {
                System.out.println("Order ID already exists: " + orderId);
                return paymentHistoryDAO.getPaymentByOrderId(orderId);
            }

            // Parse amount (VNPay trả về amount * 100)
            long amountLong = Long.parseLong(amountStr) / 100;

            // Xác định plan type và số lượt convert dựa trên amount
            String planType = determinePlanType(amountLong);
            int additionalConverts = calculateAdditionalConverts(amountLong);

            // Parse payment date
            Date paymentDate = parseVNPayDate(payDateStr);

            // Tạo payment history object
            PaymentHistory payment = new PaymentHistory();
            payment.setUserId(userId);
            payment.setOrderId(orderId);
            payment.setAmount(amountLong);
            payment.setStatus("SUCCESS");
            payment.setVnpTransactionNo(vnpTransactionNo);
            payment.setVnpBankCode(vnpBankCode);
            payment.setVnpBankTranNo(vnpBankTranNo);
            payment.setVnpResponseCode(vnpResponseCode);
            payment.setOrderInfo(orderInfo);
            payment.setPlanType(planType);
            payment.setAdditionalConverts(additionalConverts);
            payment.setPaymentDate(paymentDate);
            payment.setCreatedAt(new Date());

            // Lưu payment history
            String paymentId = paymentHistoryDAO.savePaymentHistory(payment);
            payment.setId(paymentId);

            // Cập nhật user
            boolean userUpdated = updateUserAfterPayment(userId, planType, additionalConverts);

            if (userUpdated) {
                System.out.println("User updated successfully after payment: " + userId);
            } else {
                System.err.println("Failed to update user after payment: " + userId);
            }

            return payment;

        } catch (Exception e) {
            System.err.println("Error processing successful payment: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Xử lý thanh toán thất bại
     */
    public PaymentHistory processFailedPayment(
            String userId,
            String orderId,
            String amountStr,
            String vnpTransactionNo,
            String vnpBankCode,
            String vnpResponseCode,
            String orderInfo,
            String payDateStr) {

        try {
            // Parse amount
            long amountLong = amountStr != null ? Long.parseLong(amountStr) / 100 : 0;

            // Parse payment date
            Date paymentDate = parseVNPayDate(payDateStr);

            // Tạo payment history object
            PaymentHistory payment = new PaymentHistory();
            payment.setUserId(userId);
            payment.setOrderId(orderId);
            payment.setAmount(amountLong);
            payment.setStatus("FAILED");
            payment.setVnpTransactionNo(vnpTransactionNo);
            payment.setVnpBankCode(vnpBankCode);
            payment.setVnpResponseCode(vnpResponseCode);
            payment.setOrderInfo(orderInfo);
            payment.setPaymentDate(paymentDate);
            payment.setCreatedAt(new Date());

            // Lưu payment history
            String paymentId = paymentHistoryDAO.savePaymentHistory(payment);
            payment.setId(paymentId);

            return payment;

        } catch (Exception e) {
            System.err.println("Error processing failed payment: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy payment history của user
     */
    public List<PaymentHistory> getUserPaymentHistory(String userId) {
        return paymentHistoryDAO.getPaymentsByUserId(userId);
    }

    /**
     * Lấy successful payments của user
     */
    public List<PaymentHistory> getUserSuccessfulPayments(String userId) {
        return paymentHistoryDAO.getSuccessfulPaymentsByUserId(userId);
    }

    /**
     * Lấy payment theo order ID
     */
    public PaymentHistory getPaymentByOrderId(String orderId) {
        return paymentHistoryDAO.getPaymentByOrderId(orderId);
    }

    /**
     * Xác định plan type dựa trên số tiền
     */
    private String determinePlanType(long amount) {
        if (amount >= 200000) {
            return "PRO";
        } else if (amount >= 100000) {
            return "PREMIUM";
        } else if (amount >= 50000) {
            return "BASIC";
        } else {
            return "CUSTOM";
        }
    }

    /**
     * Tính số lượt convert được thêm dựa trên số tiền
     */
    private int calculateAdditionalConverts(long amount) {
        if (amount >= 200000) {
            return 1000; // PRO
        } else if (amount >= 100000) {
            return 200; // PREMIUM
        } else if (amount >= 50000) {
            return 50; // BASIC
        } else {
            // Custom: 1 VND = 0.002 converts (500 VND = 1 convert)
            return (int) (amount / 500);
        }
    }

    /**
     * Cập nhật thông tin user sau khi thanh toán thành công
     */
    private boolean updateUserAfterPayment(String userId, String planType, int additionalConverts) {
        try {
            return authDAO.updateUserAfterPayment(userId, planType, additionalConverts);
        } catch (Exception e) {
            System.err.println("Error updating user after payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parse VNPay date format (yyyyMMddHHmmss) to Date
     */
    private Date parseVNPayDate(String payDateStr) {
        if (payDateStr == null || payDateStr.isEmpty()) {
            return new Date();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            return sdf.parse(payDateStr);
        } catch (ParseException e) {
            System.err.println("Error parsing VNPay date: " + payDateStr);
            return new Date();
        }
    }
}
