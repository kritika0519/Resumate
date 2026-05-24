# ============================================
# Build Stage - Compile Java application
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy Maven config
COPY pom.xml .

# Copy source code
COPY src ./src

# Build JAR file
RUN mvn clean package -DskipTests

# ============================================
# Runtime Stage - Lightweight JRE image
# ============================================
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1000 appuser && adduser -D -u 1000 -G appuser appuser

# Copy built JAR from builder stage
COPY --from=builder /app/target/ai-resume-analyzer-1.0.0.jar .

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Start application with proper signal handling
ENTRYPOINT ["java", "-jar", "ai-resume-analyzer-1.0.0.jar"]
