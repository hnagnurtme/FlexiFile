<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Lỗi hệ thống</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 50px; text-align: center; }
        .error-container { max-width: 500px; margin: 0 auto; }
        .error-code { font-size: 72px; color: #dc3545; margin: 0; }
        .error-message { font-size: 24px; margin: 20px 0; }
        .btn { display: inline-block; padding: 10px 20px; margin: 10px; 
               background: #007bff; color: white; text-decoration: none; border-radius: 4px; }
    </style>
</head>
<body>
    <div class="error-container">
        <h1 class="error-code">Lỗi</h1>
        <div class="error-message">
            <%= request.getAttribute("errorMessage") != null ? 
                request.getAttribute("errorMessage") : "Đã xảy ra lỗi hệ thống" %>
        </div>
        <p>Vui lòng thử lại sau hoặc liên hệ hỗ trợ kỹ thuật.</p>
        
        <div>
            <a href="${pageContext.request.contextPath}" class="btn">Về trang chủ</a>
            <a href="javascript:history.back()" class="btn">Quay lại</a>
        </div>
    </div>
</body>
</html>