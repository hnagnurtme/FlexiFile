# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:resolve

# Copy the rest of the application source code
COPY src ./src

# Build the application, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Create the final image with Tomcat
FROM tomcat:10.1.24-jre17-temurin-jammy

# Remove the default ROOT webapp
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy the WAR file from the builder stage to Tomcat's webapps directory
COPY --from=builder /app/target/FlexiFile.war /usr/local/tomcat/webapps/FlexiFile.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
