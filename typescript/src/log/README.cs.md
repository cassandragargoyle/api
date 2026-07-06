# `@cassandragargoyle/api/log`

Logovací infrastruktura sdílená TypeScript / React moduly CassandraGargoyle.
Zrcadlí veřejné API Java balíčku
[`org.cassandragargoyle.api.log`](../../../java/src/main/java/org/cassandragargoyle/api/log/README.cs.md)
a jeho Python parity modulu
[`cassandragargoyle.api.log`](../../../python/src/cassandragargoyle/api/log) —
stejné jméno kořenového loggeru, stejný výstupní formát, stejné krátké aliasy
úrovní — takže výstup z Java, Python a TypeScript komponent vypadá identicky a
je zpracovatelný stejnými nástroji.

Modul je **isomorfní**: konzolový transport funguje v prohlížeči / React webview
(bez souborového systému) a souborový transport pouze pro Node se načítá líně na
vyžádání, takže bundly pro prohlížeč souborový kód tree-shakingem odstraní.

> **Pozn.:** Tento dokument je překladem
> [README.md](README.md). Při rozporu má přednost anglická verze
> (oficiální jazyk projektu).

## Veřejné API

| Export                      | Druh      | Účel                                                                        |
| --------------------------- | --------- | --------------------------------------------------------------------------- |
| `LogFactory`                | class     | Vytváří instance `Logger` napojené na jméno                                 |
| `Logger`                    | interface | Metody `trace` / `debug` / `info` / `warn` / `error` se zástupci `{0}`      |
| `Logging`                   | class     | Inicializuje transporty (konzole + volitelně Node soubor), nastavuje úrovně |
| `LogTransport`              | interface | Cíl pro naformátované logové řádky; lze dodat vlastní cíle                  |
| `markLogSkip` / `isLogSkip` | funkce    | Označí / detekuje chyby k potlačení z logu (TS obdoba `LogSkipException`)   |

## Instalace

Ve verzi v1 se balíček konzumuje přes lokální pack / file dependency (bez registru):

```bash
cd typescript
npm install
npm run build       # vytvoří dist/ (ESM + CJS + .d.ts)
npm pack            # vytvoří cassandragargoyle-api-<version>.tgz
```

Poté v konzumentovi (např. portunix-vscode `src/pilot/`):

```bash
npm install /cesta/k/cassandragargoyle-api-<version>.tgz
```

## Typické použití

### Vytvoření loggeru

```ts
import { LogFactory } from "@cassandragargoyle/api/log";

const log = LogFactory.getLogger("com.example.Svc");

log.info("Starting {0}", "job-7");
log.error("Failed {0}: {1}", err, "job-7", "timeout");
```

`message` používá stejnou konvenci pozičních zástupců `{0}`, `{1}` jako Java
volání `Logger.log(level, msg, args)`, takže logové řetězce lze mezi runtimy
kopírovat beze změny.

### Inicializace logování při startu

```ts
import { Logging } from "@cassandragargoyle/api/log";

// Prohlížeč / webview: pouze konzole (soubor je dokumentovaný no-op).
Logging.initialize("INFO");

// Node / hostitel VS Code rozšíření: konzole + rotující per-PID soubor.
Logging.initialize("INFO", { console: true, file: true });
```

Po `initialize` tečou logové záznamy:

- **Konzole** (prohlížeč + Node) — směrováno podle úrovně na
  `console.debug/info/warn/error`.
- **Soubor** (pouze Node) — `~/.cassandragargoyle/var/log/messages.<PID>.log`,
  se dvěma rotovanými kopiemi (`.1`, `.2`); soubory starší než 10 dní jsou při
  startu smazány. V prohlížeči je to dokumentovaný no-op. Souborový transport se
  načítá asynchronně; `await Logging.ready()` se vyřeší, jakmile je připojen.

### Aliasy úrovní

`Logging.setLogLevel(level)` přijímá krátké aliasy identické s Java/Python:

| Alias               | Mapuje na |
| ------------------- | --------- |
| `TRACE`, `T`, `4`   | trace     |
| `DEBUG`, `D`, `3`   | debug     |
| `INFO`, `I`, `2`    | info      |
| `WARNING`, `W`, `1` | warn      |
| `ERROR`, `E`, `0`   | error     |

### Potlačení nevýznamných výjimek

Pokud je nějaká chyba třetí strany očekávaná a nezajímavá, označ ji přes
`markLogSkip`; výchozí filtr odpovídající záznamy zahodí dřív, než dorazí na
jakýkoli transport — TS obdoba marker rozhraní Java `LogSkipException`.

```ts
import { markLogSkip } from "@cassandragargoyle/api/log";

log.error("harmless boot warning", markLogSkip(err));
```

## Výstupní formát

```text
dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message
```

Víceřádkové zprávy a stack trace jsou připojovány doslovně. Pozor: metoda
`warn()` vypisuje token úrovně `WARNING`, aby přesně odpovídala Java/Python
byte po bytu.

## Související

- Java log balíček: [`org.cassandragargoyle.api.log`](../../../java/src/main/java/org/cassandragargoyle/api/log/README.cs.md)
- Python parity modul: [`cassandragargoyle.api.log`](../../../python/src/cassandragargoyle/api/log)
- První konzument: [portunix-vscode](https://github.com/CassandraGargoyle/portunix-vscode)
  Pilot UI (`src/pilot/`), VS Code rozšíření (`src/vscode-extension/`)
