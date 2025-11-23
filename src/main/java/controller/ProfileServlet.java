package controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.bean.PaymentHistory;
import model.bean.User;
import model.bo.PaymentBO;
import model.dao.AuthDAO;
import util.CloudinaryUtil;

@WebServlet("/profile")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class ProfileServlet extends HttpServlet {

    private PaymentBO paymentBO;
    private AuthDAO authDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.paymentBO = new PaymentBO();
        this.authDAO = new AuthDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Kiểm tra đăng nhập
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Refresh user data từ database để đảm bảo thông tin mới nhất
            User updatedUser = authDAO.getUserById(user.getId());
            if (updatedUser != null) {
                session.setAttribute("user", updatedUser);
                user = updatedUser;
            }

            // Lấy lịch sử thanh toán
            List<PaymentHistory> paymentHistory = paymentBO.getUserPaymentHistory(user.getId());
            System.out.println("DEBUG: User ID: " + user.getId());
            System.out.println("DEBUG: Payment History count: " + (paymentHistory != null ? paymentHistory.size() : "null"));
            if (paymentHistory != null && !paymentHistory.isEmpty()) {
                for (PaymentHistory ph : paymentHistory) {
                    System.out.println("DEBUG: Payment - OrderID: " + ph.getOrderId() + ", Status: " + ph.getStatus() + ", Amount: " + ph.getAmount());
                }
            }
            request.setAttribute("paymentHistory", paymentHistory);

            // Forward đến profile JSP
            request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in ProfileServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải thông tin profile: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("updateProfile".equals(action)) {
                handleUpdateProfile(request, response, user, session);
            } else if ("uploadAvatar".equals(action)) {
                handleUploadAvatar(request, response, user, session);
            } else if ("changePassword".equals(action)) {
                handleChangePassword(request, response, user, session);
            } else {
                response.sendRedirect(request.getContextPath() + "/profile");
            }
        } catch (Exception e) {
            System.err.println("Error in ProfileServlet POST: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi server không mong muốn: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Xử lý cập nhật thông tin profile
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response,
                                     User user, HttpSession session)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String username = request.getParameter("username");

        try {
            // Cập nhật profile
            boolean updated = authDAO.updateUserProfile(user.getId(), fullName, username, null);

            if (updated) {
                // Refresh user data trong session
                User updatedUser = authDAO.getUserById(user.getId());
                if (updatedUser != null) {
                    session.setAttribute("user", updatedUser);
                }

                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"message\": \"Cập nhật thông tin thành công!\"}");
            } else {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Không thể cập nhật thông tin!\"}");
            }

        } catch (Exception e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi server: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Xử lý upload avatar lên Cloudinary
     */
    private void handleUploadAvatar(HttpServletRequest request, HttpServletResponse response,
                                    User user, HttpSession session)
            throws ServletException, IOException {

        try {
            Part filePart = request.getPart("avatar");

            if (filePart == null || filePart.getSize() == 0) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Vui lòng chọn file ảnh!\"}");
                return;
            }

            // Validate file type
            String contentType = filePart.getContentType();
            if (!contentType.startsWith("image/")) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"File phải là ảnh (jpg, png, gif)!\"}");
                return;
            }

            // Validate file size (max 10MB)
            if (filePart.getSize() > 10 * 1024 * 1024) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Kích thước file tối đa 10MB!\"}");
                return;
            }

            // Tạo tên file unique cho Cloudinary
            String cloudinaryFileName = "avatar_" + user.getId() + "_" + System.currentTimeMillis();

            // Upload lên Cloudinary
            InputStream inputStream = filePart.getInputStream();
            String cloudinaryUrl = CloudinaryUtil.uploadAvatar(inputStream, cloudinaryFileName);

            System.out.println("Avatar uploaded to Cloudinary: " + cloudinaryUrl);

            // Cập nhật avatar URL trong database
            boolean updated = authDAO.updateUserAvatar(user.getId(), cloudinaryUrl);

            if (updated) {
                // Xóa avatar cũ từ Cloudinary nếu có
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()
                    && user.getAvatarUrl().contains("cloudinary.com")) {
                    deleteOldAvatar(user.getAvatarUrl());
                }

                // Refresh user data trong session
                User updatedUser = authDAO.getUserById(user.getId());
                if (updatedUser != null) {
                    session.setAttribute("user", updatedUser);
                }

                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"message\": \"Cập nhật avatar thành công!\", \"avatarUrl\": \"" + cloudinaryUrl + "\"}");
            } else {
                // Xóa file từ Cloudinary nếu update database thất bại
                try {
                    String publicId = CloudinaryUtil.getPublicIdFromUrl(cloudinaryUrl);
                    CloudinaryUtil.deleteFile(publicId);
                } catch (Exception deleteError) {
                    System.err.println("Error deleting uploaded avatar from Cloudinary: " + deleteError.getMessage());
                }

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"success\": false, \"message\": \"Không thể cập nhật avatar!\"}");
            }

        } catch (Exception e) {
            System.err.println("Error uploading avatar: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi upload avatar: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Xóa avatar cũ từ Cloudinary
     */
    private void deleteOldAvatar(String avatarUrl) {
        try {
            if (avatarUrl != null && avatarUrl.contains("cloudinary.com")) {
                String publicId = CloudinaryUtil.getPublicIdFromUrl(avatarUrl);
                CloudinaryUtil.deleteFile(publicId);
                System.out.println("Deleted old avatar from Cloudinary: " + publicId);
            }
        } catch (Exception e) {
            System.err.println("Error deleting old avatar from Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Xử lý đổi mật khẩu
     */
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response,
                                      User user, HttpSession session)
            throws ServletException, IOException {

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            // Validate inputs
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Vui lòng nhập mật khẩu cũ!\"}");
                return;
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Vui lòng nhập mật khẩu mới!\"}");
                return;
            }

            if (newPassword.length() < 6) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Mật khẩu mới phải có ít nhất 6 ký tự!\"}");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Mật khẩu mới không khớp!\"}");
                return;
            }

            // Change password
            boolean changed = authDAO.changePassword(user.getId(), oldPassword, newPassword);

            if (changed) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"message\": \"Đổi mật khẩu thành công!\"}");
            } else {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Mật khẩu cũ không đúng!\"}");
            }

        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi server: " + e.getMessage() + "\"}");
        }
    }
}
