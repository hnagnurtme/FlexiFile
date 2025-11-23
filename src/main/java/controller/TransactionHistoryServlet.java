package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/transactions")
public class TransactionHistoryServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Lấy danh sách giao dịch từ database
            List<Map<String, String>> transactions = getTransactions();
            
            request.setAttribute("transactions", transactions);
            request.getRequestDispatcher("/payment/transactions.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải danh sách giao dịch");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    private List<Map<String, String>> getTransactions() {
        // TODO: Implement database query logic
        List<Map<String, String>> transactions = new ArrayList<>();
        
        // Mock data - thực tế sẽ query từ database
        Map<String, String> transaction1 = new HashMap<>();
        transaction1.put("orderId", "DH123456");
        transaction1.put("amount", "100000");
        transaction1.put("status", "00");
        transaction1.put("bankCode", "VNBANK");
        transaction1.put("createDate", "2024-01-15 10:30:00");
        transactions.add(transaction1);
        
        Map<String, String> transaction2 = new HashMap<>();
        transaction2.put("orderId", "DH123457");
        transaction2.put("amount", "200000");
        transaction2.put("status", "01");
        transaction2.put("bankCode", "INTCARD");
        transaction2.put("createDate", "2024-01-15 11:30:00");
        transactions.add(transaction2);
        
        return transactions;
    }
}