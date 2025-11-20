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
import util.VNPayUtil;

@WebServlet("/payment/vnpay_return")
public class VNPayReturnServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            System.out.println("=== VNPay Return Servlet Called ===");
            
            // Log all parameters for debugging
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                System.out.println(paramName + ": " + paramValue);
            }
            
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
            
            // Remove secure hash type and secure hash from fields for verification
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            
            // Verify checksum
            boolean checksumValid = VNPayUtil.verifyReturn(fields, vnp_SecureHash);
            
            if (checksumValid) {
                String responseCode = request.getParameter("vnp_ResponseCode");
                String orderId = request.getParameter("vnp_TxnRef");
                String amount = request.getParameter("vnp_Amount");
                String bankCode = request.getParameter("vnp_BankCode");
                String payDate = request.getParameter("vnp_PayDate");
                String transactionNo = request.getParameter("vnp_TransactionNo");
                String orderInfo = request.getParameter("vnp_OrderInfo");
                String bankTranNo = request.getParameter("vnp_BankTranNo");
                String cardType = request.getParameter("vnp_CardType");
                String tmnCode = request.getParameter("vnp_TmnCode");
                
                // Set attributes for JSP
                request.setAttribute("orderId", orderId);
                request.setAttribute("amount", amount);
                request.setAttribute("bankCode", bankCode);
                request.setAttribute("payDate", payDate);
                request.setAttribute("transactionNo", transactionNo);
                request.setAttribute("responseCode", responseCode);
                request.setAttribute("orderInfo", orderInfo);
                request.setAttribute("bankTranNo", bankTranNo);
                request.setAttribute("cardType", cardType);
                request.setAttribute("tmnCode", tmnCode);
                
                if ("00".equals(responseCode)) {
                    request.setAttribute("message", "Thanh toán thành công");
                    request.setAttribute("status", "success");
                    System.out.println("Payment SUCCESS for order: " + orderId);
                } else {
                    request.setAttribute("message", "Thanh toán thất bại");
                    request.setAttribute("status", "error");
                    System.out.println("Payment FAILED for order: " + orderId + ", Code: " + responseCode);
                }
            } else {
                request.setAttribute("message", "Chữ ký không hợp lệ");
                request.setAttribute("status", "error");
                System.out.println("INVALID SIGNATURE");
            }
            
            request.getRequestDispatcher("../jsp/vnpay/vnpay_return.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý kết quả thanh toán: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }
}