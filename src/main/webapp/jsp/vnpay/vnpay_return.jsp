<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.bean.User" %>
<%@ page import="model.bean.PaymentHistory" %>
<%
    // Lấy thông tin từ request attributes (đã được servlet xử lý)
    String message = (String) request.getAttribute("message");
    String status = (String) request.getAttribute("status");
    PaymentHistory payment = (PaymentHistory) request.getAttribute("payment");

    String responseCode = (String) request.getAttribute("responseCode");
    String orderId = (String) request.getAttribute("orderId");
    String amount = (String) request.getAttribute("amount");
    String bankCode = (String) request.getAttribute("bankCode");
    String bankTranNo = (String) request.getAttribute("bankTranNo");
    String transactionNo = (String) request.getAttribute("transactionNo");
    String orderInfo = (String) request.getAttribute("orderInfo");
    String payDate = (String) request.getAttribute("payDate");

    // Xác định trạng thái
    boolean isSuccess = "success".equals(status);

    // Lấy user từ session (đã được cập nhật bởi servlet)
    User user = (User) session.getAttribute("user");

    // Log
    System.out.println("=== JSP Payment Display ===");
    System.out.println("Status: " + status);
    System.out.println("Message: " + message);
    System.out.println("Payment ID: " + (payment != null ? payment.getId() : "NULL"));
    System.out.println("User Plan: " + (user != null ? user.getPlanType() : "NULL"));
%>
<html>
<head>
    <title>Kết quả thanh toán</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            margin: 50px auto;
            max-width: 800px;
            padding: 20px;
        }
        .result { 
            padding: 20px; 
            border-radius: 8px; 
            margin: 20px 0; 
            border-left: 5px solid;
        }
        .success { 
            background: #d4edda; 
            color: #155724; 
            border-left-color: #28a745;
        }
        .error { 
            background: #f8d7da; 
            color: #721c24; 
            border-left-color: #dc3545;
        }
        .info { 
            margin: 10px 0; 
            padding: 8px;
            background: white;
            border-radius: 4px;
        }
        .upgrade-info {
            background: #e8f5e8;
            padding: 15px;
            border-radius: 5px;
            margin: 15px 0;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin: 5px;
        }
        .btn-success {
            background: #28a745;
        }
    </style>
</head>
<body>
    <h1>Kết quả thanh toán FlexiFile</h1>
    
    <div class="result <%= isSuccess ? "success" : "error" %>">
        <h2><%= message != null ? message : "Kết quả thanh toán" %></h2>

        <% if (isSuccess && user != null && payment != null) { %>
            <div class="upgrade-info">
                <h3>🎉 Tài khoản đã được nâng cấp!</h3>
                <p><strong>Gói dịch vụ:</strong> <%= payment.getPlanType() != null ? payment.getPlanType() : user.getPlanType() %></p>
                <p><strong>Lượt convert đã thêm:</strong> <%= payment.getAdditionalConverts() %> lượt</p>
                <p><strong>Lượt convert còn lại:</strong> <%= user.getRemainingConverts() %> lượt</p>
                <p><strong>Vai trò:</strong> <%= user.getRole() %></p>
            </div>
        <% } %>
        
        <div class="info">
            <strong>Mã đơn hàng:</strong> <%= orderId != null ? orderId : "N/A" %>
        </div>
        
        <div class="info">
            <strong>Mô tả:</strong> <%= orderInfo != null ? orderInfo : "N/A" %>
        </div>
        
        <% if (amount != null) { 
            long amountLong = Long.parseLong(amount) / 100;
        %>
            <div class="info">
                <strong>Số tiền:</strong> <%= String.format("%,d", amountLong) %> VND
            </div>
        <% } %>
        
        <div class="info">
            <strong>Mã giao dịch VNPay:</strong> <%= transactionNo != null ? transactionNo : "N/A" %>
        </div>
        
        <div class="info">
            <strong>Mã giao dịch Ngân hàng:</strong> <%= bankTranNo != null ? bankTranNo : "N/A" %>
        </div>
        
        <div class="info">
            <strong>Ngân hàng:</strong> <%= bankCode != null ? bankCode : "N/A" %>
        </div>
        
        <div class="info">
            <strong>Mã phản hồi:</strong> <%= responseCode != null ? responseCode : "N/A" %>
        </div>
        
        <% if (payDate != null && payDate.length() >= 14) { 
            String formattedDate = payDate.substring(6, 8) + "/" + 
                                  payDate.substring(4, 6) + "/" + 
                                  payDate.substring(0, 4) + " " + 
                                  payDate.substring(8, 10) + ":" + 
                                  payDate.substring(10, 12) + ":" + 
                                  payDate.substring(12, 14);
        %>
            <div class="info">
                <strong>Thời gian thanh toán:</strong> <%= formattedDate %>
            </div>
        <% } %>
    </div>
    
    <div style="margin-top: 20px;">
        <% if (isSuccess) { %>
            <a href="${pageContext.request.contextPath}/convert" class="btn btn-success">
                🚀 Bắt đầu Convert File
            </a>
            <a href="${pageContext.request.contextPath}/profile" class="btn">
                👤 Xem hồ sơ
            </a>
        <% } else { %>
            <a href="${pageContext.request.contextPath}/payment" class="btn">
                🔄 Thử lại thanh toán
            </a>
        <% } %>
        <a href="${pageContext.request.contextPath}/homeServlet" class="btn">
            🏠 Về trang chủ
        </a>
    </div>
    
    <!-- Debug section -->
    <% if (payment != null) { %>
        <div style="margin-top: 30px; padding: 15px; background: #f8f9fa; border-radius: 5px;">
            <h3>🔧 Thông tin chi tiết</h3>
            <p><strong>Payment ID:</strong> <%= payment.getId() %></p>
            <p><strong>User:</strong> <%= user != null ? user.getEmail() : "NULL" %></p>
            <p><strong>Status:</strong> <%= payment.getStatus() %></p>
            <p><strong>Plan Type:</strong> <%= payment.getPlanType() %></p>
            <p><strong>Amount:</strong> <%= String.format("%,d", (long)payment.getAmount()) %> VND</p>
        </div>
    <% } %>
</body>
</html>