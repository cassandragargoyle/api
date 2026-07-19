# Makefile for CassandraGargoyle API
# Maven-based Java project build automation

.PHONY: all build clean compile package test install deploy verify \
        install-local install-skip-tests \
        clean-install clean-package \
        build-java test-java clean-java \
        lint lint-java lint-md lint-python \
        validate-contract \
        venv test-python build-python install-python lock \
        clean-python clean-venv \
        test-go vet-go \
        build-typescript test-typescript lint-typescript clean-typescript \
        help

MVN = mvn -f java/pom.xml
GO = go
NPM = npm --prefix typescript

# Default target
all: package

## Build all artifacts (Java JAR + Python wheel/sdist + TypeScript dist)
build: build-java build-python build-typescript

# ============================================================================
# Basic Maven Lifecycle
# ============================================================================

## Clean build artifacts
clean: clean-java clean-python clean-typescript

## Compile source code
compile:
	$(MVN) compile

## Run tests across all language modules (Java + Python + Go + TypeScript)
test: test-java test-python test-go test-typescript

## Create JAR package
package:
	$(MVN) package

## Run all verification (compile, test, package)
verify:
	$(MVN) verify

## Build Java JAR (package without tests)
build-java:
	$(MVN) package -DskipTests

## Run Java tests
test-java:
	$(MVN) test

## Clean Java build artifacts
clean-java:
	$(MVN) clean

# ============================================================================
# Installation Targets
# ============================================================================

## Install to local Maven repository (~/.m2/repository)
install:
	$(MVN) install

## Install to local repository, skipping tests
install-skip-tests:
	$(MVN) install -DskipTests

## Clean and install to local repository
clean-install:
	$(MVN) clean install

## Clean and package (without install)
clean-package:
	$(MVN) clean package

## Install to local repository with full clean
install-local: clean-install

# ============================================================================
# Deployment (Remote Repository)
# ============================================================================

## Deploy to configured remote repository (requires distributionManagement in pom.xml)
deploy:
	$(MVN) deploy

## Deploy skipping tests
deploy-skip-tests:
	$(MVN) deploy -DskipTests

# ============================================================================
# Development Utilities
# ============================================================================

## Show dependency tree
deps:
	$(MVN) dependency:tree

## Check for dependency updates
deps-updates:
	$(MVN) versions:display-dependency-updates

## Check for plugin updates
plugin-updates:
	$(MVN) versions:display-plugin-updates

## Generate project site documentation
site:
	$(MVN) site

## Run with debug output
debug:
	$(MVN) -X compile

## Show effective POM
effective-pom:
	$(MVN) help:effective-pom

## Show active profiles
profiles:
	$(MVN) help:active-profiles

# ============================================================================
# Linting
# ============================================================================

## Run all linters (Java + Python + TypeScript + Markdown)
lint: lint-java lint-python lint-typescript lint-md

## Run Java linter (Checkstyle)
lint-java:
	@echo "Running Java linter (Checkstyle)..."
	$(MVN) checkstyle:check

## Run Python linter (ruff)
lint-python: | $(PYTHON_VENV)
	@echo "Running Python linter (ruff)..."
	$(UV) run --extra dev ruff check python/

## Run Markdown linter
lint-md:
	@echo "Running Markdown linter..."
	markdownlint-cli2

# ============================================================================
# Contract Validation
# ============================================================================

## Validate contract examples against their JSON Schemas (Draft 2020-12)
validate-contract: | typescript/node_modules
	@echo "Validating contract examples against schemas..."
	$(NPM) run validate:contracts

# ============================================================================
# Python Targets (uv-managed)
# ============================================================================

# uv creates and manages the venv at python/.venv when run with --project python
UV = uv --project python
PYTHON_VENV = python/.venv

# System Python is used only by tooling outside the project venv (e.g. preflight)
ifeq ($(OS),Windows_NT)
    PYTHON_SYS = python
else
    PYTHON_SYS = python3
endif

## Create Python virtual environment and install dev dependencies (via uv)
venv:
	$(UV) sync --extra dev

## Run Python tests
test-python: | $(PYTHON_VENV)
	$(UV) run --extra dev pytest python/tests/

## Build Python wheel and sdist
build-python: | $(PYTHON_VENV)
	$(UV) build

## Install Python module in editable mode (uv sync installs editable by default)
install-python: | $(PYTHON_VENV)
	$(UV) sync --extra dev

## Refresh the lockfile (run after changing dependencies in pyproject.toml)
lock:
	$(UV) lock

## Clean Python build artifacts
clean-python:
	rm -rf python/dist/ python/build/ python/src/*.egg-info

## Remove Python virtual environment
clean-venv:
	rm -rf $(PYTHON_VENV)

$(PYTHON_VENV):
	$(MAKE) venv

# ============================================================================
# Go Targets
# ============================================================================

## Run Go tests
test-go:
	cd go && $(GO) test ./...

## Run Go static analysis (go vet)
vet-go:
	cd go && $(GO) vet ./...

# ============================================================================
# TypeScript Targets (npm-managed, package at typescript/)
# ============================================================================

# Install node_modules on first use (npm install is idempotent)
typescript/node_modules:
	$(NPM) install

## Build TypeScript package (ESM + CJS + .d.ts to typescript/dist)
build-typescript: | typescript/node_modules
	$(NPM) run build

## Run TypeScript tests (Vitest)
test-typescript: | typescript/node_modules
	$(NPM) test

## Run TypeScript linter (ESLint + Prettier)
lint-typescript: | typescript/node_modules
	@echo "Running TypeScript linter (ESLint + Prettier)..."
	$(NPM) run lint

## Clean TypeScript build artifacts
clean-typescript:
	rm -rf typescript/dist typescript/*.tgz

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
	@echo "  make build        - Build all artifacts (Java + Python + TypeScript)"
	@echo "  make clean        - Clean all build artifacts"
	@echo "  make compile      - Compile source code"
	@echo "  make test         - Run tests across all language modules (Java + Python + Go + TypeScript)"
	@echo "  make package      - Create JAR package"
	@echo "  make verify       - Run full verification"
	@echo ""
	@echo "Java:"
	@echo "  make build-java        - Build JAR (skip tests)"
	@echo "  make test-java         - Run Java tests"
	@echo "  make clean-java        - Clean Java build artifacts"
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
	@echo "  (Requires distributionManagement in java/pom.xml)"
	@echo ""
	@echo "Linting:"
	@echo "  make lint              - Run all linters (Java + Python + TypeScript + Markdown)"
	@echo "  make lint-java         - Run Java linter (Checkstyle)"
	@echo "  make lint-python       - Run Python linter (ruff)"
	@echo "  make lint-typescript   - Run TypeScript linter (ESLint + Prettier)"
	@echo "  make lint-md           - Run Markdown linter (markdownlint-cli2)"
	@echo ""
	@echo "Contract Validation:"
	@echo "  make validate-contract - Validate contract examples against JSON Schemas"
	@echo ""
	@echo "Python (uv-managed, venv at python/.venv):"
	@echo "  make venv              - Create venv and install dev dependencies (uv sync)"
	@echo "  make test-python       - Run Python tests"
	@echo "  make build-python      - Build wheel and sdist (uv build)"
	@echo "  make install-python    - Install/sync project in editable mode"
	@echo "  make lock              - Refresh uv.lock after dependency changes"
	@echo "  make clean-python      - Clean Python build artifacts"
	@echo "  make clean-venv        - Remove Python virtual environment"
	@echo ""
	@echo "Go:"
	@echo "  make test-go           - Run Go tests"
	@echo "  make vet-go            - Run Go static analysis (go vet)"
	@echo ""
	@echo "TypeScript (npm-managed, package at typescript/):"
	@echo "  make build-typescript  - Build ESM + CJS + .d.ts to typescript/dist"
	@echo "  make test-typescript   - Run TypeScript tests (Vitest)"
	@echo "  make lint-typescript   - Run ESLint + Prettier"
	@echo "  make clean-typescript  - Clean TypeScript build artifacts"
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
	@echo "Project: org.cassandragargoyle:org-cassandragargoyle-api"
	@echo "Java Version: 21"
