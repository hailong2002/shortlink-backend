# Stage 1: Build mã nguồn bằng Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copy file pom.xml và download dependencies trước để tận dụng cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ code và build ra file JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Chạy ứng dụng với JRE nhẹ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy file jar từ stage build sang (đảm bảo tên file .jar đúng với pom.xml)
COPY --from=build /app/target/*.jar app.jar

# Khai báo port ứng dụng (thường là 8080)
EXPOSE 8080

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java","-Xms4g","-Xmx4g","-XX:+UseG1GC","-Dserver.servlet.context-path=/svc","-Dserver.port=10082","-Dserver.tomcat.threads.max=200","-Dserver.tomcat.threads.min-spare=50","-Dserver.tomcat.accept-count=500","-Dserver.tomcat.connection-timeout=60000","-Dspring.datasource.hikari.maximum-pool-size=100","-Dspring.datasource.hikari.minimum-idle=20","-Dspring.datasource.hikari.connection-timeout=30000","--enable-preview","-jar","/app/app.jar"]