# Use Eclipse Temurin JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file (built by Maven)
COPY target/calcus_demo-1.0.0.jar/app/ calcus_demo-1.0.0.jar

# Expose port
EXPOSE 8082

# Health check for Render
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8082/ || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]