Dokončení práce na feature branch - merge do hlavní větve, úklid branch a volitelná aktualizace stavu issue.

**DŮLEŽITÉ: NIKDY nepřidávej "Co-Authored-By: Claude <noreply@anthropic.com>" do kódových souborů ani git commitů.**

## KROK 1: Zjisti kontext

Zeptej se mě na:

1. Název feature branch (nebo použij aktuální branch)
2. Cílová větev pro merge (výchozí: main)

Ověř, že branch existuje a obsahuje commity.

## KROK 2: Merge a úklid

> **Co je PR (Pull Request)?** Žádost o začlenění změn z feature branch do hlavní větve. Používá se pro code review v týmech. Pro solo vývoj lze mergovat přímo.

Zeptej se uživatele na preferovaný postup:

### Varianta A: Přímý merge (výchozí pro solo vývoj)

```bash
git checkout main
git merge feature/<issue-num>-<popis>
git branch -d feature/<issue-num>-<popis>
```

**STOP** - Počkej na potvrzení před provedením merge.

### Varianta B: Pull Request na GitHub

Pokud má issue propojený GitHub issue:

1. Push branch na Gitea: `git push origin <branch>`
2. Připrav staging pro GitHub přes publish skripty (`scripts/github_02_sync_publish.py`)
3. Připrav PR popis:
   - **Summary**: Co tento PR dělá
   - **Motivation**: Proč (odkaz na GitHub issue: `Closes #N`)
   - **Changes**: Seznam změn
   - **Testing**: Kroky pro ověření

## KROK 3: Aktualizace issue (volitelné)

Zeptej se uživatele: "Je tímto issue kompletně dokončeno?"

### Pokud ANO:

Uprav soubor issue v `docs/issues/internal/`:

- Změň `**Status:** New` na `**Status:** Implemented`
- Přidej datum uzavření

Aktualizuj také `docs/issues/README.md` - změň status v tabulce na ✅ Implemented.

Commitni změnu stavu issue:

```text
docs(#<issue-num>): close issue - implementation complete
```

### Pokud NE:

Přeskoč aktualizaci stavu. Issue zůstává otevřené pro další práci.

## KROK 4: Shrnutí

Zobraz shrnutí:

- Merge provedený do větve: `<název>`
- Feature branch smazána: ano/ne
- Issue status aktualizován: ano/ne/přeskočeno
- Další kroky (pokud jsou)
