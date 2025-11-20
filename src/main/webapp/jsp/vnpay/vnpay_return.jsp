<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%
    String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
    String vnp_TxnRef = request.getParameter("vnp_TxnRef");
    String vnp_Amount = request.getParameter("vnp_Amount");
    String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
    String vnp_BankCode = request.getParameter("vnp_BankCode");
    String vnp_PayDate = request.getParameter("vnp_PayDate");
    String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
    
    // Xác định trạng thái thanh toán
    boolean isSuccess = "00".equals(vnp_ResponseCode);
    String statusText = isSuccess ? "THÀNH CÔNG" : "THẤT BẠI";
    String statusClass = isSuccess ? "success" : "error";
%>
<html>
<head>
    <title>Kết quả thanh toán</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .result-container {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin: 20px 0;
        }
        .success {
            border-left: 5px solid #28a745;
            background-color: #f8fff9;
        }
        .error {
            border-left: 5px solid #dc3545;
            background-color: #fff8f8;
        }
        .status {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 20px;
        }
        .success .status { color: #28a745; }
        .error .status { color: #dc3545; }
        .info-table {
            width: 100%;
            border-collapse: collapse;
        }
        .info-table td {
            padding: 8px;
            border-bottom: 1px solid #eee;
        }
        .info-table td:first-child {
            font-weight: bold;
            width: 40%;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin: 5px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-success {
            background-color: #28a745;
        }
        .btn-success:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>
    <h1>Kết quả thanh toán VNPay</h1>
    
    <div class="result-container <%= statusClass %>">
        <div class="status">
            <%= statusText %>
        </div>
        
        <table class="info-table">
            <tr>
                <td>Mã đơn hàng:</td>
                <td><%= vnp_TxnRef != null ? vnp_TxnRef : "N/A" %></td>
            </tr>
            <tr>
                <td>Mô tả:</td>
                <td><%= vnp_OrderInfo != null ? vnp_OrderInfo : "N/A" %></td>
            </tr>
            <tr>
                <td>Số tiền:</td>
                <td>
                    <% if (vnp_Amount != null) { 
                         long amount = Long.parseLong(vnp_Amount) / 100;
                    %>
                         <%= String.format("%,d", amount) %> VND
                    <% } else { %>
                         N/A
                    <% } %>
                </td>
            </tr>
            <tr>
                <td>Mã giao dịch:</td>
                <td><%= vnp_TransactionNo != null ? vnp_TransactionNo : "N/A" %></td>
            </tr>
            <tr>
                <td>Ngân hàng:</td>
                <td><%= vnp_BankCode != null ? vnp_BankCode : "N/A" %></td>
            </tr>
            <tr>
                <td>Thời gian thanh toán:</td>
                <td>
                    <% if (vnp_PayDate != null) { 
                         // Format: yyyyMMddHHmmss
                         String dateStr = vnp_PayDate;
                         if (dateStr.length() >= 14) {
                             String formattedDate = dateStr.substring(6, 8) + "/" + 
                                                   dateStr.substring(4, 6) + "/" + 
                                                   dateStr.substring(0, 4) + " " + 
                                                   dateStr.substring(8, 10) + ":" + 
                                                   dateStr.substring(10, 12) + ":" + 
                                                   dateStr.substring(12, 14);
                    %>
                         <%= formattedDate %>
                    <%   } else { %>
                         <%= dateStr %>
                    <%   }
                       } else { %>
                         N/A
                    <% } %>
                </td>
            </tr>
            <tr>
                <td>Mã kết quả:</td>
                <td><%= vnp_ResponseCode != null ? vnp_ResponseCode : "N/A" %></td>
            </tr>
        </table>
    </div>
    
    <div style="margin-top: 20px;">
        
        <a href="${pageContext.request.contextPath}/payment" class="btn">
            Thanh toán lại
        </a>
        
        <a href="${pageContext.request.contextPath}" class="btn">
            ← Về trang chủ
        </a>
    </div>
    
    <!-- Hiển thị thông báo lỗi chi tiết nếu có -->
    <% if (!isSuccess && vnp_ResponseCode != null) { %>
    <div style="margin-top: 20px; padding: 15px; background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px;">
        <strong>Thông báo lỗi:</strong><br>
        <%
            String errorMessage = "";
            switch(vnp_ResponseCode) {
                case "07": errorMessage = "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường)."; break;
                case "09": errorMessage = "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng."; break;
                case "10": errorMessage = "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần."; break;
                case "11": errorMessage = "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch."; break;
                case "12": errorMessage = "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa."; break;
                case "13": errorMessage = "Giao dịch không thành công do: Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch."; break;
                case "24": errorMessage = "Giao dịch không thành công do: Khách hàng hủy giao dịch."; break;
                case "51": errorMessage = "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch."; break;
                case "65": errorMessage = "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày."; break;
                case "75": errorMessage = "Ngân hàng thanh toán đang bảo trì."; break;
                case "79": errorMessage = "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch."; break;
                default: errorMessage = "Giao dịch không thành công. Vui lòng thử lại hoặc liên hệ hỗ trợ."; break;
            }
        %>
        <%= errorMessage %>
    </div>
    <% } %>
</body>
</html>