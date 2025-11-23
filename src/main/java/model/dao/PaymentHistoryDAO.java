package model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;

import model.bean.PaymentHistory;
import util.FirebaseUtil;

public class PaymentHistoryDAO {

    private static final String COLLECTION_NAME = "payment_history";
    private Firestore db = FirebaseUtil.getFirestore();

    /**
     * Lưu payment history vào Firestore
     */
    public String savePaymentHistory(PaymentHistory payment) {
        try {
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("userId", payment.getUserId());
            paymentData.put("orderId", payment.getOrderId());
            paymentData.put("amount", payment.getAmount());
            paymentData.put("status", payment.getStatus());
            paymentData.put("vnpTransactionNo", payment.getVnpTransactionNo());
            paymentData.put("vnpBankCode", payment.getVnpBankCode());
            paymentData.put("vnpBankTranNo", payment.getVnpBankTranNo());
            paymentData.put("vnpResponseCode", payment.getVnpResponseCode());
            paymentData.put("orderInfo", payment.getOrderInfo());
            paymentData.put("planType", payment.getPlanType());
            paymentData.put("additionalConverts", payment.getAdditionalConverts());
            paymentData.put("paymentDate", payment.getPaymentDate());
            paymentData.put("createdAt", payment.getCreatedAt());

            DocumentReference docRef = db.collection(COLLECTION_NAME).document();
            docRef.set(paymentData).get();

            System.out.println("Payment history saved with ID: " + docRef.getId());
            return docRef.getId();

        } catch (Exception e) {
            System.err.println("Error saving payment history: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy payment history theo ID
     */
    public PaymentHistory getPaymentById(String paymentId) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION_NAME)
                    .document(paymentId)
                    .get()
                    .get();

            if (!doc.exists()) {
                return null;
            }

            return mapDocumentToPayment(doc);

        } catch (Exception e) {
            System.err.println("Error getting payment by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy payment history theo orderId (vnp_TxnRef)
     */
    public PaymentHistory getPaymentByOrderId(String orderId) {
        try {
            QuerySnapshot querySnapshot = db.collection(COLLECTION_NAME)
                    .whereEqualTo("orderId", orderId)
                    .get()
                    .get();

            if (querySnapshot.isEmpty()) {
                return null;
            }

            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
            return mapDocumentToPayment(doc);

        } catch (Exception e) {
            System.err.println("Error getting payment by order ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả payment history của user
     */
    public List<PaymentHistory> getPaymentsByUserId(String userId) {
        try {
            System.out.println("DEBUG PaymentHistoryDAO: Getting payments for userId: " + userId);
            QuerySnapshot querySnapshot;

            // Thử query với orderBy trước (cần index)
            try {
                querySnapshot = db.collection(COLLECTION_NAME)
                        .whereEqualTo("userId", userId)
                        .orderBy("paymentDate", Direction.DESCENDING)
                        .get()
                        .get();
                System.out.println("DEBUG: Query with orderBy successful");
            } catch (Exception indexError) {
                // Nếu không có index, query đơn giản và sắp xếp trong code
                System.out.println("DEBUG: Index not found, using in-memory sorting. Error: " + indexError.getMessage());
                querySnapshot = db.collection(COLLECTION_NAME)
                        .whereEqualTo("userId", userId)
                        .get()
                        .get();
            }

            System.out.println("DEBUG: Query returned " + querySnapshot.size() + " documents");

            List<PaymentHistory> paymentList = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                PaymentHistory payment = mapDocumentToPayment(doc);
                System.out.println("DEBUG: Mapped payment - ID: " + payment.getId() + ", OrderID: " + payment.getOrderId() + ", UserId: " + payment.getUserId());
                paymentList.add(payment);
            }

            // Sắp xếp theo paymentDate giảm dần (mới nhất trước)
            paymentList.sort((p1, p2) -> {
                if (p1.getPaymentDate() == null && p2.getPaymentDate() == null) return 0;
                if (p1.getPaymentDate() == null) return 1;
                if (p2.getPaymentDate() == null) return -1;
                return p2.getPaymentDate().compareTo(p1.getPaymentDate());
            });

            System.out.println("DEBUG: Returning " + paymentList.size() + " payments after sorting");
            return paymentList;

        } catch (Exception e) {
            System.err.println("Error getting payments by user ID: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy tất cả successful payments của user
     */
    public List<PaymentHistory> getSuccessfulPaymentsByUserId(String userId) {
        try {
            QuerySnapshot querySnapshot;

            // Thử query với orderBy trước (cần index)
            try {
                querySnapshot = db.collection(COLLECTION_NAME)
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("status", "SUCCESS")
                        .orderBy("paymentDate", Direction.DESCENDING)
                        .get()
                        .get();
            } catch (Exception indexError) {
                // Nếu không có index, query đơn giản và sắp xếp trong code
                System.out.println("Index not found for successful payments, using in-memory sorting");
                querySnapshot = db.collection(COLLECTION_NAME)
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("status", "SUCCESS")
                        .get()
                        .get();
            }

            List<PaymentHistory> paymentList = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                paymentList.add(mapDocumentToPayment(doc));
            }

            // Sắp xếp theo paymentDate giảm dần (mới nhất trước)
            paymentList.sort((p1, p2) -> {
                if (p1.getPaymentDate() == null && p2.getPaymentDate() == null) return 0;
                if (p1.getPaymentDate() == null) return 1;
                if (p2.getPaymentDate() == null) return -1;
                return p2.getPaymentDate().compareTo(p1.getPaymentDate());
            });

            return paymentList;

        } catch (Exception e) {
            System.err.println("Error getting successful payments: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Cập nhật status của payment
     */
    public boolean updatePaymentStatus(String paymentId, String status) {
        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", status);

            db.collection(COLLECTION_NAME)
                    .document(paymentId)
                    .update(updates)
                    .get();

            System.out.println("Payment status updated: " + paymentId);
            return true;

        } catch (Exception e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra xem orderId đã tồn tại chưa (tránh duplicate payment)
     */
    public boolean isOrderIdExist(String orderId) {
        try {
            QuerySnapshot querySnapshot = db.collection(COLLECTION_NAME)
                    .whereEqualTo("orderId", orderId)
                    .get()
                    .get();

            return !querySnapshot.isEmpty();

        } catch (Exception e) {
            System.err.println("Error checking order ID: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Map Firestore document to PaymentHistory object
     */
    private PaymentHistory mapDocumentToPayment(DocumentSnapshot doc) {
        PaymentHistory payment = new PaymentHistory();
        payment.setId(doc.getId());
        payment.setUserId(doc.getString("userId"));
        payment.setOrderId(doc.getString("orderId"));

        Double amount = doc.getDouble("amount");
        payment.setAmount(amount != null ? amount : 0.0);

        payment.setStatus(doc.getString("status"));
        payment.setVnpTransactionNo(doc.getString("vnpTransactionNo"));
        payment.setVnpBankCode(doc.getString("vnpBankCode"));
        payment.setVnpBankTranNo(doc.getString("vnpBankTranNo"));
        payment.setVnpResponseCode(doc.getString("vnpResponseCode"));
        payment.setOrderInfo(doc.getString("orderInfo"));
        payment.setPlanType(doc.getString("planType"));

        Long additionalConverts = doc.getLong("additionalConverts");
        payment.setAdditionalConverts(additionalConverts != null ? additionalConverts.intValue() : 0);

        payment.setPaymentDate(doc.getDate("paymentDate"));
        payment.setCreatedAt(doc.getDate("createdAt"));

        return payment;
    }
}
