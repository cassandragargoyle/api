# CassandraGargoyle API

Multi-jazyková sdílená knihovna (Java, Python, Go, TypeScript) poskytující entity, utility a základní abstrakce pro projekty CassandraGargoyle.

> **Pozn.:** Tento dokument je překladem [README.md](README.md). Při rozporu má přednost anglická verze (oficiální jazyk projektu).

## Dokumentace

Vývojové pokyny najdete v [docs/contributing/](docs/contributing/).

## Změny (Changelog)

Historii verzí najdete v [CHANGELOG.md](CHANGELOG.md).

## Informace o projektu

- **Verze:** 1.0.0.9-SNAPSHOT
- **Java:** 21 (Maven, NetBeans Platform)
- **Python:** ≥ 3.11 (spravováno přes uv)
- **Go:** ≥ 1.22
- **Repozitář**: <https://github.com/cassandragargoyle/api>

## Struktura projektu

```text
Api/
├── java/          # Hlavní API modul (Java, Maven)
├── python/        # Sdílené API moduly (Python)
├── go/            # Sdílené API moduly (Go)
├── typescript/    # Sdílené API moduly (TypeScript / React, npm) — vyžaduje Node 18+
└── contract/      # Jazykově nezávislé task kontrakty (JSON Schema)
```

## Hlavní funkce

### Správa entit

- Základní abstrakce a implementace entit
- Správa verzí
- Platformně specifické zacházení s entitami
- Entity Diagram, Node a Edge pro grafové struktury
- Data container entity pro správu dat

### Správa software

- Definice software entit
- Podpora programovacích jazyků
- Detekce typu operačního systému
- Kategorizace software a jeho funkcí
- Zacházení s kompatibilitou napříč platformami

### Utility

- **Date Utilities:** manipulace s datumy a formátování
- **String Utilities:** zpracování řetězců a manipulace s nimi
- **System Utilities:** systémové operace
- **Preferences Utilities:** správa uživatelských preferencí
- **Base64 Encoding:** kódování dat
- **OS Detection:** detekce a identifikace operačního systému

### Fuzzy porovnávání řetězců

- **Levenshteinova vzdálenost** s odvozenou normalizovanou podobností v `[0.0, 1.0]`
- Pracuje nad Unicode code points / runy (korektní pro znaky s diakritikou,
  CJK i emoji)
- K dispozici ve všech třech jazykových modulech
  (`org.cassandragargoyle.api.fuzzy`, `cassandragargoyle.api.fuzzy`,
  `github.com/cassandragargoyle/api/go/fuzzy`)

### Telemetrie (OpenTelemetry)

- **TelemetryProvider** utility class pro centralizovanou konfiguraci tracingu
- Tři režimy provozu: **NoOp** (výchozí, nulová režie), **Console** (vývoj),
  **OTLP** (produkce)
- Builder pattern s možností přepisu přes proměnné prostředí
  (`OTEL_EXPORTER_OTLP_ENDPOINT`, `OTEL_SERVICE_NAME`)
- W3C TraceContext propagace pro distribuované systémy
- Trace logging na CLI přes `enableTraceLogging()`

### Logování

- Vlastní logovací framework
- Implementace log factory patternu
- Zpracování výjimek během logování
- Identické logovací API napříč Java, Python a TypeScript
  (`org.cassandragargoyle.api.log`, `cassandragargoyle.api.log`,
  `@cassandragargoyle/api/log`) — stejné jméno kořenového loggeru, výstupní
  formát (`dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message`) i krátké
  aliasy úrovní
- TypeScript modul je isomorfní: konzolový transport pro prohlížeč / React
  webview a volitelný souborový transport pouze pro Node

### Podpora CLI

- Implementace command-line interface
- Integrace s Apache Commons CLI
- Integrace řízení trace výstupu

## Závislosti

### Java

- **NetBeans Platform Modules** — `org-openide-util`, `org-openide-util-ui`,
  `org-openide-util-lookup`, `org-openide-filesystems`, `org-openide-modules`
- **Spring Framework** (5.3.39) — DI a správa komponent
- **Apache Commons** — `commons-cli` (1.11.0), `commons-lang3` (3.20.0),
  `commons-text` (1.15.0)
- **OpenTelemetry** (1.40.0) — distribuovaný tracing
- **JUnit Jupiter** (6.0.3) — testování

### Python

- **OpenTelemetry** (≥ 1.25) — API + SDK + OTLP gRPC/HTTP exportéry
- **pytest** (≥ 7.0), **ruff** (≥ 0.15) — testování a linting (dev extras)

### Go

- Pouze standardní knihovna (žádné externí závislosti pro současný balíček
  `fuzzy`)

## Build projektu

```bash
make build            # všechny artefakty (Java JAR + Python wheel/sdist + TypeScript dist)
make build-java       # pouze JAR (přeskočí testy)
make build-python     # pouze wheel + sdist
make build-typescript # pouze TypeScript ESM + CJS + .d.ts
```

Nebo přímo pro jednotlivé jazyky:

```bash
mvn clean install -f java/pom.xml      # Java do ~/.m2
uv --project python build              # Python wheel/sdist do python/dist/
cd go && go build ./...                # Go (jen stdlib, žádný install krok)
npm --prefix typescript run build      # TypeScript do typescript/dist/
```

## Spuštění testů

```bash
make test            # všechny jazykové moduly (Java + Python + Go + TypeScript)
make test-java       # pouze Java (mvn test)
make test-python     # pouze Python (pytest)
make test-go         # pouze Go (go test ./...)
make test-typescript # pouze TypeScript (Vitest)
```

Pro přehled všech dostupných cílů spusťte `make help`.

## Vývoj

Tento projekt používá:

- **Java**: Maven pro správu buildu, Spring pro DI, NetBeans Platform jako
  framework aplikace
- **Python**: uv pro venv a správu závislostí, pytest pro testování, ruff pro
  linting
- **Go**: Go moduly (`go.mod`) pro build a správu závislostí,
  `go test ./...` pro testování, `go vet` pro statickou analýzu a `gofmt` pro
  formátování; modul leží v `go/` s import path
  `github.com/cassandragargoyle/api/go` (release tagy používají prefix `go/`
  podle [pravidla pro submoduly](https://go.dev/ref/mod#vcs-version),
  např. `go/v1.0.0.8`)
- **TypeScript**: npm pro správu závislostí, `tsup` pro duální ESM/CJS build
  s emitovanými `.d.ts`, Vitest pro testování, ESLint + Prettier pro linting;
  balíček leží v `typescript/` jako `@cassandragargoyle/api` (subpath export
  `@cassandragargoyle/api/log`) a vyžaduje Node 18+

## Licence

Tento projekt je licencován pod MIT licencí. Detaily viz soubor [LICENSE](LICENSE).

Software je vyvíjen a udržován komunitou CassandraGargoyle.

## Přispívání

Pokyny pro vývoj a přispívání najdete v [docs/contributing/](docs/contributing/).
