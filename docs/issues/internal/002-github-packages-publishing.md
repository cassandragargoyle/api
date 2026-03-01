# Issue #002: GitHub Packages publishing workflow

**Type**: Feature
**Priority**: Medium
**Status**: New
**Created**: 2026-03-01
**Labels**: ci, maven, github
**Related**: #001
**GitHub Issue**: [cassandragargoyle/api#6](https://github.com/cassandragargoyle/api/issues/6)
**Repository**: Api

## Description

Implement GitHub Packages publishing for CassandraGargoyle API artifacts. This is the concrete implementation task derived from the architecture decisions in #001.

## Tasks

### 1. Add distributionManagement to pom.xml

```xml
<distributionManagement>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/cassandragargoyle/api</url>
    </repository>
</distributionManagement>
```

### 2. Create workflow `.github/workflows/publish.yml`

```yaml
name: Publish to GitHub Packages

on:
  push:
    tags:
      - 'v*'

env:
  JAVA_VERSION: '21'

jobs:
  publish:
    name: Publish
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Cache Maven packages
        uses: actions/cache@v4
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Publish to GitHub Packages
        run: mvn deploy -B -DskipTests
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### 3. Publish first version

```bash
git tag v1.0.0.3
git push origin v1.0.0.3
```

Workflow triggers automatically on `v*` tag push.

## Notes

- Versions in pom.xml use `-SNAPSHOT` suffix during development
- Publish scripts automatically strip `-SNAPSHOT` before GitHub publication
- GitHub token with `read:packages` and `write:packages` permissions is required
- Consumer projects need `read:packages` token in `~/.m2/settings.xml`
