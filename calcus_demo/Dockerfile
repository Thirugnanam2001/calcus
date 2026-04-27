# Multi-stage build for smaller image size
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy jar from build stage
COPY --from=build /app/target/calcus_demo-1.0.0.jar/app/ calcus_demo-1.0.0.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/ || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "calcus_demo-1.0.0.jar"]
<<<<<<< HEAD







# Use Eclipse Temurin JDK 17
#FROM eclipse-temurin:17-jdk-alpine
#
# Set working directory
#WORKDIR /app
#
# Copy jar file (built by Maven)
#COPY target/calcus_demo-1.0.0.jar/app/ calcus_demo-1.0.0.jar
#
# Expose port
#EXPOSE 8082
#
#Health check for Render
#HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
#  CMD wget --quiet --tries=1 --spider http://localhost:8082/ || exit 1
#
# Run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]
=======
>>>>>>> 1d90076282055c6cb495d1e3b4ae3b73008bf955
