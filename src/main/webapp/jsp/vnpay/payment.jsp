<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.bean.User" %>
<%
    User user = (User) session.getAttribute("user");
    boolean isLoggedIn = user != null;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nâng cấp tài khoản - FlexiFile</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .header {
            text-align: center;
            color: #1976D2;
            margin-bottom: 30px;
        }

        .header h1 {
            font-size: 42px;
            margin-bottom: 10px;
            text-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .header p {
            font-size: 18px;
            color: #555;
        }

        .nav-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: white;
            padding: 15px 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }

        .nav-bar strong {
            color: #1976D2;
            font-size: 18px;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            transition: all 0.3s;
        }

        .btn-primary {
            background: #1976D2;
            color: white;
        }

        .btn-outline {
            background: white;
            color: #1976D2;
            border: 2px solid #1976D2;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
        }

        .current-plan {
            background: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.1);
            display: flex;
            justify-content: space-around;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
        }

        .plan-stat {
            text-align: center;
        }

        .plan-stat-value {
            font-size: 24px;
            font-weight: bold;
            color: #1976D2;
        }

        .plan-stat-label {
            color: #666;
            font-size: 14px;
            margin-top: 5px;
        }

        .warning-box {
            background: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .warning-box i {
            font-size: 24px;
            color: #856404;
        }

        .plans-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .plan-card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            text-align: center;
            transition: all 0.3s;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            position: relative;
            overflow: hidden;
        }

        .plan-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 5px;
            background: linear-gradient(90deg, #1976D2, #42A5F5);
            transform: scaleX(0);
            transition: transform 0.3s;
        }

        .plan-card:hover::before {
            transform: scaleX(1);
        }

        .plan-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 15px 30px rgba(25, 118, 210, 0.2);
        }

        .plan-card.featured {
            border: 2px solid #ffc107;
            transform: scale(1.05);
        }

        .plan-card.featured .plan-badge {
            display: block;
        }

        .plan-badge {
            display: none;
            position: absolute;
            top: 20px;
            right: -35px;
            background: #ffc107;
            color: #000;
            padding: 5px 40px;
            transform: rotate(45deg);
            font-weight: bold;
            font-size: 12px;
        }

        .plan-icon {
            font-size: 48px;
            margin-bottom: 15px;
            background: linear-gradient(135deg, #1976D2, #42A5F5);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .plan-name {
            font-size: 28px;
            font-weight: bold;
            margin-bottom: 10px;
            color: #333;
        }

        .plan-price {
            font-size: 36px;
            font-weight: bold;
            color: #1976D2;
            margin: 15px 0;
        }

        .plan-price small {
            font-size: 16px;
            color: #999;
        }

        .plan-features {
            list-style: none;
            padding: 20px 0;
            margin: 20px 0;
            border-top: 1px solid #eee;
            border-bottom: 1px solid #eee;
        }

        .plan-features li {
            padding: 10px 0;
            color: #666;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .plan-features li i {
            color: #28a745;
            font-size: 14px;
        }

        .btn-upgrade {
            background: linear-gradient(135deg, #1976D2, #42A5F5);
            color: white;
            padding: 15px 30px;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            width: 100%;
            transition: all 0.3s;
        }

        .btn-upgrade:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(25, 118, 210, 0.4);
        }

        .btn-upgrade:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        .custom-section {
            background: white;
            padding: 40px;
            border-radius: 15px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }

        .custom-section h2 {
            color: #333;
            margin-bottom: 10px;
        }

        .custom-section p {
            color: #666;
            margin-bottom: 30px;
        }

        .custom-form {
            max-width: 500px;
            margin: 0 auto;
        }

        .form-group {
            margin-bottom: 20px;
            text-align: left;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 600;
        }

        .form-group input {
            width: 100%;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #1976D2;
        }

        .form-group small {
            color: #1976D2;
        }

        @media (max-width: 768px) {
            .header h1 {
                font-size: 32px;
            }

            .plans-container {
                grid-template-columns: 1fr;
            }

            .plan-card.featured {
                transform: scale(1);
            }

            .current-plan {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="nav-bar">
            <div>
                <strong><i class="fas fa-file-alt"></i> FlexiFile</strong>
            </div>
            <div style="display: flex; gap: 10px;">
                <a href="${pageContext.request.contextPath}/homeServlet" class="btn btn-outline">
                    <i class="fas fa-home"></i> Trang chủ
                </a>
                <% if (isLoggedIn) { %>
                    <a href="${pageContext.request.contextPath}/profile" class="btn btn-primary">
                        <i class="fas fa-user"></i> Tài khoản
                    </a>
                <% } %>
            </div>
        </div>

        <div class="header">
            <h1><i class="fas fa-crown"></i> Nâng cấp tài khoản</h1>
            <p>Chọn gói phù hợp với nhu cầu của bạn</p>
        </div>

        <% if (isLoggedIn) { %>
            <div class="current-plan">
                <div class="plan-stat">
                    <div class="plan-stat-value">
                        <i class="fas fa-gem"></i>
                        <%= user.getPlanType() != null ? user.getPlanType() : "FREE" %>
                    </div>
                    <div class="plan-stat-label">Gói hiện tại</div>
                </div>
                <div class="plan-stat">
                    <div class="plan-stat-value">
                        <%= user.getRemainingConverts() %>
                    </div>
                    <div class="plan-stat-label">Lượt convert còn lại</div>
                </div>
                <div class="plan-stat">
                    <div class="plan-stat-value">
                        <%= user.getRole() %>
                    </div>
                    <div class="plan-stat-label">Vai trò</div>
                </div>
            </div>
        <% } else { %>
            <div class="warning-box">
                <i class="fas fa-exclamation-triangle"></i>
                <div>
                    <strong>Chú ý:</strong> Bạn cần
                    <a href="${pageContext.request.contextPath}/login" style="color: #856404; text-decoration: underline;">
                        đăng nhập
                    </a>
                    để nâng cấp tài khoản.
                </div>
            </div>
        <% } %>

        <div class="plans-container">
            <!-- Gói BASIC -->
            <div class="plan-card">
                <div class="plan-icon">
                    <i class="fas fa-star"></i>
                </div>
                <div class="plan-name">BASIC</div>
                <div class="plan-price">50,000₫</div>
                <ul class="plan-features">
                    <li><i class="fas fa-check-circle"></i> +50 lượt convert</li>
                    <li><i class="fas fa-check-circle"></i> Convert file cơ bản</li>
                    <li><i class="fas fa-check-circle"></i> Hỗ trợ 10 định dạng</li>
                    <li><i class="fas fa-check-circle"></i> File tối đa 10MB</li>
                </ul>
                <form action="${pageContext.request.contextPath}/payment" method="post">
                    <input type="hidden" name="orderId" value="BASIC<%= System.currentTimeMillis() %>">
                    <input type="hidden" name="amount" value="50000">
                    <input type="hidden" name="orderDesc" value="Nang cap BASIC - 50 converts">
                    <input type="hidden" name="bankCode" value="">
                    <input type="hidden" name="language" value="vn">
                    <button type="submit" class="btn-upgrade" <%= !isLoggedIn ? "disabled" : "" %>>
                        <i class="fas fa-shopping-cart"></i> Nâng cấp ngay
                    </button>
                </form>
            </div>

            <!-- Gói PREMIUM -->
            <div class="plan-card featured">
                <div class="plan-badge">PHỔ BIẾN</div>
                <div class="plan-icon">
                    <i class="fas fa-rocket"></i>
                </div>
                <div class="plan-name">PREMIUM</div>
                <div class="plan-price">100,000₫</div>
                <ul class="plan-features">
                    <li><i class="fas fa-check-circle"></i> +200 lượt convert</li>
                    <li><i class="fas fa-check-circle"></i> Tất cả tính năng BASIC</li>
                    <li><i class="fas fa-check-circle"></i> Hỗ trợ 25 định dạng</li>
                    <li><i class="fas fa-check-circle"></i> File tối đa 50MB</li>
                    <li><i class="fas fa-check-circle"></i> Xử lý ưu tiên</li>
                </ul>
                <form action="${pageContext.request.contextPath}/payment" method="post">
                    <input type="hidden" name="orderId" value="PREMIUM<%= System.currentTimeMillis() %>">
                    <input type="hidden" name="amount" value="100000">
                    <input type="hidden" name="orderDesc" value="Nang cap PREMIUM - 200 converts">
                    <input type="hidden" name="bankCode" value="">
                    <input type="hidden" name="language" value="vn">
                    <button type="submit" class="btn-upgrade" <%= !isLoggedIn ? "disabled" : "" %>>
                        <i class="fas fa-shopping-cart"></i> Nâng cấp ngay
                    </button>
                </form>
            </div>

            <!-- Gói PRO -->
            <div class="plan-card">
                <div class="plan-icon">
                    <i class="fas fa-crown"></i>
                </div>
                <div class="plan-name">PRO</div>
                <div class="plan-price">200,000₫</div>
                <ul class="plan-features">
                    <li><i class="fas fa-check-circle"></i> +1000 lượt convert</li>
                    <li><i class="fas fa-check-circle"></i> Tất cả tính năng PREMIUM</li>
                    <li><i class="fas fa-check-circle"></i> Hỗ trợ 50+ định dạng</li>
                    <li><i class="fas fa-check-circle"></i> File tối đa 100MB</li>
                    <li><i class="fas fa-check-circle"></i> Xử lý ưu tiên cao</li>
                    <li><i class="fas fa-check-circle"></i> Hỗ trợ 24/7</li>
                </ul>
                <form action="${pageContext.request.contextPath}/payment" method="post">
                    <input type="hidden" name="orderId" value="PRO<%= System.currentTimeMillis() %>">
                    <input type="hidden" name="amount" value="200000">
                    <input type="hidden" name="orderDesc" value="Nang cap PRO - 1000 converts">
                    <input type="hidden" name="bankCode" value="">
                    <input type="hidden" name="language" value="vn">
                    <button type="submit" class="btn-upgrade" <%= !isLoggedIn ? "disabled" : "" %>>
                        <i class="fas fa-shopping-cart"></i> Nâng cấp ngay
                    </button>
                </form>
            </div>
        </div>

        <!-- Custom Package -->
        <div class="custom-section">
            <h2><i class="fas fa-sliders-h"></i> Gói tùy chỉnh</h2>
            <p>Linh hoạt chọn số tiền phù hợp với nhu cầu của bạn</p>
            <form action="${pageContext.request.contextPath}/payment" method="post" class="custom-form">
                <div class="form-group">
                    <label for="customAmount">
                        <i class="fas fa-money-bill-wave"></i> Số tiền (VND)
                    </label>
                    <input
                        type="number"
                        id="customAmount"
                        name="amount"
                        min="10000"
                        max="1000000"
                        step="10000"
                        value="100000"
                        required
                        onchange="updateCustomDesc(this.value)">
                    <small>
                        <i class="fas fa-info-circle"></i>
                        Bạn sẽ nhận được <span id="convertCount">200</span> lượt convert
                    </small>
                </div>
                <input type="hidden" name="orderId" value="CUSTOM<%= System.currentTimeMillis() %>">
                <input type="hidden" name="orderDesc" id="customDesc" value="Custom package - 100000 VND">
                <input type="hidden" name="bankCode" value="">
                <input type="hidden" name="language" value="vn">
                <button type="submit" class="btn-upgrade" <%= !isLoggedIn ? "disabled" : "" %>>
                    <i class="fas fa-shopping-cart"></i> Thanh toán
                </button>
            </form>
        </div>
    </div>

    <script>
        function updateCustomDesc(amount) {
            const converts = Math.floor(amount / 500);
            document.getElementById('customDesc').value = 'Custom package - ' + amount + ' VND';
            document.getElementById('convertCount').textContent = converts;
        }
    </script>
</body>
</html>
