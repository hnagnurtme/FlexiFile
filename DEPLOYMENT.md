# FlexiFile Deployment Guide

Hướng dẫn nhanh để deploy FlexiFile lên các nền tảng khác nhau.

## Chuẩn Bị

### 1. Tạo file `.env` từ template

```bash
cp .env.example .env
```

Sau đó chỉnh sửa `.env` với thông tin thực tế của bạn:

```env
# Cloudinary
CLOUDINARY_CLOUD_NAME=your-production-cloud-name
CLOUDINARY_API_KEY=your-production-api-key
CLOUDINARY_API_SECRET=your-production-api-secret

# RabbitMQ
RABBITMQ_URL=amqps://username:password@production-host/vhost

# Email
EMAIL_USERNAME=noreply@yourdomain.com
EMAIL_PASSWORD=your-app-password

# VNPay - QUAN TRỌNG: Thay đổi VNPAY_RETURN_URL theo domain của bạn!
VNPAY_PAY_URL=https://vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=https://yourdomain.com/FlexiFile/payment/vnpay_return
VNPAY_TMN_CODE=YOUR_PRODUCTION_TMN_CODE
VNPAY_HASH_SECRET=YOUR_PRODUCTION_HASH_SECRET
```

## Deploy với Docker

### Local Testing

```bash
# Build image
docker build -t flexifile:latest .

# Run với environment variables từ file .env
docker run -d \
  -p 8080:8080 \
  --name flexifile \
  --env-file .env \
  flexifile:latest

# Hoặc chỉ định từng biến
docker run -d \
  -p 8080:8080 \
  --name flexifile \
  -e VNPAY_RETURN_URL=https://yourdomain.com/FlexiFile/payment/vnpay_return \
  -e VNPAY_TMN_CODE=YOUR_TMN_CODE \
  -e VNPAY_HASH_SECRET=YOUR_HASH_SECRET \
  flexifile:latest
```

### Docker Compose

Tạo file `docker-compose.yml`:

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    env_file:
      - .env
    restart: unless-stopped

  worker:
    build: .
    command: ["java", "-cp", "/usr/local/tomcat/webapps/FlexiFile/WEB-INF/classes:/usr/local/tomcat/webapps/FlexiFile/WEB-INF/lib/*", "worker.WorkerLauncher"]
    env_file:
      - .env
    restart: unless-stopped
```

Chạy:

```bash
docker-compose up -d
```

## Deploy lên Cloud Platforms

### 1. Render.com

#### Bước 1: Build và push Docker image

```bash
# Build multi-arch image
./render.sh latest

# Hoặc với version cụ thể
./render.sh v1.0.0
```

#### Bước 2: Tạo Web Service trên Render

1. Đăng nhập vào [Render Dashboard](https://dashboard.render.com/)
2. Click "New" → "Web Service"
3. Chọn "Deploy an existing image from a registry"
4. Nhập Docker image: `your-dockerhub-username/flexifile:latest`
5. Cấu hình:
   - **Name:** flexifile
   - **Region:** Chọn region gần bạn
   - **Instance Type:** Free hoặc Starter
   - **Port:** 8080

#### Bước 3: Thêm Environment Variables

Trong phần "Environment Variables", thêm các biến sau:

```
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
RABBITMQ_URL=amqps://username:password@host/vhost
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
VNPAY_PAY_URL=https://vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=https://your-render-app.onrender.com/FlexiFile/payment/vnpay_return
VNPAY_TMN_CODE=YOUR_TMN_CODE
VNPAY_HASH_SECRET=YOUR_HASH_SECRET
```

**QUAN TRỌNG:** Thay `your-render-app.onrender.com` bằng URL thực tế của app trên Render!

#### Bước 4: Deploy

Click "Create Web Service" và đợi deploy hoàn tất.

### 2. Railway.app

```bash
# Install Railway CLI
npm i -g @railway/cli

# Login
railway login

# Initialize project
railway init

# Add environment variables
railway variables set VNPAY_RETURN_URL=https://your-app.railway.app/FlexiFile/payment/vnpay_return
railway variables set VNPAY_TMN_CODE=YOUR_TMN_CODE
railway variables set VNPAY_HASH_SECRET=YOUR_HASH_SECRET
# ... thêm các biến khác

# Deploy
railway up
```

### 3. Fly.io

Tạo file `fly.toml`:

```toml
app = "flexifile"

[build]
  dockerfile = "Dockerfile"

[env]
  PORT = "8080"

[[services]]
  internal_port = 8080
  protocol = "tcp"

  [[services.ports]]
    handlers = ["http"]
    port = 80

  [[services.ports]]
    handlers = ["tls", "http"]
    port = 443
```

Deploy:

```bash
# Install Fly CLI
curl -L https://fly.io/install.sh | sh

# Login
fly auth login

# Launch app
fly launch

# Set environment variables
fly secrets set VNPAY_RETURN_URL=https://your-app.fly.dev/FlexiFile/payment/vnpay_return
fly secrets set VNPAY_TMN_CODE=YOUR_TMN_CODE
fly secrets set VNPAY_HASH_SECRET=YOUR_HASH_SECRET
fly secrets set CLOUDINARY_CLOUD_NAME=your-cloud-name
fly secrets set CLOUDINARY_API_KEY=your-api-key
fly secrets set CLOUDINARY_API_SECRET=your-api-secret

# Deploy
fly deploy
```

### 4. AWS EC2

```bash
# SSH vào EC2 instance
ssh -i your-key.pem ubuntu@your-ec2-ip

# Install Docker
sudo apt update
sudo apt install docker.io -y
sudo systemctl start docker
sudo systemctl enable docker

# Pull và run image
sudo docker pull your-dockerhub-username/flexifile:latest

# Tạo file .env trên server
nano .env
# Paste nội dung environment variables

# Run container
sudo docker run -d \
  -p 80:8080 \
  --name flexifile \
  --env-file .env \
  --restart unless-stopped \
  your-dockerhub-username/flexifile:latest
```

### 5. Google Cloud Run

```bash
# Build và push lên Google Container Registry
gcloud builds submit --tag gcr.io/PROJECT-ID/flexifile

# Deploy với environment variables
gcloud run deploy flexifile \
  --image gcr.io/PROJECT-ID/flexifile \
  --platform managed \
  --region asia-southeast1 \
  --allow-unauthenticated \
  --set-env-vars "VNPAY_RETURN_URL=https://flexifile-xxx.a.run.app/FlexiFile/payment/vnpay_return" \
  --set-env-vars "VNPAY_TMN_CODE=YOUR_TMN_CODE" \
  --set-env-vars "VNPAY_HASH_SECRET=YOUR_HASH_SECRET" \
  --set-env-vars "CLOUDINARY_CLOUD_NAME=your-cloud-name"
```

## Kiểm Tra Sau Khi Deploy

### 1. Test ứng dụng

```bash
# Kiểm tra health
curl https://yourdomain.com/FlexiFile/

# Test VNPay return URL (nên trả về trang web, không phải 404)
curl https://yourdomain.com/FlexiFile/payment/vnpay_return
```

### 2. Kiểm tra logs

```bash
# Docker
docker logs -f flexifile

# Render
# Xem logs trong Dashboard

# Railway
railway logs

# Fly.io
fly logs

# Google Cloud Run
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=flexifile"
```

### 3. Test VNPay Integration

1. Đăng nhập vào app
2. Thử nâng cấp tài khoản
3. Thực hiện thanh toán test
4. Kiểm tra callback có hoạt động không

**Lưu ý:** Nhớ cập nhật `VNPAY_RETURN_URL` trong VNPay Dashboard nếu cần!

## Rollback Nếu Có Vấn Đề

### Docker

```bash
# Stop container hiện tại
docker stop flexifile
docker rm flexifile

# Run version cũ
docker run -d \
  -p 8080:8080 \
  --name flexifile \
  --env-file .env \
  your-dockerhub-username/flexifile:v1.0.0
```

### Render/Railway/Fly.io

Sử dụng Dashboard để rollback về deployment trước đó.

## Troubleshooting

### VNPay không callback được

1. Kiểm tra `VNPAY_RETURN_URL` có đúng không (phải là public URL)
2. Kiểm tra firewall/security group có block không
3. Kiểm tra logs xem có nhận được request từ VNPay không
4. Verify TMN Code và Hash Secret

**Xem thêm:** [VNPAY_TROUBLESHOOTING.md](VNPAY_TROUBLESHOOTING.md) để biết chi tiết về các lỗi VNPay

### File upload không hoạt động

1. Kiểm tra Cloudinary credentials
2. Kiểm tra network connectivity
3. Xem logs để biết lỗi cụ thể

### RabbitMQ worker không chạy

1. Deploy worker riêng (xem docker-compose example)
2. Kiểm tra RABBITMQ_URL
3. Verify queue name

## Monitoring

Khuyến nghị sử dụng:
- **Uptime monitoring:** UptimeRobot, Pingdom
- **Error tracking:** Sentry
- **Logs:** Loggly, Papertrail
- **Performance:** New Relic, Datadog

---

**Lưu ý Bảo Mật:**
- Không bao giờ commit file `.env` lên Git
- Sử dụng secrets management cho production
- Thường xuyên rotate credentials
- Enable HTTPS/SSL cho production
