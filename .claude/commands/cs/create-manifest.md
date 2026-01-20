---
description: Vytvoří software manifest podle metodiky projektu
argument-hint: [název položky]
---

## Instrukce

Tvým úkolem je vytvořit software manifest pro položku: **$ARGUMENTS**

### Pokud není zadána položka

Pokud není zadána žádná položka (argument je prázdný), zeptej se uživatele česky:

> Pro jaký software, nástroj nebo technologii chceš vytvořit manifest?

Počkej na odpověď uživatele a poté pokračuj.

### Postup vytvoření manifestu

1. **Nastuduj metodiku**: Přečti si `docs/manifests/README.md` pro pochopení struktury a konvencí manifestů

2. **Prozkoumej existující manifesty**: Podívej se na existující manifesty v `docs/manifests/` pro inspiraci formátem a obsahem (např. `manifest-docker.md`, `manifest-go.md`)

3. **Vyhledej informace**: Použij web search pro získání aktuálních informací o:
   - Aktuální verze a release date
   - Oficiální dokumentace a website
   - Licenční podmínky
   - System requirements
   - Hlavní use cases

4. **Vytvoř manifest**: Vytvoř soubor `docs/manifests/manifest-{název}.md` obsahující:
   - Basic Information (name, category, license, version, maintainer, investment type)
   - Installation Details
   - Platform Support
   - Dependencies
   - Portunix Integration
   - Verification commands
   - Best Use Cases (✅ doporučené, ⚠️ nedoporučené)
   - Community & Metrics
   - Learning Resources

5. **Aktualizuj README**: Přidej nový manifest do seznamu v `docs/manifests/README.md` na správné místo podle kategorie

### Konvence

- Použij ikony podle typu investice: 📈 (veřejně obchodovaná), 🔒 (privátní), 🆓 (open-source)
- Použij ✅ pro doporučené use cases a ⚠️ pro případy kdy zvážit alternativy
- Dodržuj anglický jazyk v obsahu manifestu (projekt má anglickou dokumentaci)
- Název souboru: `manifest-{kebab-case-název}.md`
