---
name: finish-branch
description: Dokončení práce na feature branch - merge do hlavní větve, úklid branch a volitelná aktualizace stavu issue. Použij, když chce uživatel dokončit feature branch.
disable-model-invocation: true
---

# Dokončení feature branch

**DŮLEŽITÉ: NIKDY nepřidávej "Co-Authored-By: Claude <noreply@anthropic.com>" do kódových souborů ani git commitů.**

## KROK 1: Zjisti kontext

Zeptej se mě na:

1. Název feature branch (nebo použij aktuální branch)
2. Cílová větev pro merge (výchozí: main)

Ověř, že branch existuje a obsahuje commity.

## KROK 2: Merge a úklid

> **Co je PR (Pull Request)?** Žádost o začlenění změn z feature branch do hlavní větve. Změny do `main` se v tomto repozitáři začleňují přes PR na GitHubu.

> **Remotes:** `origin` = GitHub (`cassandragargoyle/api`), `gitea` = Gitea. Před pushem ověř `git remote -v`.

Zeptej se uživatele na preferovaný postup:

### Varianta A: Pull Request na GitHub (výchozí)

1. Push branch na GitHub: `git push -u origin <branch>`
2. Připrav PR popis:
   - **Summary**: Co tento PR dělá
   - **Motivation**: Proč (odkaz na GitHub issue: `Closes #N`)
   - **Changes**: Seznam změn
   - **Testing**: Kroky pro ověření
3. Vytvoř PR: `gh pr create --base main --head <branch> --title "<commit title>" --body "<popis>"`

**STOP** - Počkej, až uživatel PR zpracuje na webu.

Po merge PR ověř stav (`gh pr view <N> --json state`) a ukliď:

```bash
git checkout main
git pull --ff-only
git branch -d <branch>
git push origin --delete <branch>   # pokud ji GitHub nesmazal automaticky
git fetch --prune origin
```

### Varianta B: Přímý lokální merge (jen na výslovné přání)

Obchází PR review. Použij jen, když to uživatel výslovně chce.

```bash
git checkout main
git merge <branch>
git branch -d <branch>
```

**STOP** - Počkej na potvrzení před provedením merge.

Rollback před pushem: `git branch <branch> <commit>` a `git reset --hard origin/main`.

## KROK 3: Aktualizace issue (volitelné)

Zeptej se uživatele: "Je tímto issue kompletně dokončeno?"

### Pokud ANO:

1. Uprav soubor issue v `docs/issues/`:
   - Změň `**Status**: Open` na `**Status**: Implemented`
   - Přidej datum uzavření

2. Archivuj soubor do `done/` (zachová git historii):

   ```bash
   git mv docs/issues/<NNN>-<name>.md docs/issues/done/<NNN>-<name>.md
   ```

3. Uzavři GitHub issue, pokud ho neuzavřel PR přes `Closes #N` (seznam issues
   je na GitHubu — žádné README tabulky se needitují):

   ```bash
   gh issue close <N> --repo cassandragargoyle/api --comment "Implemented in <commit/version>."
   ```

4. Commitni změnu stavu společně s přesunem:

   ```text
   docs(#<issue-num>): archive issue - implementation complete
   ```

### Pokud NE:

Přeskoč aktualizaci stavu. Issue zůstává otevřené pro další práci.

## KROK 4: Shrnutí

Zobraz shrnutí:

- Způsob: PR #N / přímý merge do `<název>`
- Feature branch smazána: ano/ne
- Issue status aktualizován: ano/ne/přeskočeno
- Další kroky (pokud jsou)
