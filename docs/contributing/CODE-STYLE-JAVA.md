# Java Code Style Guidelines

## Purpose

This document defines the Java coding standards for CassandraGargoyle projects, based on industry best practices and established Java conventions.

## General Principles

### 1. Follow Java Conventions

- Use Oracle's Java Code Conventions as baseline
- Follow naming conventions strictly
- Use modern Java features (Java 21 idioms)
- Embrace object-oriented design principles

### 2. Code Quality

- Write self-documenting code
- Prefer composition over inheritance
- Use interfaces for contracts
- Apply SOLID principles

## File and Package Structure

### File Naming

- Use PascalCase matching class name: `AbstractEntity.java`
- One public class per file
- File name must match public class name

### Package Naming

Use reverse domain notation with lowercase:

```java
✅ org.cassandragargoyle.api.entity
✅ org.cassandragargoyle.api.util
✅ org.cassandragargoyle.persistence
❌ org.cassandragargoyle.Api.entity
❌ org.cassandragargoyle.api.Entity
```

### Directory Structure

```text
api/
├── api/
│   └── src/
│       ├── main/java/org/cassandragargoyle/api/
│       │   ├── entity/
│       │   │   ├── AbstractEntity.java
│       │   │   ├── Entity.java
│       │   │   └── ...
│       │   ├── log/
│       │   │   ├── LogFactory.java
│       │   │   └── Logging.java
│       │   ├── software/
│       │   │   ├── SoftwareEntity.java
│       │   │   └── ...
│       │   └── util/
│       │       ├── DateUtil.java
│       │       ├── StringUtil.java
│       │       └── ...
│       └── test/java/org/cassandragargoyle/api/
│           └── util/
│               └── OSDetectorTest.java
└── persistence/
    └── src/
        └── main/java/org/cassandragargoyle/persistence/
            └── EntityRepository.java
```

## Naming Conventions

### Classes and Interfaces

- **Classes**: PascalCase (`AbstractEntity`, `LogFactory`)
- **Interfaces**: PascalCase, often with `-able` suffix or clear contract names
- **Abstract classes**: PascalCase, consider `Abstract` prefix if needed

```java
// Classes
public class LogFactory { }
public class SoftwareEntity { }

// Interfaces
public interface Entity { }
public interface EntityRepository<T, ID> { }

// Abstract classes
public abstract class AbstractEntity { }
public abstract class AbstractFileEntity { }
```

### Methods

Use camelCase with verbs describing actions:

```java
// Good method names
public Object getProperty(String key) { }
public boolean isDeleted() { }
public String getPropertyStr(String key) { }
public List<T> findAll() { }

// Avoid
public void entity() { }  // Not descriptive
public boolean deleted() { }  // Missing context
```

### Variables and Fields

- **Instance/Local variables**: camelCase
- **Constants**: ALL_CAPS with underscores
- **Static final fields**: ALL_CAPS with underscores

```java
public class AbstractEntity extends BaseClass implements Entity
{
	// Constants
	protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	// Instance fields
	protected Properties properties = new Properties();
	Entity parent;

	public AbstractEntity(String name)
	{
		setProperty("name", name);
	}
}
```

## Code Formatting

### Indentation and Spacing

- Use tabs for indentation (not spaces)
- Maximum line length: 120 characters
- Use blank lines to separate logical blocks

### Braces and Line Breaks

Follow Allman style (braces on new line):

```java
// Method declarations
public void methodName()
{
	if (condition)
	{
		// statements
	}
	else
	{
		// statements
	}

	for (String item : items)
	{
		processItem(item);
	}
}

// Class declarations
public class ClassName
{
	// content
}
```

### Multi-line Method Calls

> **TODO: PENDING VALIDATION** - Continuation line alignment needs to be validated by authority.

When a method call spans multiple lines, align continuation arguments under the first argument:

```java
// Correct - continuation aligned under first argument
LOG.log(Level.FINE, "Executing with processor: {0}, verbose: {1}",
		LogFactory.args(processorType, verbose));

// Correct - builder pattern with single tab indent
return Optional.of(entity)
	.filter(Entity::isDeleted)
	.map(Entity::getName)
	.orElse(defaultName);
```

### Multi-line String Concatenation

> **TODO: PENDING VALIDATION** - String concatenation line break style needs to be validated by authority.

When string concatenation spans multiple lines, put `+` operator at the beginning of continuation line:

```java
// Correct - + at beginning of new line, aligned under first character
throw new IllegalArgumentException("Unsupported format: " + format
								   + ". Supported formats: " + String.join(", ", SUPPORTED_FORMATS));
```

## Import Statements

### Import Organization

Group imports in this order with blank lines between groups:

1. Java standard library (`java.*`, `javax.*`)
2. Third-party libraries
3. Local project packages

```java
// Standard library
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

// Third-party libraries
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Exceptions;

// Local packages
import org.cassandragargoyle.api.entity.Entity;
import org.cassandragargoyle.api.log.LogFactory;
```

### Import Guidelines

- Avoid wildcard imports (`import java.util.*;`)
- Use static imports sparingly, only for frequently used utility methods
- Organize imports alphabetically within groups

```java
// Acceptable static imports
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
```

## Class Design

### Enums

> **TODO: PENDING VALIDATION** - Empty line before closing brace in enum needs to be validated by authority.

```java
public enum CodeLanguage
{
	JAVA,
	PYTHON,
	GO,
	CPP

}
```

### Class Structure

Organize class members in this order:

1. Static constants
2. Static variables
3. Instance variables
4. Enums (if inner)
5. Constructors
6. Static methods
7. Instance methods
8. Nested classes

```java
public class LogFactory
{
	// 1. Static constants
	public static final String LOG_IGNOREDLOGGERS = "CassandraGargoyle/Log/IngoredLoggers/";
	public static final String LOG_FORCEDMESSAGES = "CassandraGargoyle/Log/ForcedMessages/";

	// 2. Static variables
	// (none in this example)

	// 3. Instance variables
	// (none in this example)

	// 5. Constructors
	// (none - utility class)

	// 6. Static methods
	public static Logger getLogger(Class clazz)
	{
		String className = clazz.getName();
		String resourceBundleName = clazz.getPackage().getName() + ".Bundle";
		try
		{
			return Logger.getLogger(className, resourceBundleName);
		}
		catch (MissingResourceException ex)
		{
			return Logger.getLogger(className);
		}
	}

	public static Object[] args(Object... args)
	{
		return args;
	}

	// 8. Nested classes / annotations
	@Retention(RetentionPolicy.SOURCE)
	@Target({ElementType.FIELD})
	public @interface IgnoreLoggerForUI
	{
		// no parameters needed
	}
}
```

## Error Handling

### Exception Handling Best Practices

1. Use specific exception types
2. Always include meaningful error messages
3. Log errors appropriately
4. Clean up resources in finally blocks or use try-with-resources

```java
public Configuration loadConfiguration(String configPath) throws ConfigurationException
{
	if (configPath == null || configPath.trim().isEmpty())
	{
		throw new IllegalArgumentException("Configuration path cannot be null or empty");
	}

	try (InputStream inputStream = Files.newInputStream(Paths.get(configPath)))
	{
		return parseConfiguration(inputStream);
	}
	catch (IOException e)
	{
		LOG.log(Level.SEVERE, "Failed to read configuration file: {0}", configPath);
		throw new ConfigurationException("Failed to read configuration file: " + configPath, e);
	}
}
```

### Custom Exceptions

Create domain-specific exception classes:

```java
public class ConfigurationException extends Exception
{
	public ConfigurationException(String message)
	{
		super(message);
	}

	public ConfigurationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
```

## Logging

### Logger Declaration

All classes use consistent logging with `LogFactory` from `org.cassandragargoyle.api.log`:

```java
import org.cassandragargoyle.api.log.LogFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyService
{
	private static final Logger LOG = LogFactory.getLogger(MyService.class);

	public void doSomething()
	{
		LOG.log(Level.INFO, "Starting operation");
		LOG.log(Level.FINE, "Processing entity: {0}", entityName);
		LOG.log(Level.WARNING, "Warning for entity: {0}", entityName);
		LOG.log(Level.SEVERE, "Error processing: {0}, cause: {1}", LogFactory.args(entityName, exception.getMessage()));
	}
}
```

### Logging Guidelines

- **Logger name**: Always use `LOG` (not `LOGGER` or other names)
- **Logger factory**: Use `LogFactory.getLogger()` from `org.cassandragargoyle.api.log`
- **Logger type**: Use `java.util.logging.Logger`
- **Logging method**: Use `LOG.log(Level.XXX, message, args)`
- **Parameters**:
  - Single parameter: pass directly as third argument
  - Multiple parameters: use `LogFactory.args(...)`
  - Use `{0}`, `{1}`, etc. as placeholders (java.util.logging format)
- **Log levels** (java.util.logging.Level):
  - `Level.SEVERE` - For errors that need attention
  - `Level.WARNING` - For potential issues
  - `Level.INFO` - For important operational messages
  - `Level.FINE` / `Level.FINER` / `Level.FINEST` - For detailed debugging information
  - `Level.CONFIG` - For configuration information

## Documentation and Comments

### Javadoc Comments

Use Javadoc for all public APIs.

> **TODO: PENDING VALIDATION** - The following Javadoc formatting styles need to be validated by authority:
> - `<p>` tag on separate line
> - `<li>` tags without indentation inside `<ul>`

```java
/**
 * Generic repository interface for entity persistence operations
 *
 * @param <T> the entity type
 * @param <ID> the identifier type
 * @author Zdenek
 * @since 2026-03-01
 */
public interface EntityRepository<T, ID>
{
	Optional<T> findById(ID id);

	List<T> findAll();

	T save(T entity);

	void delete(T entity);
}
```

### Javadoc @param Alignment

> **TODO: PENDING VALIDATION** - Javadoc @param alignment style needs to be validated by authority.

When method has multiple parameters, align descriptions into columns:

```java
/**
 * Registers a custom transformer.
 *
 * @param name        the transformer name
 * @param transformer the transformation function
 * @return true if registered successfully, false if name already exists
 */
public boolean registerTransformer(String name, Function<String, String> transformer)
{
	// implementation
}
```

### Inline Comments

- Explain complex algorithms and business logic
- Avoid stating the obvious
- Keep comments up to date with code changes

## Modern Java Features

### Streams API

Use streams for collection processing:

```java
public List<String> getActiveEntityNames()
{
	return entities.stream()
		.filter(e -> !e.isDeleted())
		.map(Entity::getName)
		.sorted()
		.collect(Collectors.toList());
}

public Optional<Entity> findByName(String name)
{
	return entities.stream()
		.filter(e -> e.getName().equals(name))
		.findFirst();
}
```

### Optional Usage

Use Optional to handle nullable values:

```java
public Optional<Entity> findEntity(String id)
{
	return repository.findById(id);
}

public void processEntity(String entityId)
{
	findEntity(entityId)
		.ifPresentOrElse(
			this::applyChanges,
			this::handleNotFound
		);
}
```

### Lambda Expressions

Use lambdas for functional interfaces:
```java
// Comparators
entities.sort((e1, e2) -> e1.getName().compareTo(e2.getName()));

// Custom functional interfaces
public interface EntityProcessor
{
	void process(Entity entity) throws ProcessingException;
}
```

## Testing Guidelines

### Test Class Organization

- Use same package structure as main code
- Name test classes with `Test` suffix
- Group tests logically using nested classes

```java
public class EntityRepositoryTest
{

	private EntityRepository<TestEntity, Long> repository;

	@BeforeEach
	void setUp()
	{
		repository = new InMemoryEntityRepository<>();
	}

	@Nested
	@DisplayName("Find Operations")
	class FindOperations
	{

		@Test
		@DisplayName("Should find entity by id")
		void shouldFindEntityById()
		{
			// Arrange
			TestEntity entity = new TestEntity("test");
			repository.save(entity);

			// Act & Assert
			assertTrue(repository.existsById(entity.getId()));
		}

		@Test
		@DisplayName("Should return empty for non-existent id")
		void shouldReturnEmptyForNonExistentId()
		{
			// Act & Assert
			assertTrue(repository.findById(999L).isEmpty());
		}
	}
}
```

### Test Naming

Use descriptive test method names:

```java
// Good test names
@Test void shouldReturnTrueWhenEntityExists() { }
@Test void shouldThrowExceptionWhenPropertyNotFound() { }
@Test void shouldDetectLinuxOperatingSystem() { }

// Avoid generic names
@Test void testFind() { }
@Test void testEntity() { }
```

## Dependency Management

### Maven Configuration

Use Maven for dependency management with clear grouping. Versions are managed centrally in the parent POM (`cassandragargoyle-parent`):

```xml
<dependencies>
    <!-- NetBeans modules -->
    <dependency>
        <groupId>org.netbeans.api</groupId>
        <artifactId>org-openide-util</artifactId>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
    </dependency>

    <!-- Apache Commons -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
</dependencies>
```

Module POMs inherit version numbers from the parent - do not specify versions in child modules.

## Security Best Practices

### Input Validation

Always validate external input:

```java
public void setProperty(String key, Object value)
{
	if (key == null || key.trim().isEmpty())
	{
		throw new IllegalArgumentException("Property key cannot be null or empty");
	}
	properties.put(key, value);
}
```

### Resource Management

Use try-with-resources for automatic resource cleanup:

```java
public String readConfigurationFile(String path) throws IOException
{
	try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8))
	{
		return reader.lines()
			.collect(Collectors.joining(System.lineSeparator()));
	}
}
```

## Performance Guidelines

### Collection Usage

Choose appropriate collection types:

```java
// For frequent lookups
private final Map<String, Entity> entityCache = new HashMap<>();

// For ordered data
private final List<String> propertyKeys = new ArrayList<>();

// For unique items
private final Set<String> knownCategories = new HashSet<>();

// For thread-safe access
private final Map<String, Entity> threadSafeCache = new ConcurrentHashMap<>();
```

### String Handling

Use StringBuilder for string concatenation in loops:

```java
public String buildPropertySummary(Properties properties)
{
	StringBuilder summary = new StringBuilder();
	for (String key : properties.keySet())
	{
		summary.append(key).append("=").append(properties.get(key)).append("\n");
	}
	return summary.toString();
}
```

## Build Configuration

### Maven Plugins

The parent POM manages plugin versions centrally via `<pluginManagement>`:

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <release>${java.version}</release>
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>${maven.jar.plugin.version}</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>${maven.surefire.plugin.version}</version>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

## Code Review Checklist

### Before Submitting Code

- [ ] Code follows naming conventions
- [ ] All public APIs have Javadoc
- [ ] Exception handling is appropriate
- [ ] Tests cover new functionality
- [ ] No code smells or warnings
- [ ] Imports are organized correctly
- [ ] Resource cleanup is handled properly

### Design Review Points

- [ ] Classes have single responsibility
- [ ] Methods are focused and concise
- [ ] Appropriate design patterns used
- [ ] Error handling strategy is consistent
- [ ] Performance considerations addressed
- [ ] Security implications considered

---

**Note**: These guidelines should be adapted based on specific project requirements and team preferences. Regular review and updates ensure alignment with evolving best practices.

*Created: 2025-08-23*
*Last updated: 2026-03-01*
