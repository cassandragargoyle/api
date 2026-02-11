# Issue #001: Github packages

**Type**: Feature
**Priority**: Medium
**Status**: New
**Created**: 2026-02-11
**Labels**: 
**Related**: 
**Repository**: Api

## Úvodní problém

Pokud máte 3 samostatné produkty (A/B/C), 3 repozitáře a 3 týmy, tak kopírování API modulu mezi repozitáři je skoro jistota, že si časem vyrobíte „3 podobné, ale jiné“ verze a peklo s kompatibilitou.

Nedělejte kopie (prakticky nikdy)

Varianta „repo s API a do A/B/C dělat kopii“ má typické následky:

- drifty verzí (A má opravu, B ji nemá, C má jinou)
- těžké backporty
- review/ownership chaos („kde je pravda?“)
- bezpečnostní opravy se šíří pomalu
- buildy nejsou reprodukovatelné, protože to je „někde zkopírované“.

Tohle se hodí jen na jednorázový fork, když se týmy vědomě rozcházejí.

Doporučený návrh: samostatná knihovna + verzování

1) Udělejte samostatný repozitář pro API modul (např. company-shared-api / platform-api)

Maven artefakt: třeba cz.firma.platform:shared-api

Releaseujte jako normální knihovnu (JAR) do interního Maven repa

Nexus / Artifactory / GitHub Packages / GitLab Package Registry / případně i Gitea balíčky, pokud to používáte.

2) A/B/C si to přidají jako dependency

Každý produkt si pinne verzi:

- stabilní týmům: 1.7.3
- nebo přes BOM / dependencyManagement (viz níže)

3) Nastavte pravidla kompatibility

Když „API“ opravdu znamená API pro ostatní, musí být stabilní:

- **semver (MAJOR breaking changes, MINOR backward-compatible, PATCH bugfix)**
- deprecations místo okamžitého mazání
- Ať je jasné, co je veřejné API vs. interní pomocné věci.

Klíčový detail: co přesně je „API modul“?

Podle příkladu („funkce pro logování“) to často není „API“, ale shared runtime knihovna / platform lib.

## Co do shared modulu dává smysl

- společné kontrakty (DTO, eventy, SPI rozhraní)
- společné util funkce, které jsou fakt stabilní a malé
- společné error kódy, konstanty, validace (opatrně)
- „starter“ konfigurace (pokud jste Spring svět) – ale to už je jiná vrstva

## Co tam nedávat (nebo velmi opatrně)

- „framework“ věci, které vám zamknou všechny 3 produkty na stejný stack/verzi
- těžké závislosti, které pak tahají konflikty do A/B/C

Jak to udělat organizačně, aby to fungovalo mezi týmy

- CODEOWNERS / ownership: jeden tým vlastní shared modul, ostatní přispívají PR.

CI pravidlo: žádný merge bez toho, že projde build + testy.

Release proces: verzování + changelog + release notes (klidně automat).

Consumer-driven contract tests (pokročilé, ale super): A/B/C mají testy proti API, aby se breaking změna chytla hned.

Alternativy (kdy dávají smysl)

1) Git submodule / subtree

Použitelné, ale:

- pořád řešíte verze a update ručně
- je to méně příjemné než Maven dependency

Dává smysl, když nemáte interní Maven repo a nechcete ho.

2) Monorepo

U vás to z popisu nevypadá vhodně (3 týmy, 3 produkty, samostatné repo je záměr). Monorepo by vám přineslo centralizaci, ale i tření.

Minimum, které bych zavedl hned:

- shared-* repozitář jako Maven knihovna
- interní Maven registry
- semver + changelog

držet to malé: „sdílej jen to, co se opravdu vyplatí sdílet“

## Jak funguje GitHub Packages

GitHub Packages funguje jako Maven repozitář a normálně ho přidáš do pom.xml. Produkty A/B/C pak mohou záviset na tvém shared-api stejně jako na jakékoli jiné knihovně.

Níže máš praktické vysvětlení + minimální konfiguraci.

### Jak funguje GitHub Packages (pro Maven)

GitHub Packages je hostovaný artefaktový repozitář (podobně jako Nexus/Artifactory), kde publikuješ:

- JAR knihovny
- SNAPSHOT i RELEASE verze
- Maven metadata

Každý balík je vázán na:

- owner / repository

Např:

com.company.platform:shared-api:1.2.0
→ github.com/company/shared-api

1) Publikování knihovny (shared-api)
pom.xml knihovny

```xml
<groupId>cz.company.platform</groupId>
<artifactId>shared-api</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>

<distributionManagement>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/OWNER/REPOSITORY</url>
    </repository>
</distributionManagement>
```

Autentizace (nutné)

Do ~/.m2/settings.xml:

```xml
<servers>
  <server>
    <id>github</id>
    <username>GITHUB_USERNAME</username>
    <password>GITHUB_TOKEN</password>
  </server>
</servers>
```

Token musí mít:

read:packages
write:packages
repo   (pro private repo)

Deploy
mvn deploy

Artefakt se objeví v:

GitHub → Repository → Packages

2) Použití v A / B / C

Ano — dá se normálně do pom.xml.

Přidání repozitáře

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/OWNER/REPOSITORY</url>
    </repository>
</repositories>

Dependency
<dependency>
    <groupId>cz.company.platform</groupId>
    <artifactId>shared-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

Autentizace i pro čtení

I konzumenti (A/B/C) musí mít v settings.xml:

```xml
<server>
  <id>github</id>
  <username>GITHUB_USERNAME</username>
  <password>GITHUB_TOKEN</password>
</server>
```

Stačí token s:

read:packages

Doporučená architektura pro tvůj případ

Pro 3 produkty + shared modul:

Repozitáře
shared-api        → Maven knihovna (GitHub Packages)
product-A
product-B
product-C

Volitelné (lepší)
shared-bom        → řízení verzí všech shared modulů
shared-logging
shared-test

Pak v A/B/C:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>cz.company.platform</groupId>
      <artifactId>shared-bom</artifactId>
      <version>1.0.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

A už nepíšeš verze u každé shared knihovny.

Výhody GitHub Packages

- Jednoduché (žádný Nexus server)
- Napojené na CI/CD (GitHub Actions → mvn deploy)
- Verzování artefaktů
- Access control
- Audit + historie

Nevýhody

- Pomalejší než lokální Nexus
- Auth token nutný i pro read
- Horší práce se SNAPSHOTy než Nexus

„best practice“ návrh

- strukturu shared-* modulů
- semver + release workflow
- GitHub Actions pro auto publish
- BOM řízení verzí
- jak zabránit breaking změnám mezi týmy
- jak udělat „platform layer“ (logging, config, metrics, events)
- private nebo public repo?
- Spring nebo plain Java?

Další dotčené oblasti GitHub Actions / Gitea / jiný CI ?
