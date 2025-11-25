# VNPay Integration - Troubleshooting Guide

## Lỗi Phổ Biến và Cách Khắc Phục

### ❌ Error Code 15: Invalid Checksum

**URL lỗi:** `https://sandbox.vnpayment.vn/paymentv2/Payment/Error.html?code=15`

**Nguyên nhân:** Chữ ký điện tử (Secure Hash) không hợp lệ

**Đã sửa:** Hash data KHÔNG được URL encode (chỉ query string mới encode)

**Kiểm tra:**

```bash
# 1. Check logs để xem hash data
# Nên thấy dạng:
# Hash data: vnp_Amount=10000000&vnp_Command=pay&vnp_CreateDate=20231123150000...

# 2. Verify Hash Secret đúng
echo $VNPAY_HASH_SECRET
# Hoặc check trong code nếu hardcode

# 3. Test lại payment flow
```

### ❌ Error Code 24: Transaction Failed (Giao dịch không thành công)

**Nguyên nhân:**
- Số tiền không hợp lệ (quá nhỏ hoặc quá lớn)
- Thời gian giao dịch đã hết hạn
- Thẻ test không đủ số dư (sandbox)

**Giải pháp:**
```java
// Đảm bảo amount > 0 và nhân 100
vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));

// Đảm bảo thời gian expire hợp lý (15 phút)
cld.add(Calendar.MINUTE, 15);
```

### ❌ Error Code 07: Trừ tiền thành công, Giao dịch bị nghi ngờ

**Nguyên nhân:**
- IP Address không đúng
- Order ID bị trùng

**Giải pháp:**
```java
// 1. Đảm bảo lấy đúng IP
public static String getIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-FORWARDED-FOR");
    if (ipAddress == null) {
        ipAddress = request.getRemoteAddr();
    }
    return ipAddress;
}

// 2. Order ID phải unique
String orderId = System.currentTimeMillis() + "_" + userId;
```

### ❌ Error Code 99: Các lỗi khác

**Nguyên nhân:**
- Thiếu hoặc sai tham số bắt buộc
- Format dữ liệu không đúng

**Kiểm tra các tham số bắt buộc:**
```java
// Required params
vnp_Version     = "2.1.0"
vnp_Command     = "pay"
vnp_TmnCode     = "YOUR_TMN_CODE"
vnp_Amount      = amount * 100 (VND)
vnp_CurrCode    = "VND"
vnp_TxnRef      = unique order id
vnp_OrderInfo   = mô tả đơn hàng
vnp_ReturnUrl   = callback URL
vnp_IpAddr      = client IP
vnp_CreateDate  = "yyyyMMddHHmmss"
```

### ❌ Callback không được gọi / Return URL không hoạt động

**Nguyên nhân:**
- `VNPAY_RETURN_URL` không đúng
- URL không accessible từ internet (localhost)
- Firewall block VNPay

**Giải pháp:**

```bash
# 1. Kiểm tra VNPAY_RETURN_URL
echo $VNPAY_RETURN_URL
# Phải là URL public, không phải localhost

# 2. Test URL từ bên ngoài
curl https://yourdomain.com/FlexiFile/payment/vnpay_return
# Phải trả về HTTP 200, không được 404

# 3. Check logs Tomcat
tail -f /usr/local/tomcat/logs/catalina.out
```

**Production URL đúng:**
```env
# ✅ ĐÚNG - Public URL
VNPAY_RETURN_URL=https://yourdomain.com/FlexiFile/payment/vnpay_return

# ❌ SAI - Localhost (VNPay không gọi được)
VNPAY_RETURN_URL=http://localhost:8080/FlexiFile/payment/vnpay_return

# ❌ SAI - IP private
VNPAY_RETURN_URL=http://192.168.1.100/FlexiFile/payment/vnpay_return
```

### ❌ Production vs Sandbox

**Lưu ý:** Sandbox và Production sử dụng khác nhau:

```env
# SANDBOX (Testing)
VNPAY_PAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_TMN_CODE=YOUR_SANDBOX_TMN_CODE
VNPAY_HASH_SECRET=YOUR_SANDBOX_HASH_SECRET

# PRODUCTION
VNPAY_PAY_URL=https://vnpayment.vn/paymentv2/vpcpay.html
VNPAY_TMN_CODE=YOUR_PRODUCTION_TMN_CODE
VNPAY_HASH_SECRET=YOUR_PRODUCTION_HASH_SECRET
```

**Test cards cho Sandbox:**

| Bank | Card Number | Card Holder | Issued Date | OTP |
|------|-------------|-------------|-------------|-----|
| NCB | 9704198526191432198 | NGUYEN VAN A | 07/15 | 123456 |
| VIETCOMBANK | 9704061080001234567 | NGUYEN VAN A | 07/15 | 123456 |
| SACOMBANK | 9704060070001234567 | NGUYEN VAN A | 07/15 | 123456 |

## Debug Tips

### 1. Enable Detailed Logging

File code đã có `System.out.println` để debug:

```java
// Creating payment
System.out.println("Creating payment - Hash data: " + hashDataStr);

// Verifying return
System.out.println("Verify - Hash data string: " + hashData);
```

Check logs:
```bash
# Docker
docker logs -f flexifile | grep "Hash data"

# Local Tomcat
tail -f logs/catalina.out | grep "Hash data"
```

### 2. Compare Hash Data

**Hash data phải:**
- Được sort theo alphabet
- KHÔNG được URL encode
- Không có khoảng trắng thừa
- Format: `key1=value1&key2=value2&key3=value3`

**Example hash data đúng:**
```
vnp_Amount=10000000&vnp_BankCode=NCB&vnp_Command=pay&vnp_CreateDate=20231123150000&vnp_CurrCode=VND&vnp_IpAddr=127.0.0.1&vnp_Locale=vn&vnp_OrderInfo=Payment for order 12345&vnp_OrderType=other&vnp_ReturnUrl=https://yourdomain.com/FlexiFile/payment/vnpay_return&vnp_TmnCode=18OA2Y8O&vnp_TxnRef=12345&vnp_Version=2.1.0
```

### 3. Manual Hash Testing

Test hash locally:

```bash
# Install Java (if needed)
# Create test file
cat > TestHash.java << 'EOF'
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class TestHash {
    public static void main(String[] args) {
        String secret = "AMIMAOJF9DKF9B36V6JBG78FF6MW5MZI";
        String data = "vnp_Amount=10000000&vnp_Command=pay&vnp_TmnCode=18OA2Y8O";

        String hash = hmacSHA512(secret, data);
        System.out.println("Hash: " + hash);
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
EOF

javac TestHash.java
java TestHash
```

### 4. Check Environment Variables

```bash
# Trong container
docker exec flexifile env | grep VNPAY

# Hoặc thêm log trong code
System.out.println("VNPAY_RETURN_URL: " + System.getenv("VNPAY_RETURN_URL"));
System.out.println("vnp_ReturnUrl being used: " + vnp_ReturnUrl);
```

## Payment Flow Checklist

- [ ] TMN Code và Hash Secret đúng (check với VNPay dashboard)
- [ ] Return URL là public URL, accessible từ internet
- [ ] Amount > 0 và đã nhân 100
- [ ] Order ID unique cho mỗi transaction
- [ ] CreateDate format đúng: `yyyyMMddHHmmss`
- [ ] ExpireDate hợp lý (> CreateDate, <= 15 phút)
- [ ] IP Address lấy đúng (X-FORWARDED-FOR hoặc RemoteAddr)
- [ ] Hash data KHÔNG URL encode
- [ ] Query string có URL encode
- [ ] Tham số được sort theo alphabet trước khi hash

## Test Payment Flow

```bash
# 1. Tạo payment
curl -X POST http://localhost:8080/FlexiFile/payment/create \
  -d "amount=100000" \
  -d "orderDesc=Test payment" \
  -b "session_cookie=xxx"

# 2. Kiểm tra redirect URL
# Copy URL và mở trong browser

# 3. Sử dụng test card
# Card: 9704198526191432198
# Name: NGUYEN VAN A
# Date: 07/15
# OTP: 123456

# 4. Kiểm tra callback
# VNPay sẽ redirect về VNPAY_RETURN_URL với các params
# Check logs để verify hash
```

## VNPay Documentation

- [VNPay Sandbox](https://sandbox.vnpayment.vn/apis/)
- [Integration Guide](https://sandbox.vnpayment.vn/apis/docs/huong-dan-tich-hop/)
- [Error Codes](https://sandbox.vnpayment.vn/apis/docs/bang-ma-loi/)

## Support

Nếu vẫn gặp lỗi sau khi thử các bước trên:

1. Check VNPay Dashboard xem có log giao dịch không
2. Contact VNPay support với:
   - TMN Code
   - Transaction ID (vnp_TxnRef)
   - Timestamp
   - Error code
3. Share logs (ẩn sensitive data) để được hỗ trợ

---

**Lưu ý:** File này được tạo sau khi fix lỗi Invalid Checksum (code 15). Hash data đã được sửa để KHÔNG URL encode.
