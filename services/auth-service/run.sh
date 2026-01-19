#!/bin/bash

# Set Java 21
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# Verify Java version
echo "Using Java:"
java -version

# Run Spring Boot application
echo ""
echo "Starting Auth Service..."
mvn spring-boot:run
