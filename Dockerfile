# Stage 1: Build the application
FROM gradle:8.0-jdk17 AS build

# Set the working directory in the container
WORKDIR /workspace/Team12_BE

# Copy local code to the container
COPY . .


# If you're behind a proxy, set the proxy settings
RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

RUN gradle wrapper

# Build the application
RUN ./gradlew build -x test

# Stage 2: Run the application
FROM krmp-d2hub-idock.9rum.cc/goorm/eclipse-temurin:17-jre


# Copy the built jar file from the build stage into the current stage
COPY --from=build /workspace/Team12_BE/build/libs/pickupShuttle-0.0.1-SNAPSHOT.jar .

# Set environment variables
ENV DATABASE_URL=jdbc:mysql://pickup-mysql-dev/pickup

# Start the application
CMD ["java", "-jar", "-Dspring.profiles.active=dev", "pickupShuttle-0.0.1-SNAPSHOT.jar"]
