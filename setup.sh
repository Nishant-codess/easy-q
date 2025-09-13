#!/bin/bash

# Easy-Q Setup Script
# This script sets up the Git repository and creates all necessary branches

echo "🚀 Setting up Easy-Q Git repository..."

# Initialize Git repository
echo "📁 Initializing Git repository..."
git init

# Add all files
echo "📝 Adding files to Git..."
git add .

# Initial commit
echo "💾 Creating initial commit..."
git commit -m "Initial commit: Easy-Q Digital Queue & Appointment Manager

- Complete Spring Boot 3.x application with Java 17+
- Admin module with analytics dashboard and real-time monitoring
- Booking module for appointment scheduling
- Queue module with WebSocket real-time updates
- Notification module with scheduled jobs
- MySQL database schema with seed data
- Bootstrap 5 frontend with Chart.js visualizations
- GitHub Actions CI/CD pipeline
- Comprehensive documentation"

# Create and switch to main branch
echo "🌿 Creating main branch..."
git branch -M main

# Create development branch
echo "🌿 Creating dev branch..."
git checkout -b dev

# Create feature branches
echo "🌿 Creating feature branches..."

# Booking feature branch
git checkout -b feature/booking
git checkout dev

# Queue feature branch
git checkout -b feature/queue
git checkout dev

# Notification feature branch
git checkout -b feature/notification
git checkout dev

# Admin feature branch
git checkout -b feature/admin
git checkout dev

# Return to main branch
git checkout main

echo "✅ Git repository setup complete!"
echo ""
echo "📋 Created branches:"
echo "  - main (production)"
echo "  - dev (development)"
echo "  - feature/booking"
echo "  - feature/queue"
echo "  - feature/notification"
echo "  - feature/admin"
echo ""
echo "🔗 To push to GitHub:"
echo "  1. Create a new repository on GitHub"
echo "  2. Add remote: git remote add origin <your-github-repo-url>"
echo "  3. Push all branches: git push -u origin --all"
echo ""
echo "📖 Next steps:"
echo "  1. Set up MySQL database using database/schema.sql"
echo "  2. Update application.properties with your database credentials"
echo "  3. Run: mvn spring-boot:run"
echo "  4. Access: http://localhost:8080/admin"
echo ""
echo "🎉 Easy-Q is ready for development!"
