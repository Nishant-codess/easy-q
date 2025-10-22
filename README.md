# Easy-Q: Digital Queue & Appointment Manager

A production-ready Spring Boot 3.x application for managing digital queues and appointments with real-time updates, analytics, and a modern web interface.

## 🚀 Features

### Core Modules
- **Admin Module**: Complete implementation with analytics dashboard, user management, and real-time monitoring
- **Booking Module**: Appointment scheduling with availability checking
- **Queue Module**: Real-time queue management with WebSocket updates
- **Notification Module**: Scheduled notifications with SMS/Email support (stubbed)

### Key Features
- 📊 **Real-time Analytics Dashboard** with Chart.js visualizations
- 🔄 **Live WebSocket Updates** for queue status changes
- 📱 **Responsive Design** with Bootstrap 5 and animations
- 🔐 **Spring Security** integration
- 📧 **Notification System** with scheduled jobs
- 🗄️ **MySQL Database** with proper relationships
- 🚀 **RESTful APIs** for all operations

## 🏗️ Architecture

### Project Structure
```
easy-q/
├── src/main/java/com/easyq/
│   ├── common/           # Shared models and configuration
│   ├── admin/            # Complete admin implementation
│   ├── booking/          # Appointment booking system
│   ├── queue/            # Queue management system
│   └── notification/     # Notification system
├── src/main/resources/
│   ├── templates/        # Thymeleaf templates
│   ├── static/          # CSS, JS, and static assets
│   └── application.properties
├── database/
│   └── schema.sql       # Database schema
└── README.md
```

### Technology Stack
- **Backend**: Spring Boot 3.2.0, Java 17+
- **Database**: H2 (development), PostgreSQL (production)
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js
- **Real-time**: WebSocket with STOMP
- **Security**: Spring Security
- **Build**: Maven
- **Email**: Spring Mail with SMTP support

## 🛠️ Setup & Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

### Database Setup
1. Create MySQL database:
```sql
CREATE DATABASE easyq_db;
CREATE USER 'easyq_user'@'localhost' IDENTIFIED BY 'easyq_password';
GRANT ALL PRIVILEGES ON easyq_db.* TO 'easyq_user'@'localhost';
FLUSH PRIVILEGES;
```

2. Import schema and seed data:
```bash
mysql -u easyq_user -p easyq_db < database/schema.sql
```

### Application Setup
1. Clone the repository:
```bash
git clone <repository-url>
cd easy-q
```

2. Update database configuration in `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Build and run:
```bash
cd backend
mvn clean package
mvn spring-boot:run
```

4. Access the application:
- Main application: http://localhost:8080
- Admin dashboard: http://localhost:8080/admin
- Booking: http://localhost:8080/booking
- Queue: http://localhost:8080/queue

## 📊 Admin Dashboard

The admin dashboard provides comprehensive analytics and management capabilities:

### Features
- **Real-time Statistics**: Users, appointments, queue entries, services
- **Interactive Charts**: Service distribution, daily trends, user registrations
- **Live Updates**: WebSocket-powered real-time data refresh
- **Management Tools**: User management, service configuration
- **Queue Monitoring**: Real-time queue status and management

### Default Admin Credentials
- Username: `admin`
- Password: `admin123`

## 🔄 API Endpoints

### Admin APIs
```bash
# Dashboard statistics
GET /admin/api/stats

# User management
GET /admin/users
POST /admin/api/users
PUT /admin/api/users/{id}
DELETE /admin/api/users/{id}

# Service management
GET /admin/services
POST /admin/api/services
PUT /admin/api/services/{id}
DELETE /admin/api/services/{id}
```

### Booking APIs
```bash
# Book appointment
POST /booking/book
Content-Type: application/json
{
  "serviceId": 1,
  "appointmentDate": "2024-01-15",
  "appointmentTime": "09:00",
  "notes": "Optional notes"
}

# Get user appointments
GET /booking/api/appointments

# Get available time slots
GET /booking/api/available-slots?date=2024-01-15&serviceId=1
```

### Queue APIs
```bash
# Join queue
POST /queue/join
Content-Type: application/x-www-form-urlencoded
serviceId=1

# Get queue entries
GET /queue/api/entries?serviceId=1

# Call next in queue
POST /queue/call-next
Content-Type: application/x-www-form-urlencoded
serviceId=1
```

### WebSocket Endpoints
- **Queue Updates**: `/topic/queue`
- **Notifications**: `/topic/notifications`
- **Connection**: `/ws`

## 🌿 Branch Strategy

The project follows a feature branch strategy:

- `main`: Production-ready code
- `dev`: Development integration branch
- `feature/booking`: Booking module development
- `feature/queue`: Queue module development
- `feature/notification`: Notification module development
- `feature/admin`: Admin module development

### Branch Commands
```bash
# Create and switch to feature branch
git checkout -b feature/your-feature

# Push feature branch
git push origin feature/your-feature

# Merge to dev
git checkout dev
git merge feature/your-feature
git push origin dev

# Merge to main (after testing)
git checkout main
git merge dev
git push origin main
```

## 🧪 Testing

### Manual Testing Steps
1. **Database Import**: Verify schema.sql imports successfully
2. **Application Startup**: Check `mvn spring-boot:run` starts without errors
3. **Admin Dashboard**: Access http://localhost:8080/admin and verify charts load
4. **Booking Flow**: Test appointment booking end-to-end
5. **Queue Management**: Test joining queue and real-time updates
6. **WebSocket**: Verify queue updates appear in real-time

### Automated Testing
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## 📝 Module Responsibilities

### Admin Module (Complete)
- ✅ Dashboard with analytics and charts
- ✅ User management (CRUD operations)
- ✅ Service management (CRUD operations)
- ✅ Real-time statistics and monitoring
- ✅ Appointment and queue oversight

### Booking Module (Starter)
- ✅ Basic appointment booking
- ✅ Service selection and time slot management
- ✅ User appointment history
- 🔄 **TODO**: Advanced scheduling rules
- 🔄 **TODO**: Recurring appointments
- 🔄 **TODO**: Appointment reminders

### Queue Module (Starter)
- ✅ Real-time queue management
- ✅ WebSocket integration for live updates
- ✅ Queue status tracking
- 🔄 **TODO**: Priority queue support
- 🔄 **TODO**: Queue analytics
- 🔄 **TODO**: Multi-service queue management

### Notification Module (Starter)
- ✅ Scheduled notification jobs
- ✅ Notification logging and tracking
- 🔄 **TODO**: Twilio SMS integration
- 🔄 **TODO**: SMTP email integration
- 🔄 **TODO**: Push notifications
- 🔄 **TODO**: Notification preferences

## 🔧 Configuration

### Environment Variables
```bash
# Database
DB_USERNAME=easyq_user
DB_PASSWORD=easyq_password

# Notifications (Optional)
TWILIO_ACCOUNT_SID=your_twilio_sid
TWILIO_AUTH_TOKEN=your_twilio_token
TWILIO_PHONE_NUMBER=+1234567890

SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
```

### Application Properties
Key configuration options in `application.properties`:
- Database connection settings
- WebSocket configuration
- Security settings
- Notification service placeholders
- Logging levels

## 🚀 Deployment

### Local Development
```bash
mvn spring-boot:run
```

### Production Build
```bash
mvn clean package -DskipTests
java -jar target/easy-q-1.0.0.jar
```

### Docker (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/easy-q-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the GitHub repository
- Check the documentation in each module
- Review the TODO comments in the code for implementation guidance

## 🎯 Roadmap

### Phase 1 (Current)
- ✅ Core application structure
- ✅ Admin dashboard with analytics
- ✅ Basic booking and queue functionality
- ✅ WebSocket real-time updates

### Phase 2 (Next)
- 🔄 Complete notification system with external providers
- 🔄 Advanced queue management features
- 🔄 Mobile-responsive improvements
- 🔄 API documentation with Swagger

### Phase 3 (Future)
- 🔄 Multi-tenant support
- 🔄 Advanced analytics and reporting
- 🔄 Integration with external calendar systems
- 🔄 Mobile app development

---

**Built with ❤️ using Spring Boot 3.x and modern web technologies**
#   e a s y - q  
 