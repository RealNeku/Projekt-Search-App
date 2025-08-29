#!/bin/bash

# German MWE Extractor Runner Script
# This script builds and runs the German MWE extractor with various options

echo "German Multiword Expression Extractor"
echo "======================================"

# Change to project directory
cd group6LinguisticSearch

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven to build the project"
    exit 1
fi

# Clean and compile the project
echo "Building the project..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

echo "Build successful!"
echo ""

# Function to run the main application
run_main_app() {
    echo "Running German MWE Extractor..."
    mvn exec:java -Dexec.mainClass="group6.java.GermanMWEExtractorApp" -Dexec.args="$*"
}

# Function to run the demo
run_demo() {
    echo "Running German MWE Extractor Demo..."
    mvn exec:java -Dexec.mainClass="group6.java.demo.GermanMWEDemo"
}

# Parse command line arguments
if [ $# -eq 0 ]; then
    echo "Usage: $0 [demo|url|title|search|random|category] [additional_args...]"
    echo ""
    echo "Examples:"
    echo "  $0 demo                                    # Run demonstration"
    echo "  $0 title 'Deutsche_Sprache'               # Extract from specific article"
    echo "  $0 search 'Bundeskanzler' 3               # Search and extract from 3 articles"
    echo "  $0 random 2                               # Extract from 2 random articles"
    echo "  $0 url 'https://de.wikipedia.org/wiki/...' # Extract from URL"
    echo "  $0 category 'Deutsche_Sprache' 5          # Extract from category"
    exit 1
fi

COMMAND=$1
shift

case $COMMAND in
    demo)
        run_demo
        ;;
    url|title|search|random|category)
        run_main_app $COMMAND "$@"
        ;;
    *)
        echo "Unknown command: $COMMAND"
        echo "Available commands: demo, url, title, search, random, category"
        exit 1
        ;;
esac

echo ""
echo "Execution completed!"