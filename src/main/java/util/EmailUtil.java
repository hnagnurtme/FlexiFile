package util;

import java.util.Properties;

import config.EmailConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {
    
    public static boolean sendOTP(String toEmail, String otpCode) {
        try {
            // Config email properties
            Properties props = new Properties();
            props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
            props.put("mail.smtp.port", EmailConfig.SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            // Create session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(EmailConfig.FROM_EMAIL, EmailConfig.FROM_PASSWORD);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.FROM_EMAIL, EmailConfig.FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã OTP đặt lại mật khẩu - FlexFile");

            // Email body
            String htmlContent = getOTPEmailTemplate(otpCode);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getOTPEmailTemplate(String otpCode) {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<style>" +
            "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
            ".container { max-width: 600px; margin: 20px auto; background: white; border-radius: 8px; overflow: hidden; }" +
            ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }" +
            ".content { padding: 40px 30px; }" +
            ".otp-box { background: #f8f9fa; border: 2px dashed #0095f6; border-radius: 8px; padding: 20px; text-align: center; margin: 30px 0; }" +
            ".otp-code { font-size: 32px; font-weight: bold; color: #0095f6; letter-spacing: 8px; }" +
            ".footer { background: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 12px; }" +
            ".warning { color: #ed4956; font-size: 14px; margin-top: 20px; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<div class='container'>" +
            "<div class='header'>" +
            "<h1>🔐 Đặt lại mật khẩu</h1>" +
            "</div>" +
            "<div class='content'>" +
            "<p>Xin chào,</p>" +
            "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản FlexiFile của mình. Sử dụng mã OTP bên dưới để tiếp tục:</p>" +
            "<div class='otp-box'>" +
            "<div style='color: #666; font-size: 14px; margin-bottom: 10px;'>Mã OTP của bạn</div>" +
            "<div class='otp-code'>" + otpCode + "</div>" +
            "</div>" +
            "<p><strong>Lưu ý quan trọng:</strong></p>" +
            "<ul>" +
            "<li>Mã OTP này có hiệu lực trong <strong>5 phút</strong></li>" +
            "<li>Không chia sẻ mã này với bất kỳ ai</li>" +
            "<li>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này</li>" +
            "</ul>" +
            "<p class='warning'>⚠️ Nếu bạn không thực hiện yêu cầu này, vui lòng liên hệ với chúng tôi ngay lập tức.</p>" +
            "</div>" +
            "<div class='footer'>" +
            "<p>© 2025 FlexiFile. All rights reserved.</p>" +
            "<p>Email này được gửi tự động, vui lòng không trả lời.</p>" +
            "</div>" +
            "</div>" +
            "</body>" +
            "</html>";
    }

    /**
     * Gửi email chào mừng sau khi đăng ký
     */
    public static boolean sendWelcomeEmail(String toEmail, String fullName) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
            props.put("mail.smtp.port", EmailConfig.SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        EmailConfig.FROM_EMAIL, 
                        EmailConfig.FROM_PASSWORD
                    );
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.FROM_EMAIL, EmailConfig.FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Chào mừng đến với FlexiFile!");
            
            String htmlContent = "<!DOCTYPE html>" +
                "<html><body style='font-family: Arial, sans-serif;'>" +
                "<h2>Chào mừng " + fullName + "!</h2>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản FlexiFile.</p>" +
                "<p>Bạn có thể bắt đầu sử dụng dịch vụ chuyển đổi file ngay bây giờ.</p>" +
                "<p>Trân trọng,<br>Đội ngũ FlexiFile</p>" +
                "</body></html>";
            
            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            
            System.out.println("Welcome email sent to: " + toEmail);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
