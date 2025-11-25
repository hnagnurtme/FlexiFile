# FlexiFile - Tóm Tắt Thay Đổi

## Thay Đổi Chính

### 1. VNPay Configuration - Sử dụng Environment Variables

**File:** `src/main/java/util/VNPayUtil.java`

Đã chuyển đổi từ hardcoded values sang environment variables:

```java
// Trước:
private static final String vnp_ReturnUrl = "http://localhost:8080/FlexiFile/payment/vnpay_return";

// Sau:
private static final String vnp_ReturnUrl = getEnvOrDefault(
    "VNPAY_RETURN_URL",
    "http://localhost:8080/FlexiFile/payment/vnpay_return"
);
```

**Environment Variables được hỗ trợ:**
- `VNPAY_PAY_URL` - URL của VNPay payment gateway
- `VNPAY_RETURN_URL` - URL callback sau khi thanh toán
- `VNPAY_TMN_CODE` - Merchant code
- `VNPAY_HASH_SECRET` - Secret key để hash

### 2. VNPay Hash Fix - Sửa lỗi Invalid Checksum (Error Code 15)

**Vấn đề:** Hash data bị URL encode → VNPay trả về error code 15

**File:** `src/main/java/util/VNPayUtil.java`

**Giải pháp:** Hash data KHÔNG được URL encode (chỉ query string mới encode)

```java
// ❌ SAI - Encode hash data
hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));

// ✅ ĐÚNG - Không encode hash data
hashData.append(fieldValue);
```

**Test cards cho Sandbox:**
- Card: 9704198526191432198
- Name: NGUYEN VAN A
- Date: 07/15
- OTP: 123456

### 3. Files Mới Được Tạo

1. **`.env.example`** - Template cho environment variables
2. **`DEPLOYMENT.md`** - Hướng dẫn deploy chi tiết cho các platform
3. **`VNPAY_TROUBLESHOOTING.md`** - Chi tiết troubleshooting VNPay errors
4. **`SUMMARY.md`** - File này

### 4. Files Đã Cập Nhật

1. **`README.md`** - Cải thiện structure, thêm thông tin về deployment
2. **`SETUP.md`** - Thêm hướng dẫn về environment variables
3. **`.gitignore`** - Thêm `.env` files
4. **`Dockerfile`** - Fixed Maven dependency resolution issue

## Cách Sử Dụng

### Development (Local)

```bash
# Không cần set environment variables
# App sẽ tự động dùng giá trị mặc định (localhost)
mvn cargo:run
```

### Production (Deploy)

```bash
# Bước 1: Tạo file .env
cp .env.example .env

# Bước 2: Chỉnh sửa .env với thông tin thực tế
# QUAN TRỌNG: Thay đổi VNPAY_RETURN_URL!
nano .env

# Bước 3: Deploy với Docker
docker build -t flexifile:latest .
docker run -d -p 8080:8080 --env-file .env flexifile:latest
```

### Deploy lên Render.com

```bash
# Bước 1: Build và push image
./render.sh latest

# Bước 2: Tạo Web Service trên Render Dashboard
# Bước 3: Thêm environment variables trong Dashboard:
VNPAY_RETURN_URL=https://your-app.onrender.com/FlexiFile/payment/vnpay_return
VNPAY_TMN_CODE=YOUR_CODE
VNPAY_HASH_SECRET=YOUR_SECRET
# ... và các biến khác
```

## Lợi Ích

1. ✅ **Flexibility**: Dễ dàng thay đổi config cho từng môi trường
2. ✅ **Security**: Không hardcode credentials trong code
3. ✅ **Portability**: Deploy lên bất kỳ platform nào dễ dàng
4. ✅ **Backward Compatible**: Vẫn hoạt động nếu không set env vars (dùng defaults)

## Checklist Deploy

- [ ] Copy `.env.example` thành `.env`
- [ ] Cập nhật `VNPAY_RETURN_URL` với domain thực tế
- [ ] Cập nhật Cloudinary credentials
- [ ] Cập nhật RabbitMQ URL
- [ ] Cập nhật email credentials
- [ ] Build Docker image
- [ ] Test VNPay callback

## Troubleshooting

### VNPay không callback được

**Nguyên nhân:** `VNPAY_RETURN_URL` không đúng

**Giải pháp:**
```bash
# Kiểm tra URL trong environment
docker exec flexifile env | grep VNPAY_RETURN_URL

# Update nếu cần
docker stop flexifile
docker rm flexifile
# Update .env file
docker run -d -p 8080:8080 --env-file .env flexifile:latest
```

### Environment variables không được load

**Kiểm tra:**
```bash
# Xem logs
docker logs flexifile

# Exec vào container
docker exec -it flexifile bash
echo $VNPAY_RETURN_URL
```

### VNPay Error Code 15 (Invalid Checksum)

**Nguyên nhân:** `VNPAY_RETURN_URL` không đúng

**Xem chi tiết:** [VNPAY_TROUBLESHOOTING.md](VNPAY_TROUBLESHOOTING.md)

```bash
# Kiểm tra URL trong environment
docker exec flexifile env | grep VNPAY_RETURN_URL

# Update nếu cần
docker stop flexifile
docker rm flexifile
# Update .env file
docker run -d -p 8080:8080 --env-file .env flexifile:latest
```

## Tài Liệu

- [README.md](README.md) - Tổng quan project
- [SETUP.md](SETUP.md) - Hướng dẫn setup chi tiết
- [DEPLOYMENT.md](DEPLOYMENT.md) - Hướng dẫn deploy cho các platform
- [VNPAY_TROUBLESHOOTING.md](VNPAY_TROUBLESHOOTING.md) - Troubleshooting VNPay integration
- [.env.example](.env.example) - Template environment variables
