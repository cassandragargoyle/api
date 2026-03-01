Průvodce implementací issue - od přečtení zadání přes vytvoření branch, implementaci kódu, commit až po přípravu PR popisu.

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

## KROK 6: Push a PR (volitelný)

Pokud má issue propojený GitHub issue:

1. Push branch na Gitea: `git push origin <branch>`
2. Nabídni přípravu pro GitHub PR přes publish skripty (`scripts/github_02_sync_publish.py`)
3. Připrav PR popis ve formátu:
   - Název: stručný popis změny
   - Tělo: summary, test plan, odkaz na GitHub issue (`Closes #N`)

Pokud issue nemá GitHub propojení, stačí push na Gitea.

---

## Další kroky

Po dokončení implementace:

- Merge branch do main: `git checkout main && git merge <branch>`
- Smazání feature branch: `git branch -d <branch>`
- Aktualizuj stav issue v `docs/issues/internal/` na `Status: Implemented`
- Aktualizuj tabulku v `docs/issues/README.md` na ✅ Implemented
