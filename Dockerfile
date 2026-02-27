# Use official Tomcat 10 with Java 17 as base image
FROM tomcat:10.1-jdk17

# Remove default Tomcat apps for clean deployment
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy our built WAR file into Tomcat webapps
COPY target/hello-world.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
