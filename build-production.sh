#!/bin/bash

# Easy-Q Production Build Script

echo "🚀 Building Easy-Q for Production..."

# Clean previous builds
echo "🧹 Cleaning previous builds..."
mvn clean

# Run tests
echo "🧪 Running tests..."
mvn test

# Build production JAR
echo "📦 Building production JAR..."
mvn package -DskipTests -Pprod

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📁 JAR file location: target/easy-q-1.0.0.jar"
    echo ""
    echo "🚀 To run the application:"
    echo "java -jar target/easy-q-1.0.0.jar --spring.profiles.active=prod"
    echo ""
    echo "📋 Don't forget to set your environment variables:"
    echo "- DATABASE_URL"
    echo "- DATABASE_USERNAME" 
    echo "- DATABASE_PASSWORD"
    echo "- SMTP_USERNAME"
    echo "- SMTP_PASSWORD"
    echo "- ADMIN_USERNAME"
    echo "- ADMIN_PASSWORD"
else
    echo "❌ Build failed!"
    exit 1
fi

