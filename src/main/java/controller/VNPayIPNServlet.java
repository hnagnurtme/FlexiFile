package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.VNPayUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/payment/vnpay_ipn")
public class VNPayIPNServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
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
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            
            // Verify checksum
            boolean checksumValid = VNPayUtil.verifyReturn(fields, vnp_SecureHash);
            
            if (checksumValid) {
                String orderId = request.getParameter("vnp_TxnRef");
                String responseCode = request.getParameter("vnp_ResponseCode");
                String amount = request.getParameter("vnp_Amount");
                String bankCode = request.getParameter("vnp_BankCode");
                String payDate = request.getParameter("vnp_PayDate");
                String transactionNo = request.getParameter("vnp_TransactionNo");
                
                // Cập nhật trạng thái đơn hàng trong database
                boolean updateSuccess = updateOrderStatus(orderId, responseCode, transactionNo);
                
                if (updateSuccess) {
                    // Trả về kết quả thành công cho VNPay
                    out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                } else {
                    out.print("{\"RspCode\":\"99\",\"Message\":\"Update Order Fail\"}");
                }
            } else {
                out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}");
        } finally {
            out.flush();
            out.close();
        }
    }
    
    private boolean updateOrderStatus(String orderId, String responseCode, String transactionNo) {
        // TODO: Implement database update logic
        try {
            System.out.println("Updating order status - Order: " + orderId + 
                             ", Status: " + responseCode + 
                             ", Transaction: " + transactionNo);
            
            // Code thực tế sẽ cập nhật database
            /*
            Connection conn = // get connection
            String status = "00".equals(responseCode) ? "PAID" : "FAILED";
            String sql = "UPDATE orders SET status = ?, transaction_no = ?, updated_at = NOW() WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, transactionNo);
            stmt.setString(3, orderId);
            int rows = stmt.executeUpdate();
            return rows > 0;
            */
            
            return true; // Tạm thời return true
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}