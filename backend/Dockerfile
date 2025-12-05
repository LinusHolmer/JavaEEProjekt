# Steg 1: Byggmiljö
FROM openjdk:21-jdk-slim AS build

# Installera Gradle
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.3-bin.zip && \
    unzip gradle-8.3-bin.zip && \
    mv gradle-8.3 /opt/gradle && \
    ln -s /opt/gradle/bin/gradle /usr/bin/gradle

# Sätt arbetskatalog
WORKDIR /app

# Kopiera projektfiler
COPY . .

# Bygg applikationen
RUN gradle clean build -x test

# Steg 2: Körmiljö
FROM openjdk:21-jdk-slim

# Sätt arbetskatalog
WORKDIR /app

# Kopiera den byggda JAR-filen från byggsteget
COPY --from=build /app/build/libs/*.jar app.jar

# Exponera porten som applikationen kommer att använda
EXPOSE 8080

# Starta applikationen
ENTRYPOINT ["java", "-jar", "app.jar"]