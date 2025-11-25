# FlexiFile - Quick Start Guide

## 🚀 Chạy Local (Development)

```bash
# Clone project
git clone https://github.com/yourusername/FlexiFile.git
cd FlexiFile

# Build và chạy
mvn clean package
mvn cargo:run

# Truy cập: http://localhost:8080/FlexiFile
```

## 🐳 Deploy với Docker (Production)

```bash
# Bước 1: Tạo file .env
cp .env.example .env

# Bước 2: Chỉnh sửa .env - QUAN TRỌNG!
nano .env

# Thay đổi ít nhất những dòng này:
# VNPAY_RETURN_URL=https://YOUR-DOMAIN.com/FlexiFile/payment/vnpay_return
# CLOUDINARY_CLOUD_NAME=your-cloud-name
# CLOUDINARY_API_KEY=your-key
# CLOUDINARY_API_SECRET=your-secret

# Bước 3: Build Docker image
docker build -t flexifile:latest .

# Bước 4: Run container
docker run -d \
  -p 8080:8080 \
  --name flexifile \
  --env-file .env \
  --restart unless-stopped \
  flexifile:latest

# Check logs
docker logs -f flexifile
```

## ☁️ Deploy lên Render.com

```bash
# Bước 1: Build và push multi-arch image
./render.sh latest

# Bước 2: Tạo Web Service trên Render
# 1. Vào https://dashboard.render.com/
# 2. New → Web Service → Deploy an existing image
# 3. Image URL: your-dockerhub-username/flexifile:latest

# Bước 3: Thêm Environment Variables
# Trong phần Environment, thêm:
VNPAY_RETURN_URL=https://your-app.onrender.com/FlexiFile/payment/vnpay_return
VNPAY_TMN_CODE=18OA2Y8O
VNPAY_HASH_SECRET=AMIMAOJF9DKF9B36V6JBG78FF6MW5MZI
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-key
CLOUDINARY_API_SECRET=your-secret
# ... các biến khác từ .env.example

# Bước 4: Deploy!
# App sẽ available tại: https://your-app.onrender.com/FlexiFile
```

## 🧪 Test VNPay Integration (Sandbox)

```bash
# 1. Đảm bảo đang dùng sandbox config
VNPAY_PAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html

# 2. Truy cập app và đăng nhập

# 3. Thử nâng cấp tài khoản

# 4. Sử dụng test card:
# Card Number: 9704198526191432198
# Card Holder: NGUYEN VAN A
# Issued Date: 07/15
# OTP: 123456

# 5. Kiểm tra callback có hoạt động không
# Xem logs: docker logs -f flexifile
# Hoặc: tail -f logs/catalina.out
```

## 🔧 Troubleshooting

### ❌ Error Code 15: Invalid Checksum

**Đã fix!** Hash data không được URL encode.

Nếu vẫn gặp lỗi:
1. Check `VNPAY_HASH_SECRET` đúng chưa
2. Check logs: `docker logs flexifile | grep "Hash data"`
3. Xem chi tiết: [VNPAY_TROUBLESHOOTING.md](VNPAY_TROUBLESHOOTING.md)

### ❌ VNPay không callback

**Nguyên nhân:** `VNPAY_RETURN_URL` không đúng

```bash
# Check environment variable
docker exec flexifile env | grep VNPAY_RETURN_URL

# Phải là public URL, KHÔNG phải localhost:
# ✅ https://yourdomain.com/FlexiFile/payment/vnpay_return
# ❌ http://localhost:8080/FlexiFile/payment/vnpay_return
```

### ❌ File upload không hoạt động

Check Cloudinary credentials:
```bash
docker exec flexifile env | grep CLOUDINARY
```

### ❌ Docker build lỗi

```bash
# Clean và rebuild
docker system prune -a
docker build --no-cache -t flexifile:latest .
```

## 📚 Tài Liệu Đầy Đủ

- [README.md](README.md) - Tổng quan project
- [SETUP.md](SETUP.md) - Setup chi tiết
- [DEPLOYMENT.md](DEPLOYMENT.md) - Deploy to cloud platforms
- [VNPAY_TROUBLESHOOTING.md](VNPAY_TROUBLESHOOTING.md) - VNPay errors
- [.env.example](.env.example) - Environment variables template

## ⚡ Commands Thường Dùng

```bash
# Local development
mvn clean package          # Build WAR
mvn cargo:run             # Run với Tomcat
mvn clean                 # Clean build

# Docker
docker build -t flexifile:latest .                    # Build image
docker run -d -p 8080:8080 --env-file .env flexifile  # Run
docker logs -f flexifile                               # View logs
docker stop flexifile                                  # Stop
docker rm flexifile                                    # Remove
docker exec -it flexifile bash                         # Access shell

# Multi-arch build
./render.sh latest        # Build & push to Docker Hub
./render.sh v1.0.0       # Build with version tag
```

## 🎯 Deployment Checklist

Trước khi deploy production:

- [ ] Copy `.env.example` → `.env`
- [ ] Update `VNPAY_RETURN_URL` với domain thực tế
- [ ] Update Cloudinary credentials (production)
- [ ] Update VNPay credentials (production)
- [ ] Update Firebase service account key
- [ ] Update RabbitMQ URL
- [ ] Update email credentials
- [ ] Test locally với Docker
- [ ] Test VNPay sandbox
- [ ] Enable HTTPS/SSL
- [ ] Set up monitoring
- [ ] Configure backups

## 🆘 Support

Nếu gặp vấn đề:

1. Check logs: `docker logs flexifile`
2. Xem troubleshooting guides trong repo
3. Tạo issue trên GitHub
4. Contact VNPay support (cho VNPay issues)

---

**Note:** Đây là quick start guide. Xem các file MD khác để biết chi tiết.
