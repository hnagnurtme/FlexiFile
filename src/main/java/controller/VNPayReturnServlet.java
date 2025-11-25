package controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.PaymentHistory;
import model.bean.User;
import model.bo.PaymentBO;
import model.bo.AuthBO;
import util.VNPayUtil;

@WebServlet("/payment/vnpay_return")
public class VNPayReturnServlet extends HttpServlet {

    private PaymentBO paymentBO;
    private AuthBO authBO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.paymentBO = new PaymentBO();
        this.authBO = new AuthBO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== VNPayReturnServlet STARTED ===");

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            // Log tất cả parameters từ VNPay
            System.out.println("=== VNPay Parameters ===");
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                System.out.println(paramName + " = " + paramValue);
            }
            
            // Lấy tất cả parameters vào map để verify signature
            Map<String, String> fields = new HashMap<>();
            Enumeration<String> params = request.getParameterNames();

            while (params.hasMoreElements()) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            System.out.println("Received SecureHash: " + vnp_SecureHash);

            // Remove secure hash type and secure hash from fields for verification
            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");

            // Debug: In ra hash data
            String hashData = VNPayUtil.hashAllFields(fields);
            System.out.println("Calculated hash: " + hashData);
            System.out.println("Received hash:   " + vnp_SecureHash);

            // Verify checksum
            boolean checksumValid = VNPayUtil.verifyReturn(fields, vnp_SecureHash);
            System.out.println("Checksum valid: " + checksumValid);
            
            if (checksumValid) {
                String responseCode = request.getParameter("vnp_ResponseCode");
                String orderId = request.getParameter("vnp_TxnRef");
                String amount = request.getParameter("vnp_Amount");
                String bankCode = request.getParameter("vnp_BankCode");
                String bankTranNo = request.getParameter("vnp_BankTranNo");
                String payDate = request.getParameter("vnp_PayDate");
                String transactionNo = request.getParameter("vnp_TransactionNo");
                String orderInfo = request.getParameter("vnp_OrderInfo");

                System.out.println("Payment response - Order: " + orderId + ", Code: " + responseCode);

                // Kiểm tra user đã đăng nhập chưa
                if (user == null) {
                    System.err.println("User not logged in!");
                    request.setAttribute("errorMessage", "Vui lòng đăng nhập để tiếp tục");
                    request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
                    return;
                }

                PaymentHistory payment = null;

                if ("00".equals(responseCode)) {
                    // Thanh toán thành công - Lưu vào database và cập nhật user
                    System.out.println("Payment SUCCESS - Processing...");

                    payment = paymentBO.processSuccessfulPayment(
                        user.getId(),
                        orderId,
                        amount,
                        transactionNo,
                        bankCode,
                        bankTranNo,
                        responseCode,
                        orderInfo,
                        payDate
                    );

                    if (payment != null) {
                        System.out.println("Payment processed successfully: " + payment.getId());

                        // Cập nhật user trong session với thông tin mới nhất từ database
                        User updatedUser = authBO.getUserByIdBO(user.getId());
                        if (updatedUser != null) {
                            session.setAttribute("user", updatedUser);
                            System.out.println("Session user updated with new plan: " + updatedUser.getPlanType());
                        }

                        request.setAttribute("message", "Thanh toán thành công");
                        request.setAttribute("status", "success");
                        request.setAttribute("payment", payment);
                    } else {
                        System.err.println("Failed to process payment");
                        request.setAttribute("message", "Lỗi xử lý thanh toán");
                        request.setAttribute("status", "error");
                    }

                } else {
                    // Thanh toán thất bại - Chỉ lưu lịch sử
                    System.out.println("Payment FAILED - Code: " + responseCode);

                    payment = paymentBO.processFailedPayment(
                        user.getId(),
                        orderId,
                        amount,
                        transactionNo,
                        bankCode,
                        responseCode,
                        orderInfo,
                        payDate
                    );

                    request.setAttribute("message", "Thanh toán thất bại");
                    request.setAttribute("status", "error");
                    request.setAttribute("payment", payment);
                }

                // Set các attributes cho JSP
                request.setAttribute("orderId", orderId);
                request.setAttribute("amount", amount);
                request.setAttribute("bankCode", bankCode);
                request.setAttribute("bankTranNo", bankTranNo);
                request.setAttribute("payDate", payDate);
                request.setAttribute("transactionNo", transactionNo);
                request.setAttribute("responseCode", responseCode);
                request.setAttribute("orderInfo", orderInfo);

            } else {
                request.setAttribute("message", "Chữ ký không hợp lệ");
                request.setAttribute("status", "error");
                System.out.println("INVALID SIGNATURE");
            }
            
            System.out.println("Forwarding to JSP...");
            request.getRequestDispatcher("/jsp/vnpay/vnpay_return.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("ERROR in VNPayReturnServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý kết quả thanh toán: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }
}