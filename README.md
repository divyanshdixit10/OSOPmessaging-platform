# OSOP Messaging Platform - Production-Ready SaaS

A comprehensive, multi-tenant messaging platform built with Spring Boot and React, designed for production deployment and monetization.

## 🚀 Features

### Core Messaging
- **Multi-Channel Support**: Email, SMS, WhatsApp Business API
- **Rich Email Editor**: WYSIWYG editor with HTML templates
- **Bulk Messaging**: CSV/XLSX import with recipient management
- **Template System**: Create, manage, and reuse email templates
- **Scheduling**: Send campaigns at specific times
- **Retry Logic**: Automatic retry for failed sends

### Analytics & Tracking
- **Real-time Analytics**: Open rates, click rates, bounce rates
- **Campaign Performance**: Detailed metrics and insights
- **Subscriber Management**: List management and segmentation
- **Unsubscribe Handling**: Automatic unsubscribe processing
- **Export Reports**: CSV and PDF report generation

### Multi-Tenant SaaS
- **Tenant Isolation**: Complete data separation
- **Subscription Plans**: Free, Starter, Professional, Enterprise
- **Usage Quotas**: Per-plan limits and monitoring
- **Billing Integration**: Stripe payment processing
- **User Management**: Role-based access control

### Production Features
- **Scalable Architecture**: Microservices-ready design
- **Database Migrations**: Flyway for schema management
- **Caching**: Redis for performance optimization
- **Message Queues**: RabbitMQ for async processing
- **Monitoring**: Prometheus metrics and health checks
- **Security**: JWT authentication, rate limiting, input validation

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (React)       │◄──►│   (Spring Boot) │◄──►│   (MySQL)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Nginx         │    │   Redis         │    │   RabbitMQ      │
│   (Load Balancer)│    │   (Cache)       │    │   (Message Queue)│
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Technology Stack

### Backend
- **Java 17** with Spring Boot 3.5.3
- **Spring Security** for authentication and authorization
- **Spring Data JPA** for database operations
- **MySQL 8.0** for data persistence
- **Redis** for caching and rate limiting
- **RabbitMQ** for async message processing
- **Flyway** for database migrations
- **Stripe** for payment processing
- **Twilio** for SMS services
- **WhatsApp Business API** for messaging

### Frontend
- **React 18** with TypeScript
- **Chakra UI** for modern UI components
- **React Query** for data fetching and caching
- **Zustand** for state management
- **Recharts** for analytics visualization
- **React Quill** for rich text editing
- **Axios** for API communication

### Infrastructure
- **Docker** for containerization
- **Kubernetes** for orchestration
- **Nginx** for reverse proxy and load balancing
- **Prometheus** for monitoring
- **GitHub Actions** for CI/CD

## 📦 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker and Docker Compose
- MySQL 8.0
- Redis
- RabbitMQ

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/osop-messaging-platform.git
   cd osop-messaging-platform
   ```

2. **Start infrastructure services**
   ```bash
   docker-compose up -d mysql redis rabbitmq
   ```

3. **Backend setup**
   ```bash
   cd Backend
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

4. **Frontend setup**
   ```bash
   cd frontend
   npm install
   npm start
   ```

5. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

### Production Deployment

#### Using Docker Compose
```bash
docker-compose -f docker-compose.prod.yml up -d
```

#### Using Kubernetes
```bash
# Apply all Kubernetes manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n osop-messaging
```

## 🔧 Configuration

### Environment Variables

#### Backend Configuration
```bash
# Database
DATABASE_URL=jdbc:mysql://localhost:3306/messaging_platform
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin123

# Stripe
STRIPE_SECRET_KEY=sk_test_...
STRIPE_PUBLISHABLE_KEY=pk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# Twilio
TWILIO_ACCOUNT_SID=AC...
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_PHONE_NUMBER=+1234567890

# WhatsApp
WHATSAPP_ACCESS_TOKEN=your_access_token
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id
WHATSAPP_BUSINESS_ACCOUNT_ID=your_business_account_id

# AWS S3
AWS_ACCESS_KEY=your_access_key
AWS_SECRET_KEY=your_secret_key
AWS_S3_BUCKET=your_bucket_name
```

#### Frontend Configuration
```bash
REACT_APP_API_URL=https://api.yourdomain.com
REACT_APP_STRIPE_PUBLISHABLE_KEY=pk_live_...
```

## 📊 Subscription Plans

| Feature | Free | Starter | Professional | Enterprise |
|---------|------|---------|--------------|------------|
| Users | 5 | 10 | 25 | 100 |
| Campaigns/Month | 10 | 50 | 200 | 1,000 |
| Emails/Month | 1,000 | 10,000 | 50,000 | 200,000 |
| SMS/Month | 50 | 500 | 2,000 | 10,000 |
| WhatsApp/Month | 25 | 100 | 500 | 2,000 |
| Storage | 100 MB | 1 GB | 5 GB | 50 GB |
| Support | Community | Email | Priority | 24/7 |
| Price | Free | $29/month | $99/month | $299/month |

## 🔐 Security Features

- **JWT Authentication** with refresh tokens
- **Role-based Access Control** (Admin, User)
- **Rate Limiting** to prevent abuse
- **Input Validation** and sanitization
- **CORS Configuration** for cross-origin requests
- **SSL/TLS Encryption** for data in transit
- **Password Hashing** with BCrypt
- **SQL Injection Protection** with JPA
- **XSS Protection** with content security policies

## 📈 Monitoring & Observability

### Health Checks
- Application health: `/actuator/health`
- Database connectivity: `/actuator/health/db`
- Redis connectivity: `/actuator/health/redis`
- RabbitMQ connectivity: `/actuator/health/rabbitmq`

### Metrics
- Prometheus metrics: `/actuator/prometheus`
- Application metrics: `/actuator/metrics`
- Custom business metrics

### Logging
- Structured JSON logging
- Log aggregation with ELK stack
- Error tracking and alerting

## 🚀 CI/CD Pipeline

The project includes a comprehensive GitHub Actions workflow:

1. **Code Quality**: Linting, testing, security scanning
2. **Build**: Docker image creation and registry push
3. **Deploy**: Automated deployment to staging/production
4. **Monitoring**: Health checks and rollback on failure

## 📚 API Documentation

### Authentication
All API endpoints require JWT authentication except:
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`

### Key Endpoints

#### Campaigns
- `GET /api/campaigns` - List campaigns
- `POST /api/campaigns` - Create campaign
- `PUT /api/campaigns/{id}` - Update campaign
- `DELETE /api/campaigns/{id}` - Delete campaign
- `POST /api/campaigns/{id}/send` - Send campaign

#### Templates
- `GET /api/templates` - List templates
- `POST /api/templates` - Create template
- `PUT /api/templates/{id}` - Update template
- `DELETE /api/templates/{id}` - Delete template

#### Analytics
- `GET /api/analytics/campaigns/{id}` - Campaign analytics
- `GET /api/analytics/overview` - Overview statistics
- `GET /api/analytics/export` - Export analytics data

#### Billing
- `GET /api/billing/plans` - Available plans
- `POST /api/billing/checkout` - Create checkout session
- `POST /api/billing/portal` - Customer portal
- `GET /api/billing/history` - Billing history

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: [Wiki](https://github.com/yourusername/osop-messaging-platform/wiki)
- **Issues**: [GitHub Issues](https://github.com/yourusername/osop-messaging-platform/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/osop-messaging-platform/discussions)
- **Email**: support@yourdomain.com

## 🎯 Roadmap

### Q1 2024
- [ ] Advanced segmentation
- [ ] A/B testing for campaigns
- [ ] Webhook integrations
- [ ] Mobile app

### Q2 2024
- [ ] AI-powered content suggestions
- [ ] Advanced analytics dashboard
- [ ] Multi-language support
- [ ] White-label solution

### Q3 2024
- [ ] Advanced automation workflows
- [ ] Third-party integrations
- [ ] Advanced reporting
- [ ] Enterprise SSO

---

**Built with ❤️ by the OSOP Team**