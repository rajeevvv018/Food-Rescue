# Stage 1: Build FoodRescue Maven WAR
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app

# Copy project definition and source code
COPY pom.xml .
COPY src ./src

# Build the WAR package
RUN mvn clean package -DskipTests

# Stage 2: Apache Tomcat 11 Runtime
FROM tomcat:11

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy FoodRescue as the ROOT application (context path /)
COPY --from=build /app/target/FoodRescue.war /usr/local/tomcat/webapps/ROOT.war

# Enable cross-site cookie support for cross-origin frontend (SameSite=None)
RUN sed -i 's/<Context>/<Context><CookieProcessor sameSiteCookies="none" \/>/' /usr/local/tomcat/conf/context.xml

# Enable RemoteIpValve so Tomcat detects HTTPS behind Render reverse proxy
RUN sed -i '/<\/Host>/i \        <Valve className="org.apache.catalina.valves.RemoteIpValve" remoteIpHeader="x-forwarded-for" protocolHeader="x-forwarded-proto" \/>' /usr/local/tomcat/conf/server.xml

# Memory limits optimized for Render 512MB RAM free tier
ENV JAVA_OPTS="-Xms128m -Xmx300m -XX:+UseG1GC"

# Expose HTTP port
EXPOSE 8080

# Run Tomcat
CMD ["catalina.sh", "run"]
