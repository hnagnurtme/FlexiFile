# FlexiFile Setup Guide

This guide provides detailed instructions for setting up and running the FlexiFile application in development and production environments.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Development Setup](#development-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Docker Deployment](#docker-deployment)
- [Production Deployment](#production-deployment)
- [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 17**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
   - Verify installation:
     ```bash
     java -version
     # Should show version 17.x.x
     ```

2. **Apache Maven 3.6+**
   - Download from [Apache Maven](https://maven.apache.org/download.cgi)
   - Verify installation:
     ```bash
     mvn -version
     # Should show Maven 3.6.0 or higher
     ```

3. **Apache Tomcat 10.x** (Optional for local development)
   - Download from [Apache Tomcat](https://tomcat.apache.org/download-10.cgi)
   - The project includes an embedded Tomcat via Maven Cargo plugin

4. **Docker** (For containerized deployment)
   - Download from [Docker](https://www.docker.com/get-started)
   - Verify installation:
     ```bash
     docker --version
     docker-compose --version
     ```

### External Service Accounts

You'll need accounts and credentials for the following services:

1. **Firebase**
   - Create a project at [Firebase Console](https://console.firebase.google.com/)
   - Enable Firestore Database
   - Download service account key (JSON file)

2. **Cloudinary**
   - Sign up at [Cloudinary](https://cloudinary.com/)
   - Get API credentials (Cloud Name, API Key, API Secret)

3. **RabbitMQ (CloudAMQP)**
   - Sign up at [CloudAMQP](https://www.cloudamqp.com/)
   - Create an instance and get the AMQP URL

4. **VNPay** (For payment integration)
   - Register at [VNPay](https://vnpay.vn/)
   - Get merchant credentials (TMN Code, Hash Secret)
   - Note: The current configuration uses sandbox mode

5. **Gmail SMTP** (For email notifications)
   - Use a Gmail account
   - Enable 2-Factor Authentication
   - Generate an App Password

## Development Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/FlexiFile.git
cd FlexiFile
```

### 2. Project Structure

```
FlexiFile/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # Servlet controllers
│   │   │   ├── model/
│   │   │   │   ├── bean/        # Data models
│   │   │   │   ├── bo/          # Business objects
│   │   │   │   └── dao/         # Data access objects
│   │   │   ├── util/            # Utility classes
│   │   │   └── worker/          # RabbitMQ workers
│   │   ├── resources/
│   │   │   ├── serviceAccountKey.json  # Firebase credentials
│   │   │   ├── rabbitmq.properties     # RabbitMQ config
│   │   │   └── log4j.properties        # Logging config
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml      # Servlet mappings
│   │       ├── jsp/             # JSP views
│   │       ├── style/           # CSS files
│   │       └── js/              # JavaScript files
├── pom.xml                       # Maven configuration
├── Dockerfile                    # Docker image definition
└── render.sh                     # Multi-arch build script
```

## Configuration

### 1. Firebase Configuration

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Navigate to Project Settings > Service Accounts
3. Click "Generate New Private Key"
4. Save the JSON file as `serviceAccountKey.json`
5. Place it in `src/main/resources/`

**Important:** Never commit this file to version control. Add it to `.gitignore`:
```bash
echo "src/main/resources/serviceAccountKey.json" >> .gitignore
```

### 2. Cloudinary Configuration

Edit [src/main/java/util/CloudinaryConfig.java](src/main/java/util/CloudinaryConfig.java):

```java
public class CloudinaryConfig {
    public static final String CLOUD_NAME = "your-cloud-name";
    public static final String API_KEY    = "your-api-key";
    public static final String API_SECRET = "your-api-secret";

    public static final String MAIN_FOLDER = "FlexFile";
    public static final String SOURCE_FOLDER = MAIN_FOLDER + "/SourceFile";
    public static final String CONVERTED_FOLDER = MAIN_FOLDER + "/ConvertedFile";
    public static final String AVATARS_FOLDER = MAIN_FOLDER + "/Avatars";
}
```

**Security Note:** For production, use environment variables instead of hardcoding credentials.

### 3. RabbitMQ Configuration

Edit [src/main/java/config/RabbitMQConfig.java](src/main/java/config/RabbitMQConfig.java):

```java
public class RabbitMQConfig {
    public static final String CLOUDAMQP_URL = "amqps://username:password@host/vhost";
}
```

Or create `src/main/resources/rabbitmq.properties`:
```properties
rabbitmq.url=amqps://username:password@host/vhost
rabbitmq.queue.name=file_conversion_queue
```

### 4. Email Configuration

Edit [src/main/java/config/EmailConfig.java](src/main/java/config/EmailConfig.java):

```java
public class EmailConfig {
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final String SMTP_PORT = "587";
    public static final String FROM_EMAIL = "your-email@gmail.com";
    public static final String FROM_PASSWORD = "your-app-password";
    public static final String FROM_NAME = "FlexiFile Support";
}
```

**Gmail App Password:**
1. Enable 2-Factor Authentication on your Google Account
2. Visit [App Passwords](https://myaccount.google.com/apppasswords)
3. Generate a new app password for "Mail"
4. Use this 16-character password in the configuration

### 5. VNPay Configuration

Edit [src/main/java/util/VNPayUtil.java](src/main/java/util/VNPayUtil.java):

```java
private static final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
private static final String vnp_ReturnUrl = "http://your-domain.com/FlexiFile/payment/vnpay_return";
private static final String vnp_TmnCode = "YOUR_TMN_CODE";
private static final String vnp_HashSecret = "YOUR_HASH_SECRET";
```

For production, use:
```java
private static final String vnp_PayUrl = "https://vnpayment.vn/paymentv2/vpcpay.html";
```

### 6. Environment Variables (Recommended for Production)

Instead of hardcoding credentials, use environment variables:

```bash
# Set environment variables
export CLOUDINARY_CLOUD_NAME="your-cloud-name"
export CLOUDINARY_API_KEY="your-api-key"
export CLOUDINARY_API_SECRET="your-api-secret"
export RABBITMQ_URL="amqps://username:password@host/vhost"
export EMAIL_USERNAME="your-email@gmail.com"
export EMAIL_PASSWORD="your-app-password"
export VNPAY_TMN_CODE="YOUR_TMN_CODE"
export VNPAY_HASH_SECRET="YOUR_HASH_SECRET"
```

Modify your configuration classes to read from environment variables:

```java
public class CloudinaryConfig {
    public static final String CLOUD_NAME = System.getenv("CLOUDINARY_CLOUD_NAME");
    public static final String API_KEY    = System.getenv("CLOUDINARY_API_KEY");
    public static final String API_SECRET = System.getenv("CLOUDINARY_API_SECRET");
}
```

## Running the Application

### Method 1: Using Maven Cargo (Embedded Tomcat)

This is the easiest way to run the application locally:

```bash
# Clean and build the project
mvn clean package

# Run with embedded Tomcat
mvn cargo:run
```

The application will be available at: `http://localhost:8080/FlexiFile`

To stop the server, press `Ctrl+C`.

### Method 2: Using Standalone Tomcat

1. Build the WAR file:
   ```bash
   mvn clean package
   ```

2. Copy the WAR file to Tomcat:
   ```bash
   cp target/FlexiFile.war /path/to/tomcat/webapps/
   ```

3. Start Tomcat:
   ```bash
   /path/to/tomcat/bin/startup.sh  # Linux/Mac
   /path/to/tomcat/bin/startup.bat  # Windows
   ```

4. Access the application at: `http://localhost:8080/FlexiFile`

### Method 3: Using Docker

1. Build the Docker image:
   ```bash
   docker build -t flexifile:latest .
   ```

2. Run the container:
   ```bash
   docker run -d \
     -p 8080:8080 \
     --name flexifile \
     -e CLOUDINARY_CLOUD_NAME=your-cloud-name \
     -e CLOUDINARY_API_KEY=your-api-key \
     -e CLOUDINARY_API_SECRET=your-api-secret \
     flexifile:latest
   ```

3. Access the application at: `http://localhost:8080/FlexiFile`

### Starting the Worker Process

The RabbitMQ worker processes file conversions asynchronously. To start a worker:

1. Build the project:
   ```bash
   mvn clean package
   ```

2. Run the worker:
   ```bash
   java -cp target/FlexiFile/WEB-INF/classes:target/FlexiFile/WEB-INF/lib/* \
     worker.WorkerLauncher
   ```

Alternatively, the worker can be started as a separate Java application or deployed as a separate service.

## Docker Deployment

### Basic Docker Setup

The project includes a multi-stage Dockerfile that:
1. Builds the application with Maven
2. Creates a production image with Tomcat 10

Build and run:

```bash
# Build
docker build -t flexifile:latest .

# Run
docker run -d -p 8080:8080 --name flexifile flexifile:latest

# View logs
docker logs -f flexifile

# Stop
docker stop flexifile
docker rm flexifile
```

### Docker Compose (with worker)

Create a `docker-compose.yml`:

```yaml
version: '3.8'

services:
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

  worker:
    build: .
    command: ["java", "-cp", "/usr/local/tomcat/webapps/FlexiFile/WEB-INF/classes:/usr/local/tomcat/webapps/FlexiFile/WEB-INF/lib/*", "worker.WorkerLauncher"]
    environment:
      - CLOUDINARY_CLOUD_NAME=${CLOUDINARY_CLOUD_NAME}
      - CLOUDINARY_API_KEY=${CLOUDINARY_API_KEY}
      - CLOUDINARY_API_SECRET=${CLOUDINARY_API_SECRET}
      - RABBITMQ_URL=${RABBITMQ_URL}
    restart: unless-stopped
```

Run with:

```bash
docker-compose up -d
```

### Multi-Architecture Build

The included `render.sh` script builds for both AMD64 and ARM64:

```bash
# Build and push multi-arch image
chmod +x render.sh
./render.sh v1.0.0

# Or use default 'latest' tag
./render.sh
```

## Production Deployment

### Security Checklist

Before deploying to production:

- [ ] Remove all hardcoded credentials
- [ ] Use environment variables for all secrets
- [ ] Add `serviceAccountKey.json` to `.gitignore`
- [ ] Enable HTTPS/SSL
- [ ] Configure firewall rules
- [ ] Set up database backups
- [ ] Configure log rotation
- [ ] Use production VNPay credentials
- [ ] Use production Firebase project
- [ ] Implement rate limiting
- [ ] Set up monitoring and alerts

### Environment Configuration

Create a `.env` file (DO NOT commit to git):

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

# VNPay
VNPAY_TMN_CODE=YOUR_PRODUCTION_TMN_CODE
VNPAY_HASH_SECRET=YOUR_PRODUCTION_HASH_SECRET
VNPAY_RETURN_URL=https://yourdomain.com/FlexiFile/payment/vnpay_return
```

### Deployment Platforms

#### 1. Traditional Server (VPS/Dedicated)

```bash
# Install Java and Docker
sudo apt update
sudo apt install openjdk-17-jdk docker.io docker-compose

# Clone and deploy
git clone https://github.com/yourusername/FlexiFile.git
cd FlexiFile

# Build and run
docker-compose up -d

# Set up nginx reverse proxy
sudo apt install nginx
# Configure nginx to proxy to localhost:8080
```

#### 2. Cloud Platforms

**Render.com:**
```bash
# Push Docker image
./render.sh latest

# Create Web Service on Render
# - Docker Image: your-dockerhub-username/flexifile:latest
# - Port: 8080
# - Add environment variables
```

**AWS EC2:**
```bash
# SSH into EC2 instance
# Install Docker
# Pull and run image
docker pull your-dockerhub-username/flexifile:latest
docker run -d -p 80:8080 --env-file .env flexifile:latest
```

**Google Cloud Run:**
```bash
# Build and push to Google Container Registry
gcloud builds submit --tag gcr.io/PROJECT-ID/flexifile
gcloud run deploy flexifile --image gcr.io/PROJECT-ID/flexifile --platform managed
```

### Nginx Reverse Proxy Configuration

Create `/etc/nginx/sites-available/flexifile`:

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable and restart:
```bash
sudo ln -s /etc/nginx/sites-available/flexifile /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### SSL/TLS Configuration

Use Let's Encrypt for free SSL certificates:

```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d yourdomain.com
```

## Troubleshooting

### Common Issues

#### 1. Port Already in Use

```bash
# Check what's using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

#### 2. Maven Build Fails

```bash
# Clear Maven cache and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

#### 3. Firebase Connection Error

- Verify `serviceAccountKey.json` is in `src/main/resources/`
- Check Firebase project permissions
- Ensure Firestore is enabled in Firebase Console

#### 4. RabbitMQ Connection Issues

- Verify the AMQP URL format: `amqps://username:password@host/vhost`
- Check if CloudAMQP instance is running
- Verify firewall allows outbound connections to RabbitMQ

#### 5. File Conversion Not Working

- Ensure RabbitMQ worker is running
- Check worker logs for errors
- Verify Cloudinary credentials
- Check that required libraries (POI, PDFBox) are present

#### 6. VNPay Payment Fails

- Verify TMN Code and Hash Secret
- Check return URL is correct
- Ensure using sandbox mode for testing
- Verify amount is in correct format (VND * 100)

#### 7. Email Not Sending

- Verify Gmail App Password is correct
- Check if 2FA is enabled on Gmail account
- Verify SMTP settings (host: smtp.gmail.com, port: 587)
- Check firewall allows outbound port 587

### Logging

Check application logs:

```bash
# Docker logs
docker logs -f flexifile

# Tomcat logs
tail -f /path/to/tomcat/logs/catalina.out

# Application logs (if configured)
tail -f logs/application.log
```

### Database (Firebase Firestore)

Access Firestore Console to:
- View/edit user data
- Check file job records
- Monitor payment history
- Debug authentication issues

### Testing Endpoints

```bash
# Test health
curl http://localhost:8080/FlexiFile/

# Test login (example)
curl -X POST http://localhost:8080/FlexiFile/login \
  -d "username=test@example.com&password=password123"
```

## Support

For additional help:
- Check the [README.md](README.md) for project overview
- Review the [GitHub Issues](https://github.com/yourusername/FlexiFile/issues)
- Contact the development team

## Additional Resources

- [Jakarta EE Documentation](https://jakarta.ee/specifications/platform/9/)
- [Apache Tomcat 10 Documentation](https://tomcat.apache.org/tomcat-10.1-doc/)
- [Firebase Admin SDK](https://firebase.google.com/docs/admin/setup)
- [Cloudinary Documentation](https://cloudinary.com/documentation)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
- [VNPay Integration Guide](https://sandbox.vnpayment.vn/apis/)
