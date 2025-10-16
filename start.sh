#!/bin/bash

# Pharmacovigilance MCP Agent Startup Script

echo "Starting Pharmacovigilance MCP Agent..."

# Check if Java 17+ is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "Error: Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    exit 1
fi

# Set environment variables if .env file exists
if [ -f .env ]; then
    echo "Loading environment variables from .env file..."
    export $(cat .env | grep -v '^#' | xargs)
fi

# Check if OpenAI API key is set
if [ -z "$OPENAI_API_KEY" ]; then
    echo "Warning: OPENAI_API_KEY is not set. AI features will not work."
    echo "Please set your OpenAI API key:"
    echo "export OPENAI_API_KEY=your-api-key-here"
    echo "Or create a .env file with: OPENAI_API_KEY=your-api-key-here"
fi

# Build the application
echo "Building application..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

# Start the application
echo "Starting application..."
java -jar target/pharmacovigilance-mcp-agent-1.0.0.jar

echo "Application started successfully!"
echo "Access the application at: http://localhost:8080/pharmacovigilance"
echo "Access H2 console at: http://localhost:8080/pharmacovigilance/h2-console"
echo "API documentation available at: http://localhost:8080/pharmacovigilance/api/mcp/tools"


