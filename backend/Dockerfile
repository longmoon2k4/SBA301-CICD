FROM eclipse-temurin:21-jre-alpine

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy file JAR đã được build sẵn từ thư mục target ở local
COPY target/ecommerce.jar app.jar

# Expose port mặc định của ứng dụng
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
