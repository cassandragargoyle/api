---
description: Přeloží dokument do češtiny podle metodiky projektu
argument-hint: [cesta k souboru]
---

## Instrukce

Tvým úkolem je přeložit dokument do češtiny: **$ARGUMENTS**

### Metodika

Před překladem si nastuduj projektovou metodiku překladu v `docs/contributing/TRANSLATION-WORKFLOW.md`.

### Pokud není zadán soubor

Pokud není zadána cesta k souboru (argument je prázdný), zeptej se uživatele česky:

> Který dokument chceš přeložit do češtiny? Zadej cestu k souboru.

Počkej na odpověď uživatele a poté pokračuj.

### Postup překladu

1. **Ověř existenci souboru**: Zkontroluj, že zdrojový soubor existuje

2. **Přečti zdrojový dokument**: Načti obsah souboru k překladu

3. **Urči cílovou cestu**: Přeložený soubor ulož do `.translated/cs/` se zachováním původní adresářové struktury

   Příklady:
   - `docs/manifests/manifest-docker.md` → `.translated/cs/docs/manifests/manifest-docker.md`
   - `docs/learning-paths/scraping/technical-challenges.md` → `.translated/cs/docs/learning-paths/scraping/technical-challenges.md`
   - `README.md` → `.translated/cs/README.md`

4. **Vytvoř adresářovou strukturu**: Pokud cílový adresář neexistuje, vytvoř ho

5. **Přelož dokument**: Přelož obsah do češtiny s dodržením pravidel níže

6. **Ulož překlad**: Zapiš přeložený dokument na cílovou cestu

### Pravidla překladu

#### Co překládat

- Nadpisy a text
- Popisy v tabulkách
- Komentáře v kódu (pokud jsou vysvětlující)
- Alt texty a popisy

#### Co NEPŘEKLÁDAT

- Názvy souborů a cest
- Názvy funkcí, proměnných, tříd
- Příkazy a kód (kromě komentářů)
- URL adresy
- Názvy nástrojů a produktů (Docker, Kubernetes, Bright Data...)
- Technické termíny bez ustáleného českého ekvivalentu

#### Styl překladu

- Používej formální, ale srozumitelný jazyk
- Zachovej původní formátování (markdown, tabulky, seznamy)
- Technické termíny s ustáleným českým překladem používej česky (např. "databáze", "server", "soubor")
- U nejednoznačných termínů uveď anglický originál v závorce při prvním výskytu
- Zachovej ASCII diagramy beze změny (pouze přelož případné popisky)

#### Metadata překladu

Na konec přeloženého dokumentu přidej:

```markdown
---

**Překlad z**: `{původní_cesta}`
**Datum překladu**: {aktuální datum}
```

### Příklad

Zdrojový text:

```markdown
## Technical Obstacles

### Dynamic Content and JavaScript

Many modern websites load content using JavaScript.
```

Překlad:

```markdown
## Technické překážky

### Dynamický obsah a JavaScript

Mnoho moderních webových stránek načítá obsah pomocí JavaScriptu.
```

### Po dokončení

Informuj uživatele česky:

- Kde je uložen přeložený soubor
- Stručné shrnutí co bylo přeloženo
