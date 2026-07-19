---
name: deploy-local
description: Deploy locally built Api JAR to the local Maven repository (~/.m2/repository). Verifies the build and confirms the installed artifact. Použij, když chce uživatel nasadit lokálně sestavený Api JAR do lokálního Maven repozitáře.
disable-model-invocation: true
---

# Deploy Api to local Maven repository

## Workflow

### STEP 1: Check state before deployment

Check the current project version and existing local installation:

```bash
mvn -f java/pom.xml help:evaluate -Dexpression=project.version -q -DforceStdout
```

Check if the artifact already exists in the local repository:

```bash
ls ~/.m2/repository/org/cassandragargoyle/org-cassandragargoyle-api/ 2>/dev/null
```

Show the user:

- Current version from pom.xml
- Already installed versions (if any)
- Whether there are uncommitted changes (`git status --short`)

### STEP 2: Build and install to local Maven repository

**IMPORTANT**: Always use `clean install` to ensure a fresh build with all tests passing:

```bash
make install-local
```

This runs `mvn -f java/pom.xml clean install`, which:

1. Cleans previous build artifacts
2. Compiles the source code
3. Runs all tests
4. Packages the JAR
5. Installs to `~/.m2/repository`

If tests fail, report the failures to the user and stop. Do NOT skip tests unless the user explicitly requests it.

If the user explicitly asks to skip tests:

```bash
make install-skip-tests
```

### STEP 3: Verify deployment

After successful installation, verify the artifact in the local repository:

```bash
ls -la ~/.m2/repository/org/cassandragargoyle/org-cassandragargoyle-api/
```

Check the installed JAR:

```bash
jar tf ~/.m2/repository/org/cassandragargoyle/org-cassandragargoyle-api/$(mvn -f java/pom.xml help:evaluate -Dexpression=project.version -q -DforceStdout)/org-cassandragargoyle-api-$(mvn -f java/pom.xml help:evaluate -Dexpression=project.version -q -DforceStdout).jar | head -20
```

Show the user:

- Installed version
- JAR file path and size
- Confirmation that the artifact is available for other local projects

## Notes

- **SNAPSHOT versions**: Maven treats SNAPSHOT versions specially — they are always overwritten on install
- **Release versions**: If you install a release version that already exists, Maven will overwrite it
- **Other projects**: After installation, other local Maven projects can depend on this artifact by adding the dependency to their pom.xml
- **Python module**: This command only deploys the Java artifact. For Python, use `make install-python`
