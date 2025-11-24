<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.bean.User" %>
<%@ page import="model.bean.PaymentHistory" %>
<%@ page import="model.bean.ConvertHistory" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    @SuppressWarnings("unchecked")
    List<PaymentHistory> paymentHistory = (List<PaymentHistory>) request.getAttribute("paymentHistory");
    
    @SuppressWarnings("unchecked")
    List<ConvertHistory> convertHistory = (List<ConvertHistory>) request.getAttribute("convertHistory");
    
    Integer totalConverts = (Integer) request.getAttribute("totalConverts");
    if (totalConverts == null) totalConverts = 0;
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ của tôi - FlexiFile</title>
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
            background: white;
            border-radius: 15px;
            padding: 20px 30px;
            margin-bottom: 20px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header h1 {
            color: #1976D2;
            font-size: 28px;
        }

        .nav-buttons {
            display: flex;
            gap: 10px;
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

        .btn-primary:hover {
            background: #1565C0;
            transform: translateY(-2px);
        }

        .btn-outline {
            background: white;
            color: #1976D2;
            border: 2px solid #1976D2;
        }

        .btn-outline:hover {
            background: #1976D2;
            color: white;
        }

        .profile-grid {
            display: grid;
            grid-template-columns: 350px 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }

        .card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }

        .profile-card {
            text-align: center;
        }

        .avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: linear-gradient(135deg, #1976D2 0%, #42A5F5 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            font-size: 48px;
            color: white;
            overflow: hidden;
            position: relative;
        }

        .avatar img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            position: absolute;
            top: 0;
            left: 0;
        }

        .user-name {
            font-size: 24px;
            font-weight: bold;
            color: #333;
            margin-bottom: 5px;
        }

        .user-email {
            color: #666;
            margin-bottom: 20px;
        }

        .plan-badge {
            display: inline-block;
            padding: 8px 20px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 14px;
            margin-bottom: 20px;
        }

        .plan-free {
            background: #E3F2FD;
            color: #1976D2;
        }

        .plan-basic {
            background: #FFF3E0;
            color: #F57C00;
        }

        .plan-premium {
            background: #E1F5FE;
            color: #0288D1;
        }

        .plan-pro {
            background: #FFF8E1;
            color: #F57F17;
        }

        .stats {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-top: 20px;
        }

        .stat-item {
            background: #E3F2FD;
            padding: 15px;
            border-radius: 10px;
            text-align: center;
        }

        .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #1976D2;
        }

        .stat-label {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }

        .info-section {
            margin-bottom: 30px;
        }

        .section-title {
            font-size: 20px;
            font-weight: bold;
            color: #333;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .section-title i {
            color: #1976D2;
        }

        .info-grid {
            display: grid;
            gap: 15px;
        }

        .info-item {
            display: flex;
            justify-content: space-between;
            padding: 15px;
            background: #F5F5F5;
            border-radius: 10px;
        }

        .info-label {
            color: #666;
            font-weight: 500;
        }

        .info-value {
            color: #333;
            font-weight: 600;
        }

        .payment-history {
            margin-top: 20px;
        }

        .payment-table {
            width: 100%;
            border-collapse: collapse;
        }

        .payment-table th {
            background: #E3F2FD;
            padding: 12px;
            text-align: left;
            font-weight: 600;
            color: #1976D2;
            border-bottom: 2px solid #BBDEFB;
        }

        .payment-table td {
            padding: 12px;
            border-bottom: 1px solid #e0e0e0;
        }

        .payment-table tr:hover {
            background: #F5F5F5;
        }

        .status-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }

        .status-success {
            background: #C8E6C9;
            color: #2E7D32;
        }

        .status-failed {
            background: #FFCDD2;
            color: #C62828;
        }

        .empty-state {
            text-align: center;
            padding: 40px;
            color: #999;
        }

        .empty-state i {
            font-size: 48px;
            margin-bottom: 15px;
            opacity: 0.5;
            color: #1976D2;
        }

        .edit-btn {
            background: #1976D2;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            margin-top: 15px;
            width: 100%;
            transition: all 0.3s;
        }

        .edit-btn:hover {
            background: #1565C0;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }

        .modal-content {
            background-color: white;
            margin: 5% auto;
            padding: 30px;
            border-radius: 15px;
            width: 90%;
            max-width: 500px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .modal-header h3 {
            color: #1976D2;
            font-size: 22px;
        }

        .close {
            color: #999;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
            transition: color 0.3s;
        }

        .close:hover {
            color: #333;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #666;
            font-weight: 500;
        }

        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            font-family: inherit;
        }

        .form-group input:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #1976D2;
        }

        .avatar-upload {
            text-align: center;
            margin-bottom: 20px;
        }

        .avatar-preview {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            margin: 0 auto 15px;
            background: linear-gradient(135deg, #1976D2 0%, #42A5F5 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 60px;
            color: white;
            overflow: hidden;
        }

        .avatar-preview img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .file-input-wrapper {
            position: relative;
            overflow: hidden;
            display: inline-block;
        }

        .file-input-wrapper input[type=file] {
            position: absolute;
            left: -9999px;
        }

        .file-input-label {
            display: inline-block;
            padding: 10px 20px;
            background: #E3F2FD;
            color: #1976D2;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
        }

        .file-input-label:hover {
            background: #BBDEFB;
        }

        .success-message {
            background: #C8E6C9;
            color: #2E7D32;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 15px;
            display: none;
        }

        .error-message {
            background: #FFCDD2;
            color: #C62828;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 15px;
            display: none;
        }

        .avatar-clickable {
            cursor: pointer;
            transition: transform 0.3s;
        }

        .avatar-clickable:hover {
            transform: scale(1.05);
        }

        .convert-history {
            margin-top: 20px;
        }

        .convert-grid {
            display: grid;
            gap: 12px;
            margin-top: 15px;
        }

        .convert-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 16px;
            background: #F5F5F5;
            border-radius: 10px;
            transition: all 0.3s;
        }

        .convert-item:hover {
            background: #E3F2FD;
            transform: translateX(5px);
        }

        .convert-info {
            flex: 1;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .convert-icon {
            width: 45px;
            height: 45px;
            border-radius: 10px;
            background: linear-gradient(135deg, #1976D2 0%, #42A5F5 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: white;
        }

        .convert-details {
            flex: 1;
        }

        .convert-filename {
            font-weight: 600;
            color: #333;
            margin-bottom: 4px;
            font-size: 14px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 300px;
        }

        .convert-meta {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 12px;
            color: #666;
        }

        .format-arrow {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 2px 8px;
            background: white;
            border-radius: 12px;
            font-weight: 600;
        }

        .format-arrow .source {
            color: #1976D2;
        }

        .format-arrow .target {
            color: #0288D1;
        }

        .convert-time {
            color: #999;
            font-size: 11px;
        }

        .btn-download-small {
            padding: 8px 16px;
            background: #1976D2;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            transition: all 0.3s;
            white-space: nowrap;
        }

        .btn-download-small:hover {
            background: #1565C0;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
        }

        .view-all-link {
            text-align: center;
            margin-top: 15px;
        }

        .view-all-link a {
            color: #1976D2;
            text-decoration: none;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            transition: all 0.3s;
        }

        .view-all-link a:hover {
            color: #1565C0;
            gap: 10px;
        }

        @media (max-width: 768px) {
            .profile-grid {
                grid-template-columns: 1fr;
            }

            .stats {
                grid-template-columns: 1fr;
            }

            .modal-content {
                width: 95%;
                margin: 10% auto;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="header">
            <h1><i class="fas fa-user-circle"></i> Hồ sơ của tôi</h1>
            <div class="nav-buttons">
                <a href="${pageContext.request.contextPath}/upload" class="btn btn-outline">
                    <i class="fas fa-home"></i> Trang chủ
                </a>
                <a href="${pageContext.request.contextPath}/payment" class="btn btn-primary">
                    <i class="fas fa-crown"></i> Nâng cấp
                </a>
            </div>
        </div>

        <div class="profile-grid">
            <!-- Profile Card -->
            <div class="card profile-card">
                <div class="avatar avatar-clickable" onclick="openAvatarModal()">
                    <% if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) { %>
                        <img src="<%= user.getAvatarUrl() %>" alt="Avatar">
                    <% } else { %>
                        <i class="fas fa-user"></i>
                    <% } %>
                </div>
                <div class="user-name"><%= user.getFullName() != null ? user.getFullName() : user.getUsername() %></div>
                <div class="user-email"><%= user.getEmail() %></div>

                <%
                    String planType = user.getPlanType() != null ? user.getPlanType() : "FREE";
                    String planClass = "plan-" + planType.toLowerCase();
                %>
                <span class="plan-badge <%= planClass %>">
                    <i class="fas fa-gem"></i> <%= planType %>
                </span>

                <div class="stats">
                    <div class="stat-item">
                        <div class="stat-value"><%= user.getRemainingConverts() %></div>
                        <div class="stat-label">Lượt convert</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value"><%= totalConverts %></div>
                        <div class="stat-label">Đã convert</div>
                    </div>
                </div>

                <button class="edit-btn" onclick="openEditModal()">
                    <i class="fas fa-edit"></i> Chỉnh sửa thông tin
                </button>
                <button class="edit-btn" onclick="openPasswordModal()" style="margin-top: 10px; background: #0288D1;">
                    <i class="fas fa-key"></i> Đổi mật khẩu
                </button>
            </div>

            <!-- Info Card -->
            <div class="card">
                <div class="info-section">
                    <h2 class="section-title">
                        <i class="fas fa-info-circle"></i>
                        Thông tin tài khoản
                    </h2>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="info-label">Tên người dùng</span>
                            <span class="info-value"><%= user.getUsername() %></span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Email</span>
                            <span class="info-value"><%= user.getEmail() %></span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Vai trò</span>
                            <span class="info-value"><%= user.getRole() %></span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Gói dịch vụ</span>
                            <span class="info-value"><%= planType %></span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Ngày tạo</span>
                            <span class="info-value">
                                <%= user.getCreatedAt() != null ? sdf.format(user.getCreatedAt()) : "N/A" %>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- ✅ Convert History Section -->
        <div class="card convert-history">
            <h2 class="section-title">
                <i class="fas fa-history"></i>
                File đã chuyển đổi gần đây
            </h2>

            <% if (convertHistory != null && !convertHistory.isEmpty()) { %>
                <%-- Có convert history → Hiển thị danh sách --%>
                <div class="convert-grid" id="convertGrid">
                    <% 
                    // Chỉ hiển thị 5 files đầu tiên
                    int displayCount = Math.min(5, convertHistory.size());
                    for (int i = 0; i < displayCount; i++) { 
                        ConvertHistory item = convertHistory.get(i);
                    %>
                        <div class="convert-item">
                            <div class="convert-info">
                                <div class="convert-icon">
                                    <i class="fas fa-file-alt"></i>
                                </div>
                                <div class="convert-details">
                                    <div class="convert-filename" title="<%= item.getFileName() %>">
                                        <%= item.getFileName() %>
                                    </div>
                                    <div class="convert-meta">
                                        <span class="format-arrow">
                                            <span class="source"><%= item.getSourceFormat().toUpperCase() %></span>
                                            <i class="fas fa-arrow-right" style="font-size: 10px;"></i>
                                            <span class="target"><%= item.getTargetFormat().toUpperCase() %></span>
                                        </span>
                                        <span class="convert-time">
                                            <i class="fas fa-clock"></i>
                                            <%= item.getConvertedAt() != null ? sdf.format(item.getConvertedAt()) : "N/A" %>
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <% if (item.getResultUrl() != null && !item.getResultUrl().isEmpty()) { %>
                                <a href="<%= item.getResultUrl() %>" 
                                   class="btn-download-small download-link" 
                                   data-original-url="<%= item.getResultUrl() %>"
                                   data-filename="<%= item.getFileName() %>"
                                   data-target-format="<%= item.getTargetFormat() %>"
                                   download 
                                   target="_blank">
                                    <i class="fas fa-download"></i>
                                    Tải xuống
                                </a>
                            <% } %>
                        </div>
                    <% } %>
                </div>

                <%-- ✅ Load More / Collapse Button --%>
                <% if (convertHistory.size() > 5) { %>
                    <div class="view-all-link">
                        <a href="javascript:void(0)" id="loadMoreBtn" onclick="loadMoreConverts()">
                            Xem thêm (<span id="remainingCount"><%= convertHistory.size() - 5 %></span> file)
                            <i class="fas fa-chevron-down"></i>
                        </a>
                        <a href="javascript:void(0)" id="collapseBtn" onclick="collapseConverts()" style="display: none;">
                            Thu gọn
                            <i class="fas fa-chevron-up"></i>
                        </a>
                    </div>
                <% } %>

                <%-- ✅ Hidden data để JavaScript load thêm --%>
                <script id="convertHistoryData" type="application/json">
                [
                    <% 
                    for (int i = 0; i < convertHistory.size(); i++) { 
                        ConvertHistory item = convertHistory.get(i);
                        if (i > 0) out.print(",");
                    %>
                    {
                        "fileName": "<%= item.getFileName().replace("\"", "\\\"") %>",
                        "sourceFormat": "<%= item.getSourceFormat() %>",
                        "targetFormat": "<%= item.getTargetFormat() %>",
                        "resultUrl": "<%= item.getResultUrl() %>",
                        "convertedAt": "<%= item.getConvertedAt() != null ? sdf.format(item.getConvertedAt()) : "" %>"
                    }
                    <% } %>
                ]
                </script>

            <% } else { %>
                <%-- KHÔNG có convert history → Hiển thị empty state --%>
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <p>Chưa có file nào được chuyển đổi</p>
                    <a href="${pageContext.request.contextPath}/upload" class="btn btn-primary" style="margin-top: 15px;">
                        <i class="fas fa-upload"></i> Bắt đầu chuyển đổi
                    </a>
                </div>
            <% } %>
        </div>

        <!-- Payment History -->
        <div class="card payment-history">
            <h2 class="section-title">
                <i class="fas fa-history"></i>
                Lịch sử thanh toán
            </h2>

            <% if (paymentHistory != null && !paymentHistory.isEmpty()) { %>
                <table class="payment-table">
                    <thead>
                        <tr>
                            <th>Thời gian</th>
                            <th>Mã đơn hàng</th>
                            <th>Gói dịch vụ</th>
                            <th>Số tiền</th>
                            <th>Lượt convert</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (PaymentHistory payment : paymentHistory) { %>
                            <tr>
                                <td><%= payment.getPaymentDate() != null ? sdf.format(payment.getPaymentDate()) : "N/A" %></td>
                                <td><%= payment.getOrderId() %></td>
                                <td><%= payment.getPlanType() != null ? payment.getPlanType() : "N/A" %></td>
                                <td><%= String.format("%,d", (long)payment.getAmount()) %> VND</td>
                                <td>+<%= payment.getAdditionalConverts() %></td>
                                <td>
                                    <% if ("SUCCESS".equals(payment.getStatus())) { %>
                                        <span class="status-badge status-success">
                                            <i class="fas fa-check-circle"></i> Thành công
                                        </span>
                                    <% } else { %>
                                        <span class="status-badge status-failed">
                                            <i class="fas fa-times-circle"></i> Thất bại
                                        </span>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <p>Chưa có giao dịch nào</p>
                </div>
            <% } %>
        </div>
    </div>

    <!-- Modal chỉnh sửa thông tin -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3><i class="fas fa-edit"></i> Chỉnh sửa thông tin</h3>
                <span class="close" onclick="closeEditModal()">&times;</span>
            </div>
            <div id="editMessage" class="success-message"></div>
            <div id="editError" class="error-message"></div>
            <form id="editForm">
                <div class="form-group">
                    <label for="editFullName">Họ và tên</label>
                    <input type="text" id="editFullName" name="fullName"
                           value="<%= user.getFullName() != null ? user.getFullName() : "" %>" required>
                </div>
                <div class="form-group">
                    <label for="editUsername">Tên người dùng</label>
                    <input type="text" id="editUsername" name="username"
                           value="<%= user.getUsername() %>" required>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">
                    <i class="fas fa-save"></i> Lưu thay đổi
                </button>
            </form>
        </div>
    </div>

    <!-- Modal upload avatar -->
    <div id="avatarModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3><i class="fas fa-image"></i> Cập nhật Avatar</h3>
                <span class="close" onclick="closeAvatarModal()">&times;</span>
            </div>
            <div id="avatarMessage" class="success-message"></div>
            <div id="avatarError" class="error-message"></div>
            <div class="avatar-upload">
                <div class="avatar-preview" id="avatarPreview">
                    <% if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) { %>
                        <img src="<%= user.getAvatarUrl() %>" alt="Avatar" id="previewImg">
                    <% } else { %>
                        <i class="fas fa-user" id="previewIcon"></i>
                    <% } %>
                </div>
                <form id="avatarForm" enctype="multipart/form-data">
                    <div class="file-input-wrapper">
                        <input type="file" id="avatarInput" name="avatar" accept="image/*" onchange="previewAvatar(event)">
                        <label for="avatarInput" class="file-input-label">
                            <i class="fas fa-upload"></i> Chọn ảnh
                        </label>
                    </div>
                    <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 15px;">
                        <i class="fas fa-save"></i> Lưu avatar
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal đổi mật khẩu -->
    <div id="passwordModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3><i class="fas fa-key"></i> Đổi mật khẩu</h3>
                <span class="close" onclick="closePasswordModal()">&times;</span>
            </div>
            <div id="passwordMessage" class="success-message"></div>
            <div id="passwordError" class="error-message"></div>
            <form id="passwordForm">
                <div class="form-group">
                    <label for="oldPassword">Mật khẩu cũ</label>
                    <input type="password" id="oldPassword" name="oldPassword" required>
                </div>
                <div class="form-group">
                    <label for="newPassword">Mật khẩu mới</label>
                    <input type="password" id="newPassword" name="newPassword" minlength="8" required>
                    <small style="color: #666; font-size: 12px;">
                        <i class="fas fa-info-circle"></i> Tối thiểu 8 ký tự
                    </small>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Xác nhận mật khẩu mới</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" minlength="8" required>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">
                    <i class="fas fa-save"></i> Đổi mật khẩu
                </button>
            </form>
        </div>
    </div>

    <script>
        // ✅ Load more converts logic
        var allConverts = [];
        var currentDisplayCount = 5;
        var loadMoreStep = 5;

        document.addEventListener('DOMContentLoaded', function() {
            // Load convert history data
            var dataScript = document.getElementById('convertHistoryData');
            if (dataScript) {
                try {
                    allConverts = JSON.parse(dataScript.textContent);
                    console.log('📦 Loaded ALL converts:', allConverts.length);
                } catch (e) {
                    console.error('❌ Error parsing convert history data:', e);
                }
            }

            // Process download links
            processDownloadLinks();
        });

        function processDownloadLinks() {
            var downloadLinks = document.querySelectorAll('.download-link');
            
            downloadLinks.forEach(function(link) {
                var originalUrl = link.getAttribute('data-original-url');
                
                if (originalUrl && originalUrl.includes('cloudinary.com') && originalUrl.includes('/upload/')) {
                    var uploadIndex = originalUrl.indexOf('/upload/');
                    
                    if (uploadIndex !== -1) {
                        var part1 = originalUrl.substring(0, uploadIndex + 8);
                        var part2 = originalUrl.substring(uploadIndex + 8);
                        
                        var downloadUrl = part1 + 'fl_attachment/' + part2;
                        link.href = downloadUrl;
                        
                        console.log('🔗 Original:', originalUrl);
                        console.log('📥 Download:', downloadUrl);
                    }
                }
            });
        }

        function loadMoreConverts() {
            var grid = document.getElementById('convertGrid');
            var loadMoreBtn = document.getElementById('loadMoreBtn');
            var collapseBtn = document.getElementById('collapseBtn');
            var remainingCount = document.getElementById('remainingCount');
            
            // Tính toán số lượng cần load
            var nextDisplayCount = Math.min(currentDisplayCount + loadMoreStep, allConverts.length);
            var newItemsCount = nextDisplayCount - currentDisplayCount;
            
            console.log('📥 Loading more:', newItemsCount, 'items (from', currentDisplayCount, 'to', nextDisplayCount, ')');
            
            // Thêm items mới
            for (var i = currentDisplayCount; i < nextDisplayCount; i++) {
                var item = allConverts[i];
                var itemHtml = createConvertItemHTML(item);
                grid.insertAdjacentHTML('beforeend', itemHtml);
            }
            
            currentDisplayCount = nextDisplayCount;
            
            // Cập nhật số lượng còn lại
            var remaining = allConverts.length - currentDisplayCount;
            remainingCount.textContent = remaining;
            
            // Process download links cho items mới
            processDownloadLinks();
            
            // Nếu đã load hết → Ẩn "Xem thêm", hiện "Thu gọn"
            if (currentDisplayCount >= allConverts.length) {
                loadMoreBtn.style.display = 'none';
                collapseBtn.style.display = 'inline-flex';
                console.log('✅ All items loaded');
            }
        }

        function collapseConverts() {
            var grid = document.getElementById('convertGrid');
            var loadMoreBtn = document.getElementById('loadMoreBtn');
            var collapseBtn = document.getElementById('collapseBtn');
            var remainingCount = document.getElementById('remainingCount');
            
            // Xóa tất cả items (giữ lại 5 đầu tiên)
            var items = grid.querySelectorAll('.convert-item');
            for (var i = 5; i < items.length; i++) {
                items[i].remove();
            }
            
            currentDisplayCount = 5;
            
            // Cập nhật UI
            var remaining = allConverts.length - currentDisplayCount;
            remainingCount.textContent = remaining;
            
            loadMoreBtn.style.display = 'inline-flex';
            collapseBtn.style.display = 'none';
            
            // Scroll về section convert history
            document.querySelector('.convert-history').scrollIntoView({ 
                behavior: 'smooth', 
                block: 'start' 
            });
            
            console.log('📦 Collapsed to 5 items');
        }

        function createConvertItemHTML(item) {
            var html = '<div class="convert-item">' +
                '<div class="convert-info">' +
                    '<div class="convert-icon">' +
                        '<i class="fas fa-file-alt"></i>' +
                    '</div>' +
                    '<div class="convert-details">' +
                        '<div class="convert-filename" title="' + escapeHtml(item.fileName) + '">' +
                            escapeHtml(item.fileName) +
                        '</div>' +
                        '<div class="convert-meta">' +
                            '<span class="format-arrow">' +
                                '<span class="source">' + item.sourceFormat.toUpperCase() + '</span>' +
                                '<i class="fas fa-arrow-right" style="font-size: 10px;"></i>' +
                                '<span class="target">' + item.targetFormat.toUpperCase() + '</span>' +
                            '</span>' +
                            '<span class="convert-time">' +
                                '<i class="fas fa-clock"></i> ' +
                                item.convertedAt +
                            '</span>' +
                        '</div>' +
                    '</div>' +
                '</div>';
            
            if (item.resultUrl) {
                html += '<a href="' + item.resultUrl + '" ' +
                    'class="btn-download-small download-link" ' +
                    'data-original-url="' + item.resultUrl + '" ' +
                    'data-filename="' + escapeHtml(item.fileName) + '" ' +
                    'data-target-format="' + item.targetFormat + '" ' +
                    'download ' +
                    'target="_blank">' +
                        '<i class="fas fa-download"></i> ' +
                        'Tải xuống' +
                    '</a>';
            }
            
            html += '</div>';
            
            return html;
        }

        function escapeHtml(text) {
            var map = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#039;'
            };
            return text.replace(/[&<>"']/g, function(m) { return map[m]; });
        }

        // Modal functions
        function openEditModal() {
            document.getElementById('editModal').style.display = 'block';
        }

        function closeEditModal() {
            document.getElementById('editModal').style.display = 'none';
            document.getElementById('editMessage').style.display = 'none';
            document.getElementById('editError').style.display = 'none';
        }

        function openAvatarModal() {
            document.getElementById('avatarModal').style.display = 'block';
        }

        function closeAvatarModal() {
            document.getElementById('avatarModal').style.display = 'none';
            document.getElementById('avatarMessage').style.display = 'none';
            document.getElementById('avatarError').style.display = 'none';
        }

        function openPasswordModal() {
            document.getElementById('passwordModal').style.display = 'block';
        }

        function closePasswordModal() {
            document.getElementById('passwordModal').style.display = 'none';
            document.getElementById('passwordMessage').style.display = 'none';
            document.getElementById('passwordError').style.display = 'none';
            document.getElementById('passwordForm').reset();
        }

        // Close modal when clicking outside
        window.onclick = function(event) {
            const editModal = document.getElementById('editModal');
            const avatarModal = document.getElementById('avatarModal');
            const passwordModal = document.getElementById('passwordModal');
            if (event.target == editModal) {
                closeEditModal();
            }
            if (event.target == avatarModal) {
                closeAvatarModal();
            }
            if (event.target == passwordModal) {
                closePasswordModal();
            }
        }

        // Preview avatar before upload
        function previewAvatar(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const preview = document.getElementById('avatarPreview');
                    preview.innerHTML = '<img src="' + e.target.result + '" alt="Preview" style="width: 100%; height: 100%; object-fit: cover;">';
                }
                reader.readAsDataURL(file);
            }
        }

        // Handle edit form submission
        document.getElementById('editForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            formData.append('action', 'updateProfile');

            fetch('${pageContext.request.contextPath}/profile', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('editMessage').textContent = data.message;
                    document.getElementById('editMessage').style.display = 'block';
                    document.getElementById('editError').style.display = 'none';

                    // Reload page after 1.5 seconds
                    setTimeout(function() {
                        location.reload();
                    }, 1500);
                } else {
                    document.getElementById('editError').textContent = data.message;
                    document.getElementById('editError').style.display = 'block';
                    document.getElementById('editMessage').style.display = 'none';
                }
            })
            .catch(error => {
                document.getElementById('editError').textContent = 'Lỗi kết nối: ' + error.message;
                document.getElementById('editError').style.display = 'block';
                document.getElementById('editMessage').style.display = 'none';
            });
        });

        // Handle avatar form submission
        document.getElementById('avatarForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            formData.append('action', 'uploadAvatar');

            fetch('${pageContext.request.contextPath}/profile', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('avatarMessage').textContent = data.message;
                    document.getElementById('avatarMessage').style.display = 'block';
                    document.getElementById('avatarError').style.display = 'none';

                    // Reload page after 1.5 seconds
                    setTimeout(function() {
                        location.reload();
                    }, 1500);
                } else {
                    document.getElementById('avatarError').textContent = data.message;
                    document.getElementById('avatarError').style.display = 'block';
                    document.getElementById('avatarMessage').style.display = 'none';
                }
            })
            .catch(error => {
                document.getElementById('avatarError').textContent = 'Lỗi kết nối: ' + error.message;
                document.getElementById('avatarError').style.display = 'block';
                document.getElementById('avatarMessage').style.display = 'none';
            });
        });

        // Handle password form submission
        document.getElementById('passwordForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            formData.append('action', 'changePassword');

            // Client-side validation
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (newPassword !== confirmPassword) {
                document.getElementById('passwordError').textContent = 'Mật khẩu mới không khớp!';
                document.getElementById('passwordError').style.display = 'block';
                document.getElementById('passwordMessage').style.display = 'none';
                return;
            }

            fetch('${pageContext.request.contextPath}/profile', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('passwordMessage').textContent = data.message;
                    document.getElementById('passwordMessage').style.display = 'block';
                    document.getElementById('passwordError').style.display = 'none';

                    // Clear form and close modal after 1.5 seconds
                    setTimeout(function() {
                        closePasswordModal();
                    }, 1500);
                } else {
                    document.getElementById('passwordError').textContent = data.message;
                    document.getElementById('passwordError').style.display = 'block';
                    document.getElementById('passwordMessage').style.display = 'none';
                }
            })
            .catch(error => {
                document.getElementById('passwordError').textContent = 'Lỗi kết nối: ' + error.message;
                document.getElementById('passwordError').style.display = 'block';
                document.getElementById('passwordMessage').style.display = 'none';
            });
        });
    </script>
</body>
</html>
