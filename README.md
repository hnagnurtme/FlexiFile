<p align="center" style="display: flex; justify-content: center; align-items: center; flex-wrap: wrap; gap: 15px;">
  <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9FZGcOId8e2tvMiw57wbiDRBO0luIyp2atw&s" height="55" style="border-radius:5px;" title="RabbitMQ" />
  <img src="https://congdonglaptrinhjava.wordpress.com/wp-content/uploads/2014/11/servlet-jsp-tutorial.png" height="70" title="JSP" />
  <img src="https://appexchange.salesforce.com/image_host/300c831a-4271-44f2-91da-b48269175229.png" height="55" style="border-radius:10px;" title="Cloudinary" />
  <img src="https://vnpay.vn/s1/statics.vnpay.vn/2023/9/06ncktiwd6dc1694418196384.png" height="70" style="border-radius:5px;" title="VNPay" />
   <img src="https://firebase.google.com/static/images/brand-guidelines/logo-vertical.png" height="80" style="border-radius:5px;" title="Firebase" />
</p>

# FlexiFile - File Conversion and Management System

> FlexiFile is a web-based application that facilitates file uploads, conversions, and management. Built with Jakarta EE and deployed as a WAR file on Apache Tomcat, it provides user authentication, asynchronous file processing with RabbitMQ, cloud storage via Cloudinary, and payment integration with VNPay.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Deployment](#deployment)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)

## Features

- **User Authentication & Authorization:** Secure registration, login, password recovery with Firebase Authentication
- **File Upload & Conversion:** Support for multiple file formats (PDF, DOCX, DOC, images, CSV, HTML)
- **Asynchronous Processing:** Message queue-based file conversion using RabbitMQ workers
- **Cloud Storage:** Cloudinary integration for source and converted file management
- **Payment System:** VNPay integration for premium account upgrades
- **User Dashboard:** View conversion history, manage profile, and track transactions
- **Email Notifications:** Automated email alerts for authentication and conversion status
- **Responsive UI:** JSP-based frontend with modern CSS styling

## Technologies Used

### Backend
- **Java 17** - Core programming language
- **Jakarta EE (Servlet 5.0)** - Web application framework
- **Apache Tomcat 10.x** - Application server
- **Maven** - Build and dependency management

### Libraries & Dependencies
- **Apache POI** - Microsoft Office document processing (DOCX, DOC)
- **Apache PDFBox** - PDF document handling
- **OpenCSV** - CSV file processing
- **jBCrypt** - Password hashing
- **RabbitMQ Client** - Message queue integration
- **Jackson** - JSON serialization/deserialization
- **Log4j2** - Logging framework

### External Services
- **Firebase** - User authentication and database
- **Cloudinary** - Cloud-based file storage
- **VNPay** - Payment gateway
- **RabbitMQ (CloudAMQP)** - Message broker for async processing
- **Gmail SMTP** - Email notifications

### Frontend
- **JSP (JavaServer Pages)** - Server-side rendering
- **CSS3** - Styling
- **JavaScript** - Client-side interactivity

## Architecture

FlexiFile follows a layered architecture pattern:

```
┌─────────────────────────────────────────────────┐
│              Client (Browser)                    │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         Controllers (Servlets)                   │
│  - FileUploadController                          │
│  - PaymentServlet                                │
│  - UserController, etc.                          │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         Business Logic (BO Layer)                │
│  - AuthBO, FileJobBO, PaymentBO                 │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         Data Access (DAO Layer)                  │
│  - Firebase Firestore Integration                │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│    External Services & Workers                   │
│  - RabbitMQ Workers (async conversion)           │
│  - Cloudinary (file storage)                     │
│  - VNPay (payments)                              │
└─────────────────────────────────────────────────┘
```

### Key Components

1. **Controllers:** Handle HTTP requests and responses
2. **Business Objects (BO):** Implement business logic
3. **Data Access Objects (DAO):** Interact with Firebase Firestore
4. **Workers:** Process file conversions asynchronously via RabbitMQ
5. **Utilities:** Provide helper functions for conversion, email, etc.

## Getting Started

For detailed setup and installation instructions, see [SETUP.md](SETUP.md).

### Quick Start

```bash
# Clone the repository
git clone https://github.com/yourusername/FlexiFile.git
cd FlexiFile

# Build the project
mvn clean package

# Run with embedded Tomcat
mvn cargo:run
```

Access the application at `http://localhost:8080/FlexiFile`

## Deployment

### Docker Deployment

Build and run using Docker:

```bash
# Build the Docker image
docker build -t flexifile:latest .

# Run the container
docker run -p 8080:8080 flexifile:latest
```

### Multi-Architecture Build

Use the provided script to build and push multi-architecture images:

```bash
# Build and push for linux/amd64 and linux/arm64
./render.sh latest
```

### Production Deployment

For production deployment, ensure you configure:
- Environment-specific Firebase credentials
- Production Cloudinary credentials
- Production VNPay credentials
- Secure RabbitMQ connection
- Production email configuration

See [SETUP.md](SETUP.md) for detailed configuration instructions.

## API Documentation

### Main Endpoints

#### Authentication
- `POST /login` - User login
- `POST /register` - New user registration
- `POST /forgot-password` - Password recovery
- `POST /verify-otp` - OTP verification
- `POST /reset-password` - Reset password

#### File Operations
- `POST /upload` - Upload file for conversion
- `GET /result` - View conversion results
- `GET /download` - Download converted file

#### User Management
- `GET /profile` - View user profile
- `POST /profile/update` - Update profile
- `GET /account` - View account details
- `GET /transaction-history` - View payment history

#### Payment
- `GET /upgrade` - View upgrade options
- `POST /payment/create` - Create payment
- `GET /payment/vnpay_return` - VNPay payment callback

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m 'Add YourFeature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
- Create an issue in the GitHub repository
- Contact the development team

---

Made with Java and Jakarta EE
