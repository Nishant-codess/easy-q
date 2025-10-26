# Easy-Q Production Setup Guide

## Environment Variables

Set the following environment variables for production deployment:

### Database Configuration
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/easyq
DATABASE_DRIVER=org.postgresql.Driver
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password
H2_CONSOLE_ENABLED=false
JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
JPA_DDL_AUTO=validate
JPA_SHOW_SQL=false
JPA_FORMAT_SQL=false
```

### Security Configuration
```bash
ADMIN_USERNAME=your_admin_username
ADMIN_PASSWORD=your_secure_password
```

### Email Configuration
```bash
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_AUTH=true
SMTP_STARTTLS=true

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
MAIL_SMTP_SSL_TRUST=smtp.gmail.com
```

### Notification Settings
```bash
NOTIF_EMAIL_ENABLED=true
NOTIF_SMS_ENABLED=false
NOTIF_RETRY_MAX=3
NOTIF_RETRY_DELAY=5
NOTIF_APPOINTMENT_REMINDER_HOURS=24
```

### Logging Configuration
```bash
LOG_LEVEL_EASYQ=INFO
LOG_LEVEL_WEB=INFO
LOG_LEVEL_SECURITY=INFO
```

## Production Deployment

1. **Database Setup**: Configure a production database (PostgreSQL recommended)
2. **Environment Variables**: Set all required environment variables
3. **Build**: Run `mvn clean package`
4. **Deploy**: Deploy the generated JAR file to your server
5. **Security**: Ensure all credentials are properly secured

## Features

- **Queue Management**: Digital queue system with real-time updates
- **Appointment Booking**: Schedule and manage appointments
- **Notifications**: Email notifications for queue updates and appointments
- **Admin Dashboard**: Comprehensive analytics and management
- **User Management**: Role-based access control

## API Endpoints

- `/` - Home page
- `/queue` - Queue management
- `/booking` - Appointment booking
- `/admin` - Admin dashboard
- `/notifications` - Notification management
- `/login` - User authentication

## Security Notes

- Change default admin credentials
- Use HTTPS in production
- Configure proper database security
- Set up proper email authentication
- Monitor application logs

