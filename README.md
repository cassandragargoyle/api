# CassandraGargoyle API

API module for the CassandraGargoyle project - a Java-based application framework built on NetBeans Platform.

## Documentation

See [docs/contributing/](docs/contributing/) for development guidelines.

## Project Information

- **Version:** 1.0.0.2-SNAPSHOT
- **Java Version:** 21
- **NetBeans Platform:** RELEASE120-1
- **Build Tool:** Maven
- **GitHub Repository**: https://github.com/CassandraGargoyle/Api
- **Project Name**: Api

```
Api/
├── api/           # Core API module
└── persistance/   # Persistence layer module
```

## Core Features

### Entity Management
- Base entity abstractions and implementations
- Version management
- Platform-specific entity handling
- Diagram, Node, and Edge entities for graph-like structures
- Data container entities for data management

### Software Management
- Software entity definitions
- Code language support
- Operating system type detection
- Software categorization and features
- Platform compatibility handling

### Utilities
- **Date Utilities:** Date manipulation and formatting
- **String Utilities:** String processing and manipulation
- **System Utilities:** System-level operations
- **Preferences Utilities:** Application preferences management
- **Base64 Encoding:** Data encoding utilities
- **OS Detection:** Operating system detection and identification

### Logging
- Custom logging framework
- Log factory pattern implementation
- Exception handling for logging operations

### CLI Support
- Command-line interface implementation
- Apache Commons CLI integration

## Dependencies

### Core Dependencies
- **NetBeans Platform Modules**
  - `org-openide-util`
  - `org-openide-util-ui`
  - `org-openide-util-lookup`
  - `org-openide-filesystems`
  - `org-openide-modules`

- **Spring Framework** (5.3.39)
  - Dependency injection and component management

- **Apache Commons**
  - `commons-cli` (1.5.0)
  - `commons-lang3` (3.17.0)
  - `commons-text` (1.12.0)

- **Project Lombok** (1.18.30)
  - Code generation and boilerplate reduction

### Testing
- JUnit Jupiter (5.3.1)

## Building the Project

```bash
mvn clean install
```

## Running Tests

```bash
mvn test
```

## Development

This project uses:
- Maven for build management
- Spring for dependency injection
- NetBeans Platform for application framework

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

This software is developed and maintained by the CassandraGargoyle Community.

## Contributing

[Add contribution guidelines here]
