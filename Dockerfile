# Build the executable JAR first with ./mvnw clean package. The database is intentionally never
# copied into this image; mount a readable SQLite file and set OLIST_DB_PATH at runtime.
FROM eclipse-temurin:26-jre-ubi10-minimal

WORKDIR /opt/olist

COPY --chown=10001:0 target/olist-eda-dashboard-spring-0.0.1-SNAPSHOT.jar app.jar

# A numeric non-root identity avoids depending on an image-specific user-management utility.
USER 10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/olist/app.jar"]
