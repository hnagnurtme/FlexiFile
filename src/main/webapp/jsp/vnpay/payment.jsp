<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.bean.User" %>
<%
    User user = (User) session.getAttribute("user");
    boolean isLoggedIn = user != null;
%>
<html>
<head>
    <title>Nâng cấp tài khoản - FlexiFile</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 1000px;
            margin: 50px auto;
            padding: 20px;
        }
        .plans-container {
            display: flex;
            gap: 20px;
            margin: 30px 0;
        }
        .plan-card {
            border: 2px solid #ddd;
            border-radius: 10px;
            padding: 20px;
            flex: 1;
            text-align: center;
            transition: all 0.3s;
        }
        .plan-card:hover {
            border-color: #007bff;
            transform: translateY(-5px);
        }
        .plan-card.pro {
            border-color: #ffc107;
            background: #fffdf5;
        }
        .plan-name {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .plan-price {
            font-size: 28px;
            color: #007bff;
            margin: 15px 0;
        }
        .plan-features {
            list-style: none;
            padding: 0;
            margin: 20px 0;
        }
        .plan-features li {
            padding: 5px 0;
            border-bottom: 1px solid #eee;
        }
        .btn-upgrade {
            background: #28a745;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            width: 100%;
        }
        .btn-upgrade:hover {
            background: #218838;
        }
        .current-plan {
            background: #e8f5e8;
            padding: 10px;
            border-radius: 5px;
            margin: 10px 0;
        }
    </style>
</head>
<body>
    <h1>Nâng cấp tài khoản FlexiFile</h1>
    
    <% if (isLoggedIn) { %>
        <div class="current-plan">
            <strong>Tài khoản hiện tại:</strong> 
            <%= user.getPlanType() != null ? user.getPlanType() : "FREE" %> |
            <strong>Lượt convert còn lại:</strong> <%= user.getRemainingConverts() %> |
            <strong>Vai trò:</strong> <%= user.getRole() %>
        </div>
    <% } else { %>
        <div style="background: #fff3cd; padding: 15px; border-radius: 5px; margin: 20px 0;">
            <strong>Lưu ý:</strong> Bạn cần <a href="${pageContext.request.contextPath}/login">đăng nhập</a> để nâng cấp tài khoản.
        </div>
    <% } %>
    
    <div class="plans-container">
        <!-- Gói BASIC -->
        <div class="plan-card">
            <div class="plan-name">BASIC</div>
            <div class="plan-price">50,000₫</div>
            <ul class="plan-features">
                <li>+50 lượt convert</li>
                <li>Convert file cơ bản</li>
                <li>Hỗ trợ 10 định dạng</li>
                <li>File tối đa 10MB</li>
            </ul>
            <form action="${pageContext.request.contextPath}/payment" method="post">
                <input type="hidden" name="orderId" value="BASIC<%= System.currentTimeMillis() %>">
                <input type="hidden" name="amount" value="50000">
                <input type="hidden" name="orderDesc" value="Nang cap BASIC - 50 converts">
                <input type="hidden" name="bankCode" value="">
                <input type="hidden" name="language" value="vn">
                <button type="submit" class="btn-upgrade" <%= !isLoggedIn ? "disabled" : "" %>>Nâng cấp ngay</button>
            </form>
        </div>

        <!-- Gói PREMIUM -->
        <div class="plan-card">
            <div class="plan-name">PREMIUM</div>
            <div class="plan-price">100,000₫</div>
            <ul class="plan-features">
                <li>+200 lượt convert</li>
                <li>Tất cả tính năng BASIC</li>
                <li>Hỗ trợ 25 định dạng</li>
                <li>File tối đa 50MB</li>
                <li>Xử lý ưu tiên</li>
            </ul>
            <form action="${pageContext.request.contextPath}/payment" method="post">
                <input type="hidden" name="orderId" value="PREMIUM<%= System.currentTimeMillis() %>">
                <input type="hidden" name="amount" value="100000">
                <input type="hidden" name="orderDesc" value="Nang cap PREMIUM - 200 converts">
                <input type="hidden" name="bankCode" value="">
                <input type="hidden" name="language" value="vn">
                <button type="submit" class="btn-upgrade" <%= !isLoggedIn ? "disabled" : "" %>>Nâng cấp ngay</button>
            </form>
        </div>

        <!-- Gói PRO -->
        <div class="plan-card pro">
            <div class="plan-name">PRO</div>
            <div class="plan-price">200,000₫</div>
            <ul class="plan-features">
                <li>+1000 lượt convert</li>
                <li>Tất cả tính năng PREMIUM</li>
                <li>Hỗ trợ 50+ định dạng</li>
                <li>File tối đa 100MB</li>
                <li>Xử lý ưu tiên cao</li>
                <li>Hỗ trợ 24/7</li>
            </ul>
            <form action="${pageContext.request.contextPath}/payment" method="post">
                <input type="hidden" name="orderId" value="PRO<%= System.currentTimeMillis() %>">
                <input type="hidden" name="amount" value="200000">
                <input type="hidden" name="orderDesc" value="Nang cap PRO - 1000 converts">
                <input type="hidden" name="bankCode" value="">
                <input type="hidden" name="language" value="vn">
                <button type="submit" class="btn-upgrade" <%= !isLoggedIn ? "disabled" : "" %>>Nâng cấp ngay</button>
            </form>
        </div>
    </div>

    <div style="margin-top: 30px; text-align: center;">
        <h3>Custom Package</h3>
        <form action="${pageContext.request.contextPath}/payment" method="post" style="max-width: 400px; margin: 0 auto;">
            <div style="margin-bottom: 15px;">
                <label>Số lượt convert mong muốn:</label>
                <input type="number" name="amount" min="10000" max="1000000" step="10000" value="100000" required 
                       onchange="updateCustomDesc(this.value)">
            </div>
            <input type="hidden" name="orderId" value="CUSTOM<%= System.currentTimeMillis() %>">
            <input type="hidden" name="orderDesc" id="customDesc" value="Custom package - 100000 VND">
            <input type="hidden" name="bankCode" value="">
            <input type="hidden" name="language" value="vn">
            <button type="submit" class="btn-upgrade" <%= !isLoggedIn ? "disabled" : "" %>>Thanh toán custom</button>
        </form>
    </div>

    <script>
        function updateCustomDesc(amount) {
            var converts = Math.floor(amount / 2000); 
            document.getElementById('customDesc').value = 'Custom package - ' + converts + ' converts';
        }
    </script>
</body>
</html>