# Makefile for CassandraGargoyle API
# Maven-based Java project build automation

.PHONY: all clean compile package test install deploy verify \
        install-local install-skip-tests \
        clean-install clean-package \
        help

# Default target
all: package

# ============================================================================
# Basic Maven Lifecycle
# ============================================================================

## Clean build artifacts
clean:
	mvn clean

## Compile source code
compile:
	mvn compile

## Run tests
test:
	mvn test

## Create JAR package
package:
	mvn package

## Run all verification (compile, test, package)
verify:
	mvn verify

# ============================================================================
# Installation Targets
# ============================================================================

## Install to local Maven repository (~/.m2/repository)
install:
	mvn install

## Install to local repository, skipping tests
install-skip-tests:
	mvn install -DskipTests

## Clean and install to local repository
clean-install:
	mvn clean install

## Clean and package (without install)
clean-package:
	mvn clean package

## Install to local repository with full clean
install-local: clean-install

# ============================================================================
# Deployment (Remote Repository)
# ============================================================================

## Deploy to configured remote repository (requires distributionManagement in pom.xml)
deploy:
	mvn deploy

## Deploy skipping tests
deploy-skip-tests:
	mvn deploy -DskipTests

# ============================================================================
# Development Utilities
# ============================================================================

## Show dependency tree
deps:
	mvn dependency:tree

## Check for dependency updates
deps-updates:
	mvn versions:display-dependency-updates

## Check for plugin updates
plugin-updates:
	mvn versions:display-plugin-updates

## Generate project site documentation
site:
	mvn site

## Run with debug output
debug:
	mvn -X compile

## Show effective POM
effective-pom:
	mvn help:effective-pom

## Show active profiles
profiles:
	mvn help:active-profiles

# ============================================================================
# Help
# ============================================================================

## Show this help message
help:
	@echo "CassandraGargoyle API - Makefile Help"
	@echo "======================================"
	@echo ""
	@echo "Basic Commands:"
	@echo "  make              - Build package (default)"
	@echo "  make clean        - Clean build artifacts"
	@echo "  make compile      - Compile source code"
	@echo "  make test         - Run tests"
	@echo "  make package      - Create JAR package"
	@echo "  make verify       - Run full verification"
	@echo ""
	@echo "Installation (Local Repository):"
	@echo "  make install           - Install to ~/.m2/repository"
	@echo "  make install-skip-tests - Install without running tests"
	@echo "  make clean-install     - Clean and install"
	@echo "  make install-local     - Alias for clean-install"
	@echo ""
	@echo "Deployment (Remote Repository):"
	@echo "  make deploy            - Deploy to remote repository"
	@echo "  make deploy-skip-tests - Deploy without running tests"
	@echo "  (Requires distributionManagement in pom.xml)"
	@echo ""
	@echo "Development Utilities:"
	@echo "  make deps          - Show dependency tree"
	@echo "  make deps-updates  - Check for dependency updates"
	@echo "  make plugin-updates - Check for plugin updates"
	@echo "  make site          - Generate project documentation"
	@echo "  make debug         - Compile with debug output"
	@echo "  make effective-pom - Show effective POM"
	@echo "  make profiles      - Show active profiles"
	@echo ""
	@echo "Project: org.cassandragargoyle:cassandragargoyle-parent"
	@echo "Java Version: 21"
