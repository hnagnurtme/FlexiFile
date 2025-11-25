# FlexiFile - Hướng Dẫn Cài Đặt & Triển Khai

Tài liệu này hướng dẫn chi tiết cách cài đặt và chạy ứng dụng FlexiFile trong môi trường development và production.

## Mục Lục

- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Cài Đặt Development](#cài-đặt-development)
- [Cấu Hình Ứng Dụng](#cấu-hình-ứng-dụng)
- [Chạy Ứng Dụng](#chạy-ứng-dụng)
- [Chạy Worker Consumer](#chạy-worker-consumer)
- [Docker Deployment](#docker-deployment)
- [Production Deployment](#production-deployment)
- [Xử Lý Lỗi](#xử-lý-lỗi)

---

## Yêu Cầu Hệ Thống

### Phần Mềm Cần Thiết

#### 1. Java Development Kit (JDK) 17+
```bash
# Kiểm tra version
java -version

# Output mong muốn: openjdk version "17.x.x" hoặc cao hơn
```

**Download:**
- [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
- [OpenJDK (Adoptium)](https://adoptium.net/)

#### 2. Apache Maven 3.6+
```bash
# Kiểm tra version
mvn -version

# Output mong muốn: Apache Maven 3.6.0 hoặc cao hơn
```

**Download:** [Apache Maven](https://maven.apache.org/download.cgi)

#### 3. Apache Tomcat 10.x (Optional)
- Dự án đã tích hợp sẵn embedded Tomcat qua Maven Cargo plugin
- Có thể dùng standalone Tomcat nếu muốn
- **Download:** [Apache Tomcat 10](https://tomcat.apache.org/download-10.cgi)

#### 4. Docker & Docker Compose (Cho deployment)
```bash
# Kiểm tra Docker
docker --version
docker-compose --version
```

**Download:** [Docker Desktop](https://www.docker.com/get-started)

---

### Dịch Vụ Bên Ngoài

Bạn cần đăng ký và lấy credentials từ các dịch vụ sau:

#### 1. **Firebase (Database)**
- Tạo project tại [Firebase Console](https://console.firebase.google.com/)
- Bật **Firestore Database**
- Tạo **Service Account Key** (file JSON)
- **Mục đích:** Lưu trữ users, payment history, convert history

#### 2. **Cloudinary (File Storage)**
- Đăng ký tại [Cloudinary](https://cloudinary.com/)
- Lấy: Cloud Name, API Key, API Secret
- **Mục đích:** Upload và lưu trữ files (avatar, source files, converted files)

#### 3. **CloudAMQP (RabbitMQ)**
- Đăng ký tại [CloudAMQP](https://www.cloudamqp.com/)
- Tạo instance (free tier OK cho dev)
- Lấy **AMQP URL**
- **Mục đích:** Queue để xử lý file conversion bất đồng bộ

#### 4. **VNPay (Payment Gateway)**
- Đăng ký tại [VNPay](https://vnpay.vn/)
- Lấy: TMN Code, Hash Secret
- **Sandbox URL:** `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html`
- **Production URL:** `https://vnpayment.vn/paymentv2/vpcpay.html`
- **Mục đích:** Xử lý thanh toán online

#### 5. **Gmail SMTP (Email Service)**
- Dùng tài khoản Gmail
- Bật **2-Factor Authentication**
- Tạo **App Password** tại [App Passwords](https://myaccount.google.com/apppasswords)
- **Mục đích:** Gửi email chào mừng, OTP, notification

---

## Cài Đặt Development

### 1. Clone Repository

```bash
git clone https://github.com/hnagnurtme/FlexiFile.git
cd FlexiFile
```

### 2. Cấu Trúc Thư Mục

```
FlexiFile/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── config/              # Cấu hình hệ thống
│   │   │   ├── controller/          # Servlet controllers
│   │   │   ├── model/
│   │   │   │   ├── bean/            # Data models (User, FileJob, Payment...)
│   │   │   │   ├── bo/              # Business Objects (logic tầng giữa)
│   │   │   │   └── dao/             # Data Access Objects (truy cập DB)
│   │   │   ├── util/                # Utilities (VNPay, Cloudinary, Email...)
│   │   │   └── worker/              # RabbitMQ Worker Consumer
│   │   │       ├── WorkerConsumer.java    # Main worker process
│   │   │       └── ConvertWorker.java     # File conversion logic
│   │   ├── resources/
│   │   │   └── serviceAccountKey.json     # Firebase credentials (GIT IGNORE!)
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml          # Servlet mappings
│   │       ├── jsp/                 # JSP views
│   │       ├── style/               # CSS files
│   │       └── js/                  # JavaScript files
├── pom.xml                           # Maven dependencies
├── Dockerfile                        # Docker build config
├── .env.example                      # Environment variables template
├── SETUP.md                          # Tài liệu này
└── README.md                         # Project overview
```

---

## Cấu Hình Ứng Dụng

### 1. Firebase Configuration

#### Bước 1: Tạo Service Account Key

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Chọn project hoặc tạo mới
3. **Project Settings** → **Service Accounts**
4. Click **"Generate New Private Key"**
5. Download file JSON

#### Bước 2: Cấu hình trong project

```bash
# Copy file vào thư mục resources
cp ~/Downloads/flexifile-xxxxx-firebase-adminsdk-xxxxx.json \
   src/main/resources/serviceAccountKey.json
```

#### Bước 3: Thêm vào .gitignore (QUAN TRỌNG!)

```bash
echo "src/main/resources/serviceAccountKey.json" >> .gitignore
```

**⚠️ CẢNH BÁO:** Không bao giờ commit file này lên Git!

---

### 2. Cloudinary Configuration

**File:** `src/main/java/util/CloudinaryConfig.java`

```java
public class CloudinaryConfig {
    public static final String CLOUD_NAME = "your-cloud-name";
    public static final String API_KEY    = "123456789012345";
    public static final String API_SECRET = "abcdefghijklmnopqrstuvwxyz";

    // Folder structure
    public static final String MAIN_FOLDER = "FlexFile";
    public static final String SOURCE_FOLDER = MAIN_FOLDER + "/SourceFile";
    public static final String CONVERTED_FOLDER = MAIN_FOLDER + "/ConvertedFile";
    public static final String AVATARS_FOLDER = MAIN_FOLDER + "/Avatars";
}
```

**Lấy credentials:**
1. Login vào [Cloudinary Dashboard](https://cloudinary.com/console)
2. Copy **Cloud Name**, **API Key**, **API Secret**

---

### 3. RabbitMQ Configuration

**File:** `src/main/java/config/RabbitMQConfig.java`

```java
public class RabbitMQConfig {
    public static final String CLOUDAMQP_URL =
        "amqps://username:password@hostname.rmq.cloudamqp.com/vhost";
}
```

**Lấy AMQP URL:**
1. Login vào [CloudAMQP](https://customer.cloudamqp.com/)
2. Chọn instance
3. Copy **AMQP URL**

**Format URL:** `amqps://username:password@host/vhost`

---

### 4. Email Configuration

**File:** `src/main/java/config/EmailConfig.java`

```java
public class EmailConfig {
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final String SMTP_PORT = "587";
    public static final String FROM_EMAIL = "your-email@gmail.com";
    public static final String FROM_PASSWORD = "your-16-char-app-password";
    public static final String FROM_NAME = "FlexiFile Support";
}
```

**Tạo Gmail App Password:**
1. Bật **2-Factor Authentication** cho Gmail
2. Vào [App Passwords](https://myaccount.google.com/apppasswords)
3. Chọn **Mail** → **Generate**
4. Copy 16-ký tự app password

---

### 5. VNPay Configuration

**File:** `src/main/java/util/VNPayUtil.java`

```java
public class VNPayUtil {
    // SANDBOX (Development)
    private static final String vnp_PayUrl =
        "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    private static final String vnp_ReturnUrl =
        "http://localhost:8080/FlexiFile/payment/vnpay_return";

    private static final String vnp_TmnCode = "YOUR_SANDBOX_TMN_CODE";
    private static final String vnp_HashSecret = "YOUR_SANDBOX_HASH_SECRET";

    // PRODUCTION
    // private static final String vnp_PayUrl =
    //     "https://vnpayment.vn/paymentv2/vpcpay.html";
    // private static final String vnp_ReturnUrl =
    //     "https://yourdomain.com/FlexiFile/payment/vnpay_return";
}
```

**Lưu ý:**
- Development: dùng sandbox credentials
- Production: đổi sang production credentials và URL

---

### 6. Environment Variables (Khuyến Nghị cho Production)

Thay vì hardcode credentials, dùng environment variables:

**Tạo file `.env`:**

```bash
cp .env.example .env
```

**Chỉnh sửa `.env`:**

```env
# Cloudinary
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=123456789012345
CLOUDINARY_API_SECRET=abcdefghijklmnopqrstuvwxyz

# RabbitMQ
RABBITMQ_URL=amqps://username:password@host/vhost

# Email
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# VNPay
VNPAY_TMN_CODE=YOUR_TMN_CODE
VNPAY_HASH_SECRET=YOUR_HASH_SECRET
VNPAY_RETURN_URL=http://localhost:8080/FlexiFile/payment/vnpay_return
```

**Sửa config classes để đọc từ env:**

```java
public class CloudinaryConfig {
    public static final String CLOUD_NAME =
        System.getenv().getOrDefault("CLOUDINARY_CLOUD_NAME", "default-value");
    public static final String API_KEY =
        System.getenv().getOrDefault("CLOUDINARY_API_KEY", "default-value");
    public static final String API_SECRET =
        System.getenv().getOrDefault("CLOUDINARY_API_SECRET", "default-value");
}
```

---

## Chạy Ứng Dụng

### Method 1: Maven Cargo (Embedded Tomcat) - Khuyến Nghị

Cách dễ nhất để chạy trong development:

```bash
# Clean và build project
mvn clean package

# Chạy với embedded Tomcat
mvn cargo:run
```

**Ứng dụng sẽ chạy tại:** `http://localhost:8080/FlexiFile`

**Dừng server:** Nhấn `Ctrl + C`

**Options:**

```bash
# Skip tests (nhanh hơn)
mvn clean package -DskipTests
mvn cargo:run

# Debug mode (port 5005)
mvn cargo:run -Dcargo.jvmargs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```

---

### Method 2: Standalone Tomcat

#### Bước 1: Build WAR file

```bash
mvn clean package
```

File WAR sẽ được tạo tại: `target/FlexiFile.war`

#### Bước 2: Deploy vào Tomcat

```bash
# Copy WAR vào webapps
cp target/FlexiFile.war /path/to/tomcat/webapps/

# Hoặc trên macOS (nếu cài qua Homebrew)
cp target/FlexiFile.war /usr/local/Cellar/tomcat/10.x.x/libexec/webapps/
```

#### Bước 3: Start Tomcat

```bash
# Linux/macOS
/path/to/tomcat/bin/startup.sh

# Windows
/path/to/tomcat/bin/startup.bat

# macOS (Homebrew)
brew services start tomcat
```

#### Bước 4: Truy cập

`http://localhost:8080/FlexiFile`

#### Stop Tomcat

```bash
# Linux/macOS
/path/to/tomcat/bin/shutdown.sh

# macOS (Homebrew)
brew services stop tomcat
```

---

### Method 3: Docker

#### Build image

```bash
docker build -t flexifile:latest .
```

#### Run container

```bash
docker run -d \
  -p 8080:8080 \
  --name flexifile \
  -e CLOUDINARY_CLOUD_NAME=your-cloud-name \
  -e CLOUDINARY_API_KEY=your-api-key \
  -e CLOUDINARY_API_SECRET=your-api-secret \
  -e RABBITMQ_URL=amqps://user:pass@host/vhost \
  flexifile:latest
```

#### Xem logs

```bash
docker logs -f flexifile
```

#### Stop & remove

```bash
docker stop flexifile
docker rm flexifile
```

---

## Chạy Worker Consumer

**Worker Consumer** là tiến trình xử lý file conversion bất đồng bộ qua RabbitMQ. Đây là **THÀNH PHẦN BẮT BUỘC** để file conversion hoạt động.

### Kiến Trúc

```
[Web App] → [RabbitMQ Queue] → [Worker Consumer] → [Cloudinary]
    ↓                                    ↓
  Gửi job                          Xử lý conversion
                                   Cập nhật status
```

### Cách Chạy Worker

#### Method 1: Chạy Trực Tiếp với Maven

**Bước 1: Build project**

```bash
mvn clean package
```

**Bước 2: Chạy Worker**

```bash
cd target/FlexiFile

java -cp "WEB-INF/classes:WEB-INF/lib/*" worker.WorkerConsumer
```

**Output mong đợi:**

```
WorkerConsumer listening for new jobs...
```

**Lưu ý:**
- Worker sẽ chạy liên tục và lắng nghe queue
- Nhấn `Ctrl + C` để dừng
- Nên mở terminal riêng cho worker

#### Method 2: Chạy với Script (Khuyến Nghị)

**Tạo script `run-worker.sh`:**

```bash
#!/bin/bash

echo "Building FlexiFile..."
mvn clean package -DskipTests

echo "Starting Worker Consumer..."
cd target/FlexiFile

java -cp "WEB-INF/classes:WEB-INF/lib/*" worker.WorkerConsumer
```

**Chạy:**

```bash
chmod +x run-worker.sh
./run-worker.sh
```

#### Method 3: Chạy với Docker

**Tạo file `docker-compose.yml`:**

```yaml
version: '3.8'

services:
  # Web Application
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - CLOUDINARY_CLOUD_NAME=${CLOUDINARY_CLOUD_NAME}
      - CLOUDINARY_API_KEY=${CLOUDINARY_API_KEY}
      - CLOUDINARY_API_SECRET=${CLOUDINARY_API_SECRET}
      - RABBITMQ_URL=${RABBITMQ_URL}
    restart: unless-stopped

  # Worker Consumer
  worker:
    build: .
    command: >
      java -cp
      "/usr/local/tomcat/webapps/FlexiFile/WEB-INF/classes:/usr/local/tomcat/webapps/FlexiFile/WEB-INF/lib/*"
      worker.WorkerConsumer
    environment:
      - CLOUDINARY_CLOUD_NAME=${CLOUDINARY_CLOUD_NAME}
      - CLOUDINARY_API_KEY=${CLOUDINARY_API_KEY}
      - CLOUDINARY_API_SECRET=${CLOUDINARY_API_SECRET}
      - RABBITMQ_URL=${RABBITMQ_URL}
    restart: unless-stopped
```

**Chạy cả app và worker:**

```bash
docker-compose up -d
```

**Xem logs:**

```bash
# Logs của app
docker-compose logs -f app

# Logs của worker
docker-compose logs -f worker

# Logs của cả hai
docker-compose logs -f
```

#### Method 4: Chạy Background với nohup

```bash
# Build
mvn clean package -DskipTests

# Chạy background
cd target/FlexiFile
nohup java -cp "WEB-INF/classes:WEB-INF/lib/*" worker.WorkerConsumer > worker.log 2>&1 &

# Lưu PID
echo $! > worker.pid

# Xem logs
tail -f worker.log

# Dừng worker
kill $(cat worker.pid)
```

### Kiểm Tra Worker Hoạt Động

1. **Upload file** qua web interface
2. **Check worker logs** - sẽ thấy:
   ```
   Job DONE: job-id-xyz -> https://cloudinary.com/result-url
   ```
3. **Check file status** trong database hoặc UI
4. **Download converted file**

### Worker Configuration

**File:** `src/main/java/worker/WorkerConsumer.java`

```java
public class WorkerConsumer {
    private static final String QUEUE_NAME = "convert_jobs";
    private static final int THREAD_POOL_SIZE = 5;  // Số job xử lý song song

    // Cấu hình:
    // - basicQos(5): Consumer nhận tối đa 5 unacked messages
    // - ThreadPool 5: Xử lý 5 jobs đồng thời
}
```

**Tuning Performance:**

- Tăng `THREAD_POOL_SIZE` để xử lý nhiều jobs đồng thời
- **Lưu ý:** Cloudinary có rate limit, không nên set quá cao

---

## Docker Deployment

### Basic Docker Build

```bash
# Build image
docker build -t flexifile:latest .

# Run container
docker run -d \
  -p 8080:8080 \
  --name flexifile \
  --env-file .env \
  flexifile:latest

# Xem logs
docker logs -f flexifile
```

### Multi-Architecture Build (ARM + AMD)

**Sử dụng script `render.sh`:**

```bash
#!/bin/bash
TAG=${1:-latest}

docker buildx create --use --name multiarch-builder || true

docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --tag yourusername/flexifile:$TAG \
  --push \
  .
```

**Chạy:**

```bash
chmod +x render.sh
./render.sh v1.0.0
```

### Docker Compose (App + Worker)

**File `docker-compose.yml`** (đã có ở trên)

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# Rebuild and restart
docker-compose up -d --build

# Scale workers (3 instances)
docker-compose up -d --scale worker=3
```

---

## Production Deployment

### Pre-Deployment Checklist

- [ ] **Remove all hardcoded credentials**
- [ ] **Use environment variables for all secrets**
- [ ] **Add `serviceAccountKey.json` to `.gitignore`**
- [ ] **Enable HTTPS/SSL**
- [ ] **Configure firewall rules**
- [ ] **Set up database backups (Firebase export)**
- [ ] **Configure log rotation**
- [ ] **Use production VNPay credentials**
- [ ] **Use production Firebase project**
- [ ] **Implement rate limiting**
- [ ] **Set up monitoring (logs, uptime)**
- [ ] **Test payment flow thoroughly**
- [ ] **Configure proper CORS**

### Environment Setup

**Production `.env`:**

```env
# Cloudinary (Production)
CLOUDINARY_CLOUD_NAME=prod-cloud-name
CLOUDINARY_API_KEY=prod-api-key
CLOUDINARY_API_SECRET=prod-api-secret

# RabbitMQ (Production Instance)
RABBITMQ_URL=amqps://user:pass@prod-host/vhost

# Email (Production Domain)
EMAIL_USERNAME=noreply@yourdomain.com
EMAIL_PASSWORD=production-app-password

# VNPay (Production)
VNPAY_PAY_URL=https://vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=https://yourdomain.com/FlexiFile/payment/vnpay_return
VNPAY_TMN_CODE=PRODUCTION_TMN_CODE
VNPAY_HASH_SECRET=PRODUCTION_HASH_SECRET
```

### Deployment Options

#### Option 1: VPS/Dedicated Server

**Install dependencies:**

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install Docker
sudo apt install docker.io docker-compose -y

# Install Nginx
sudo apt install nginx -y
```

**Deploy application:**

```bash
# Clone repo
git clone https://github.com/yourusername/FlexiFile.git
cd FlexiFile

# Setup environment
cp .env.example .env
nano .env  # Edit with production values

# Build and deploy
docker-compose up -d
```

#### Option 2: Cloud Platforms

**Render.com:**

1. Push image to Docker Hub:
   ```bash
   ./render.sh latest
   ```

2. Create Web Service on Render:
   - **Docker Image:** `yourusername/flexifile:latest`
   - **Port:** `8080`
   - Add environment variables

**AWS EC2:**

```bash
# SSH to instance
ssh -i key.pem ubuntu@ec2-xxx.compute.amazonaws.com

# Install Docker
sudo apt update
sudo apt install docker.io -y

# Pull and run
docker pull yourusername/flexifile:latest
docker run -d -p 80:8080 --env-file .env flexifile:latest
```

**Google Cloud Run:**

```bash
# Build and push to GCR
gcloud builds submit --tag gcr.io/PROJECT-ID/flexifile

# Deploy
gcloud run deploy flexifile \
  --image gcr.io/PROJECT-ID/flexifile \
  --platform managed \
  --allow-unauthenticated
```

### Nginx Reverse Proxy

**File `/etc/nginx/sites-available/flexifile`:**

```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;

    # Max upload size
    client_max_body_size 100M;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Timeouts for long uploads
        proxy_connect_timeout 600;
        proxy_send_timeout 600;
        proxy_read_timeout 600;
    }
}
```

**Enable site:**

```bash
sudo ln -s /etc/nginx/sites-available/flexifile /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### SSL/TLS (Let's Encrypt)

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Get certificate
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Auto-renewal (crontab)
sudo crontab -e
# Add line:
0 0 1 * * certbot renew --quiet
```

---

## Xử Lý Lỗi

### 1. Port 8080 Already in Use

```bash
# Tìm process đang dùng port
lsof -i :8080

# Hoặc
netstat -tuln | grep 8080

# Kill process
kill -9 <PID>
```

### 2. Maven Build Failed

```bash
# Clear cache và rebuild
mvn clean install -U

# Skip tests
mvn clean package -DskipTests

# Force update dependencies
mvn clean install -U -DskipTests
```

### 3. Firebase Connection Error

**Nguyên nhân:**
- File `serviceAccountKey.json` không đúng vị trí
- Firestore chưa được enable

**Giải pháp:**

```bash
# Kiểm tra file tồn tại
ls -la src/main/resources/serviceAccountKey.json

# Kiểm tra quyền đọc
chmod 644 src/main/resources/serviceAccountKey.json
```

- Vào Firebase Console → Firestore Database → Create Database

### 4. RabbitMQ Connection Failed

**Lỗi:** `Connection refused` hoặc `Authentication failed`

**Giải pháp:**

1. Kiểm tra format URL:
   ```
   amqps://username:password@hostname/vhost
   ```

2. Check instance status tại CloudAMQP dashboard

3. Test connection:
   ```bash
   curl https://customer.cloudamqp.com/api/instances/<instance-id>/aliveness-test
   ```

### 5. Worker Không Xử Lý Job

**Symptoms:**
- Upload file thành công
- Status mãi là "processing"
- Worker không log gì

**Kiểm tra:**

```bash
# 1. Worker có đang chạy?
ps aux | grep WorkerConsumer

# 2. RabbitMQ có job không?
# Vào CloudAMQP Dashboard → Queues → Check message count

# 3. Worker logs
tail -f worker.log
```

**Giải pháp:**
- Restart worker
- Check RabbitMQ credentials
- Check Cloudinary credentials

### 6. File Conversion Failed

**Lỗi trong worker logs:**

```
Error converting file: Unsupported format
```

**Supported formats:**
- **Document:** PDF, DOCX, XLSX, PPTX, TXT
- **Image:** JPG, PNG, GIF, BMP, WEBP
- **Video:** MP4, AVI, MOV (qua Cloudinary)

**Check dependencies:**

```bash
# POI (Office files)
# PDFBox (PDF)
# Cloudinary (Images, Videos)
```

### 7. VNPay Payment Returns Error

**Common errors:**

| Code | Meaning | Solution |
|------|---------|----------|
| 07 | Trừ tiền thành công nhưng giao dịch bị nghi ngờ | Liên hệ VNPay |
| 09 | Thẻ chưa đăng ký Internet Banking | Dùng thẻ khác |
| 10 | Sai OTP | Nhập lại |
| 11 | Timeout | Thử lại |
| 12 | Thẻ bị khóa | Liên hệ ngân hàng |
| 24 | Hủy giao dịch | User hủy |
| 51 | Không đủ tiền | Nạp thêm |
| 97 | Sai checksum | Check HashSecret |

**Debug:**

```java
// Enable logging trong VNPayReturnServlet
System.out.println("Received hash: " + vnp_SecureHash);
System.out.println("Calculated hash: " + calculatedHash);
```

### 8. Email Not Sending

**Lỗi:** `AuthenticationFailedException`

**Giải pháp:**

1. Check Gmail App Password (16 chars, không có dấu cách)
2. Bật 2FA cho Gmail
3. Check SMTP settings:
   - Host: `smtp.gmail.com`
   - Port: `587`
   - TLS: Enabled

### 9. Cloudinary Upload Failed

**Lỗi:** `401 Unauthorized`

**Giải pháp:**

```java
// Verify credentials
System.out.println("Cloud Name: " + CloudinaryConfig.CLOUD_NAME);
System.out.println("API Key: " + CloudinaryConfig.API_KEY);
// DON'T print API_SECRET in production!
```

- Check credentials tại [Cloudinary Dashboard](https://cloudinary.com/console)

### 10. Docker Image Build Failed

**Lỗi:** `error building at STEP "RUN mvn clean package"`

**Giải pháp:**

```bash
# Build locally first
mvn clean package -DskipTests

# Then build Docker
docker build --no-cache -t flexifile:latest .
```

---

## Monitoring & Logs

### Application Logs

```bash
# Docker logs
docker logs -f flexifile
docker-compose logs -f app
docker-compose logs -f worker

# Tomcat logs
tail -f /path/to/tomcat/logs/catalina.out

# Worker logs
tail -f worker.log
```

### Database (Firestore Console)

Truy cập [Firebase Console](https://console.firebase.google.com/) để:
- Xem/sửa user data
- Check file job records
- Monitor payment history
- Debug authentication issues

### RabbitMQ Monitoring

Truy cập [CloudAMQP Dashboard](https://customer.cloudamqp.com/) để:
- Check queue depth
- Monitor message rates
- View connection stats

### Health Check Endpoints

```bash
# App health
curl http://localhost:8080/FlexiFile/

# Test login
curl -X POST http://localhost:8080/FlexiFile/login \
  -d "email=test@example.com&password=password123"

# Test API
curl http://localhost:8080/FlexiFile/api/job-status?jobId=xxx
```

---

## Performance Tuning

### JVM Options

```bash
# Tăng heap size
export MAVEN_OPTS="-Xms512m -Xmx2048m"

# Docker run with JVM options
docker run -d \
  -e JAVA_OPTS="-Xms512m -Xmx2048m" \
  flexifile:latest
```

### Worker Scaling

```bash
# Chạy nhiều worker instances
docker-compose up -d --scale worker=3

# Hoặc chạy manual
./run-worker.sh &  # Instance 1
./run-worker.sh &  # Instance 2
./run-worker.sh &  # Instance 3
```

### Database Optimization

- **Firestore indexes:** Tạo composite indexes cho queries phức tạp
- **Connection pooling:** Already handled by Firebase SDK
- **Caching:** Implement Redis cho session/user data (optional)

---

## Backup & Recovery

### Firebase Backup

```bash
# Export Firestore data
gcloud firestore export gs://your-bucket-name

# Import Firestore data
gcloud firestore import gs://your-bucket-name/backup-folder
```

### Cloudinary Backup

- Cloudinary tự động backup
- Download files về local nếu cần:

```bash
# Sử dụng Cloudinary Admin API
```

---

## Security Best Practices

1. **Never commit credentials** to Git
2. **Use environment variables** in production
3. **Enable HTTPS** (SSL/TLS)
4. **Validate all user inputs**
5. **Sanitize file uploads** (check MIME type, size, extension)
6. **Implement rate limiting** (prevent abuse)
7. **Use prepared statements** (SQL injection prevention - if using SQL)
8. **Hash passwords** (BCrypt - already implemented)
9. **CORS configuration** (restrict origins)
10. **Keep dependencies updated** (mvn versions:display-dependency-updates)

---

## Useful Commands

```bash
# Maven
mvn clean package                    # Build WAR
mvn clean install -DskipTests       # Build + install to local repo
mvn dependency:tree                  # Show dependency tree
mvn cargo:run                        # Run with embedded Tomcat

# Docker
docker ps                            # List running containers
docker logs -f <container>           # Follow logs
docker exec -it <container> bash     # Enter container shell
docker-compose up -d                 # Start all services
docker-compose down                  # Stop all services
docker system prune -a               # Clean up Docker

# Worker
ps aux | grep WorkerConsumer         # Check if running
kill $(cat worker.pid)               # Stop worker
tail -f worker.log                   # Follow worker logs

# Nginx
sudo nginx -t                        # Test config
sudo systemctl restart nginx         # Restart Nginx
sudo tail -f /var/log/nginx/error.log  # Error logs
```

---

## Support & Resources

### Documentation
- [README.md](README.md) - Project overview
- [DEPLOYMENT.md](DEPLOYMENT.md) - Deployment guide
- [VNPAY_TROUBLESHOOTING.md](VNPAY_TROUBLESHOOTING.md) - VNPay issues

### External Resources
- [Jakarta EE](https://jakarta.ee/specifications/platform/9/)
- [Apache Tomcat 10](https://tomcat.apache.org/tomcat-10.1-doc/)
- [Firebase Admin SDK](https://firebase.google.com/docs/admin/setup)
- [Cloudinary Java SDK](https://cloudinary.com/documentation/java_integration)
- [RabbitMQ Java Client](https://www.rabbitmq.com/java-client.html)
- [VNPay API](https://sandbox.vnpayment.vn/apis/)

### Contact
- GitHub Issues: [FlexiFile Issues](https://github.com/yourusername/FlexiFile/issues)
- Email: support@flexifile.com

---

**Happy Coding! 🚀**
