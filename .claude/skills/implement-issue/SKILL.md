---
name: implement-issue
description: Průvodce implementací issue - od přečtení zadání přes vytvoření branch, implementaci kódu, commit až po přípravu PR popisu. Použij, když chce uživatel implementovat interní issue (např. #002).
---

# Implementace issue

**DŮLEŽITÉ: NIKDY nepřidávej "Co-Authored-By: Claude <noreply@anthropic.com>" do kódových souborů ani git commitů.**

## KROK 0: Kontrola role

Zkontroluj aktuální roli v `CLAUDE.local.md`. Pokud role není **Developer**:

1. Informuj uživatele, že implementace vyžaduje roli Developer
2. Nabídni přepnutí role příkazem `/role developer`
3. **STOP** - Nepokračuj dokud není role Developer aktivní

## KROK 1: Získej issue

Zeptej se mě na číslo interního issue (např. #002). Počkej na mou odpověď.

## KROK 2: Potvrzení porozumění

Poté, co ti poskytnu issue:

1. Přečti soubor issue z `docs/issues/internal/` (formát: `NNN-popis.md`)
2. Přeformuluj issue vlastními slovy
3. Vysvětli potřebné změny v kódu
4. Pokud má issue propojený GitHub issue, zmíň ho
5. Požádej mě o potvrzení

**STOP** - Počkej na mé výslovné potvrzení než budeš pokračovat.

## KROK 3: Vytvoř branch

Vytvoř git branch: `feature/<issue-num>-<kratky-popis>` nebo `fix/<issue-num>-<kratky-popis>`

- Použij malá písmena a pomlčky pro popis
- Issue číslo bez # prefixu
- Příklad: `feature/002-github-packages-publishing`
- Příklad: `fix/003-broken-entity-mapping`

Zobraz git příkaz před jeho spuštěním.

## KROK 4: Implementace

- Proveď všechny změny kódu v jedné souvislé fázi
- NEPTEJ se na otázky, pokud to není skutečně nejednoznačné
- Modifikuj pouze relevantní soubory
- Dodržuj coding style projektu (viz `docs/contributing/CODE-STYLE-JAVA.md`)
- Java 21, Allman brace style, taby pro odsazení

Na konci poskytni shrnutí:

- Modifikované soubory
- Přidané metody/třídy
- Odstraněný kód
- Klíčová rozhodnutí

Zeptej se mě, co chci:

- a) Přijmout tak jak je
- b) Upravit konkrétní části
- c) Předělat kompletně

**STOP** - Počkej na mé schválení než budeš pokračovat.

## KROK 5: Commit

Připrav commit message:

```text
<type>(#<issue-num>): <krátké shrnutí>

- klíčová změna 1
- klíčová změna 2
```

Povolené typy: `feat`, `fix`, `refactor`, `docs`, `test`, `chore`

**STOP** - Zeptej se mě na potvrzení commit message před commitnutím.

## KROK 6: Push a PR

`origin` je GitHub (`cassandragargoyle/api`). Číslo issue = číslo GitHub issue.

1. Push branch: `git push -u origin <branch>`
2. Otevři PR přes `gh` a propoj issue přes `Closes #N`:

   ```bash
   gh pr create --repo cassandragargoyle/api \
     --title "<type>(#N): <krátké shrnutí>" \
     --body "Summary …

   Test plan …

   Closes #N"
   ```
3. Alternativa bez PR: merge přímo do `main` po review (viz Další kroky).

---

## Další kroky

Po dokončení implementace:

- Sloučení: přes merge PR na GitHubu, nebo lokálně
  `git checkout main && git merge <branch> && git push origin main`
- Smazání feature branch: `git branch -d <branch>`
- Uzavři issue: `gh issue close N` (nebo automaticky přes `Closes #N` v PR)
- Archivuj detailní soubor:
  `git mv docs/issues/internal/N-*.md docs/issues/internal/done/`

Seznam issues je na GitHubu — **žádné přehledové tabulky se needitují.**
