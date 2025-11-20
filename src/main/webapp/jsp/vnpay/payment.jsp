<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Thanh toán VNPay</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input, select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <h1>Thanh toán VNPay</h1>
    
    <form action="${pageContext.request.contextPath}/payment" method="post">
        <div class="form-group">
            <label for="orderId">Mã đơn hàng:</label>
            <input type="text" id="orderId" name="orderId" value="DH<%= System.currentTimeMillis() %>" required>
        </div>
        
        <div class="form-group">
            <label for="amount">Số tiền (VND):</label>
            <input type="number" id="amount" name="amount" value="100000" min="10000" required>
        </div>
        
        <div class="form-group">
            <label for="orderDesc">Mô tả đơn hàng:</label>
            <input type="text" id="orderDesc" name="orderDesc" value="Thanh toán đơn hàng" required>
        </div>
        
        <div class="form-group">
            <label for="bankCode">Phương thức thanh toán:</label>
            <select id="bankCode" name="bankCode">
                <option value="">-- Chọn phương thức --</option>
                <option value="VNBANK">Thẻ ATM/Tài khoản ngân hàng</option>
                <option value="INTCARD">Thẻ Visa/MasterCard</option>
                <option value="VISA">Thẻ Visa</option>
                <option value="MASTERCARD">Thẻ MasterCard</option>
                <option value="JCB">Thẻ JCB</option>
                <option value="VNPAYQR">VNPay QR</option>
            </select>
        </div>
        
        <div class="form-group">
            <label for="language">Ngôn ngữ:</label>
            <select id="language" name="language">
                <option value="vn">Tiếng Việt</option>
                <option value="en">English</option>
            </select>
        </div>
        
        <button type="submit">Thanh toán với VNPay</button>
    </form>
    
    <div style="margin-top: 20px;">
        <a href="${pageContext.request.contextPath}">← Quay lại trang chủ</a>
    </div>
</body>
</html>