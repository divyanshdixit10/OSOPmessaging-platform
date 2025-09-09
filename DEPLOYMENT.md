# Deployment Guide - OSOP Messaging Platform

This guide covers deploying the OSOP Messaging Platform to production environments.

## 🏗️ Architecture Overview

The platform consists of:
- **Frontend**: React application served by Nginx
- **Backend**: Spring Boot application
- **Database**: MySQL 8.0
- **Cache**: Redis
- **Message Queue**: RabbitMQ
- **Load Balancer**: Nginx
- **Monitoring**: Prometheus + Grafana

## 🐳 Docker Deployment

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- 4GB+ RAM
- 20GB+ disk space

### Quick Start
```bash
# Clone repository
git clone https://github.com/yourusername/osop-messaging-platform.git
cd osop-messaging-platform

# Set environment variables
cp .env.example .env
# Edit .env with your configuration

# Start all services
docker-compose -f docker-compose.prod.yml up -d

# Check status
docker-compose ps
```

### Environment Configuration
Create a `.env` file with the following variables:

```bash
# Database
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_DATABASE=messaging_platform
MYSQL_USER=messaging_user
MYSQL_PASSWORD=your_secure_password

# Redis
REDIS_PASSWORD=your_redis_password

# RabbitMQ
RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=admin123

# Application
JWT_SECRET=your_jwt_secret_key_at_least_32_characters
STRIPE_SECRET_KEY=sk_live_...
STRIPE_PUBLISHABLE_KEY=pk_live_...
STRIPE_WEBHOOK_SECRET=whsec_...

# SMTP
SMTP_USERNAME=your_smtp_username
SMTP_PASSWORD=your_smtp_password

# AWS S3
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
AWS_S3_BUCKET=your_bucket_name

# Domain
DOMAIN=yourdomain.com
```

## ☸️ Kubernetes Deployment

### Prerequisites
- Kubernetes 1.20+
- kubectl configured
- Helm 3.0+ (optional)
- Ingress controller (nginx-ingress)
- cert-manager for SSL certificates

### Step 1: Create Namespace
```bash
kubectl apply -f k8s/namespace.yaml
```

### Step 2: Configure Secrets
```bash
# Update secrets.yaml with your actual values
kubectl apply -f k8s/secrets.yaml
```

### Step 3: Deploy Infrastructure
```bash
# Deploy MySQL
kubectl apply -f k8s/mysql.yaml

# Deploy Redis
kubectl apply -f k8s/redis.yaml

# Deploy RabbitMQ
kubectl apply -f k8s/rabbitmq.yaml

# Wait for infrastructure to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n osop-messaging --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n osop-messaging --timeout=300s
kubectl wait --for=condition=ready pod -l app=rabbitmq -n osop-messaging --timeout=300s
```

### Step 4: Deploy Application
```bash
# Deploy backend
kubectl apply -f k8s/backend.yaml

# Deploy frontend
kubectl apply -f k8s/frontend.yaml

# Deploy ingress
kubectl apply -f k8s/ingress.yaml
```

### Step 5: Verify Deployment
```bash
# Check all pods
kubectl get pods -n osop-messaging

# Check services
kubectl get services -n osop-messaging

# Check ingress
kubectl get ingress -n osop-messaging
```

## 🔧 Production Configuration

### SSL/TLS Setup
```bash
# Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Create ClusterIssuer for Let's Encrypt
kubectl apply -f - <<EOF
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: your-email@yourdomain.com
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
    - http01:
        ingress:
          class: nginx
EOF
```

### Database Backup
```bash
# Create backup job
kubectl apply -f - <<EOF
apiVersion: batch/v1
kind: CronJob
metadata:
  name: mysql-backup
  namespace: osop-messaging
spec:
  schedule: "0 2 * * *"  # Daily at 2 AM
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: mysql-backup
            image: mysql:8.0
            command:
            - /bin/bash
            - -c
            - |
              mysqldump -h mysql-service -u root -p\$MYSQL_ROOT_PASSWORD messaging_platform > /backup/backup-\$(date +%Y%m%d-%H%M%S).sql
            env:
            - name: MYSQL_ROOT_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: messaging-platform-secrets
                  key: DB_PASSWORD
            volumeMounts:
            - name: backup-storage
              mountPath: /backup
          volumes:
          - name: backup-storage
            persistentVolumeClaim:
              claimName: backup-pvc
          restartPolicy: OnFailure
EOF
```

### Monitoring Setup
```bash
# Install Prometheus and Grafana
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Install Prometheus
helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace \
  --set grafana.adminPassword=admin123

# Install Grafana dashboards
kubectl apply -f monitoring/grafana-dashboards.yaml
```

## 🔍 Health Checks

### Application Health
```bash
# Check backend health
curl https://api.yourdomain.com/actuator/health

# Check frontend
curl https://yourdomain.com

# Check database connectivity
curl https://api.yourdomain.com/actuator/health/db

# Check Redis connectivity
curl https://api.yourdomain.com/actuator/health/redis

# Check RabbitMQ connectivity
curl https://api.yourdomain.com/actuator/health/rabbitmq
```

### Kubernetes Health
```bash
# Check pod status
kubectl get pods -n osop-messaging

# Check service endpoints
kubectl get endpoints -n osop-messaging

# Check ingress status
kubectl describe ingress messaging-platform-ingress -n osop-messaging

# Check logs
kubectl logs -f deployment/messaging-platform-backend -n osop-messaging
```

## 📊 Monitoring & Alerting

### Prometheus Metrics
The application exposes metrics at `/actuator/prometheus`:
- JVM metrics
- HTTP request metrics
- Database connection metrics
- Custom business metrics

### Grafana Dashboards
Import the following dashboards:
- Application Overview
- Database Performance
- Infrastructure Metrics
- Business Metrics

### Alerting Rules
```yaml
# Example alerting rules
groups:
- name: messaging-platform
  rules:
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: High error rate detected
      
  - alert: DatabaseDown
    expr: up{job="mysql"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: Database is down
```

## 🔄 CI/CD Pipeline

### GitHub Actions Setup
1. Create repository secrets:
   - `KUBECONFIG`: Base64 encoded kubeconfig
   - `DOCKER_USERNAME`: Docker Hub username
   - `DOCKER_PASSWORD`: Docker Hub password
   - `STRIPE_SECRET_KEY`: Stripe secret key
   - `SMTP_PASSWORD`: SMTP password

2. Update workflow files:
   - Update image names in `.github/workflows/ci-cd.yml`
   - Update domain names in ingress configuration
   - Update secrets in Kubernetes manifests

### Deployment Process
1. **Code Push**: Triggered on push to main branch
2. **Build**: Docker images built and pushed to registry
3. **Test**: Automated tests run
4. **Deploy**: Images deployed to Kubernetes
5. **Verify**: Health checks and smoke tests
6. **Notify**: Slack/Discord notifications

## 🚨 Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check database pod
kubectl logs mysql-0 -n osop-messaging

# Check database connectivity
kubectl exec -it mysql-0 -n osop-messaging -- mysql -u root -p

# Check database credentials
kubectl get secret messaging-platform-secrets -n osop-messaging -o yaml
```

#### Redis Connection Issues
```bash
# Check Redis pod
kubectl logs redis-0 -n osop-messaging

# Test Redis connectivity
kubectl exec -it redis-0 -n osop-messaging -- redis-cli ping
```

#### Application Startup Issues
```bash
# Check application logs
kubectl logs -f deployment/messaging-platform-backend -n osop-messaging

# Check application configuration
kubectl describe configmap messaging-platform-config -n osop-messaging

# Check secrets
kubectl describe secret messaging-platform-secrets -n osop-messaging
```

#### Ingress Issues
```bash
# Check ingress controller
kubectl get pods -n ingress-nginx

# Check ingress status
kubectl describe ingress messaging-platform-ingress -n osop-messaging

# Check SSL certificates
kubectl get certificates -n osop-messaging
```

### Performance Optimization

#### Database Optimization
```sql
-- Add indexes for better performance
CREATE INDEX idx_campaigns_tenant_status ON campaigns(tenant_id, status);
CREATE INDEX idx_email_events_tenant_type ON email_events(tenant_id, event_type);
CREATE INDEX idx_subscribers_tenant_email ON subscribers(tenant_id, email);
```

#### Redis Optimization
```bash
# Configure Redis for production
redis-cli CONFIG SET maxmemory 1gb
redis-cli CONFIG SET maxmemory-policy allkeys-lru
```

#### Application Optimization
```yaml
# JVM tuning
env:
- name: JAVA_OPTS
  value: "-Xms512m -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

## 📈 Scaling

### Horizontal Scaling
```bash
# Scale backend
kubectl scale deployment messaging-platform-backend --replicas=5 -n osop-messaging

# Scale frontend
kubectl scale deployment messaging-platform-frontend --replicas=3 -n osop-messaging
```

### Vertical Scaling
```yaml
# Update resource limits
resources:
  requests:
    memory: "1Gi"
    cpu: "500m"
  limits:
    memory: "2Gi"
    cpu: "1000m"
```

### Database Scaling
- Use read replicas for read-heavy workloads
- Implement database sharding for large datasets
- Use connection pooling for better performance

## 🔒 Security Hardening

### Network Security
- Use network policies to restrict pod communication
- Enable TLS for all internal communication
- Use service mesh (Istio) for advanced security

### Application Security
- Regular security updates
- Vulnerability scanning
- Penetration testing
- Security headers configuration

### Data Security
- Encrypt data at rest
- Use secure key management
- Regular backup encryption
- Data retention policies

## 📞 Support

For deployment issues:
- Check logs: `kubectl logs -f deployment/messaging-platform-backend -n osop-messaging`
- Check events: `kubectl get events -n osop-messaging`
- Check resources: `kubectl top pods -n osop-messaging`
- Contact support: support@yourdomain.com
