package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.VNPayUtil;

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {
    
    // Hiển thị form thanh toán
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/vnpay/payment.jsp").forward(request, response);
    }
    
    // Xử lý tạo thanh toán
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String orderId = request.getParameter("orderId");
            String amountStr = request.getParameter("amount");
            String orderDesc = request.getParameter("orderDesc");
            String bankCode = request.getParameter("bankCode");
            String language = request.getParameter("language");
            
            // Validate input
            if (orderId == null || orderId.isEmpty() || 
                amountStr == null || amountStr.isEmpty()) {
                request.setAttribute("errorMessage", "Thiếu thông tin bắt buộc");
                request.getRequestDispatcher("/payment/payment.jsp").forward(request, response);
                return;
            }
            
            long amount = Long.parseLong(amountStr);
            
            // Tạo URL thanh toán VNPay
            String paymentUrl = VNPayUtil.createPaymentURL(
                orderId, 
                amount, 
                orderDesc, 
                bankCode, 
                language, 
                request
            );
            
            // Chuyển hướng đến VNPay
            response.sendRedirect(paymentUrl);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}