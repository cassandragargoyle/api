---
name: create-manifest
description: Vytvoří software manifest podle metodiky projektu. Použij, když chce uživatel vytvořit manifest pro software, nástroj nebo technologii.
---

# Vytvoření software manifestu

Tvým úkolem je vytvořit software manifest pro zadanou položku (název software, nástroje nebo technologie).

## KROK 1: Zjisti položku

Pokud uživatel nezadal položku, zeptej se česky:

> Pro jaký software, nástroj nebo technologii chceš vytvořit manifest?

Počkej na odpověď uživatele a poté pokračuj.

## KROK 2: Nastuduj metodiku

Přečti si `docs/manifests/README.md` pro pochopení struktury a konvencí manifestů.

## KROK 3: Prozkoumej existující manifesty

Podívej se na existující manifesty v `docs/manifests/` pro inspiraci formátem a obsahem (např. `manifest-docker.md`, `manifest-go.md`).

## KROK 4: Vyhledej informace

Použij web search pro získání aktuálních informací o:

- Aktuální verze a release date
- Oficiální dokumentace a website
- Licenční podmínky
- System requirements
- Hlavní use cases

## KROK 5: Vytvoř manifest

Vytvoř soubor `docs/manifests/manifest-{název}.md` obsahující:

- Basic Information (name, category, license, version, maintainer, investment type)
- Installation Details
- Platform Support
- Dependencies
- Portunix Integration
- Verification commands
- Best Use Cases (✅ doporučené, ⚠️ nedoporučené)
- Community & Metrics
- Learning Resources

## KROK 6: Aktualizuj README

Přidej nový manifest do seznamu v `docs/manifests/README.md` na správné místo podle kategorie.

## Konvence

- Použij ikony podle typu investice: 📈 (veřejně obchodovaná), 🔒 (privátní), 🆓 (open-source)
- Použij ✅ pro doporučené use cases a ⚠️ pro případy kdy zvážit alternativy
- Dodržuj anglický jazyk v obsahu manifestu (projekt má anglickou dokumentaci)
- Název souboru: `manifest-{kebab-case-název}.md`
