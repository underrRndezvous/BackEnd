# Multi-stage build for Spring Boot application
FROM gradle:8.5-jdk17 as build

# Set working directory
WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Copy source code
COPY src src

# Build application
RUN gradle build --no-daemon -x test

# Runtime stage
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN addgroup --system spring && adduser --system --group spring

# Copy built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]