# @cassandragargoyle/api

Sdílené TypeScript moduly pro projekty CassandraGargoyle. Balíček je TypeScript část
vícejazykové knihovny [CassandraGargoyle API](https://github.com/cassandragargoyle/api) a chová se
stejně jako moduly v Javě, Pythonu a Go.

> **Pozn.:** Tento dokument je překladem [README.md](README.md). Při rozporu má přednost anglická
> verze (oficiální jazyk projektu).

| Import                               | Obsah                                                                             |
| ------------------------------------ | --------------------------------------------------------------------------------- |
| `@cassandragargoyle/api/log`         | Logování se stejným formátem jako moduly v Javě a Pythonu                         |
| `@cassandragargoyle/api/preferences` | Uživatelské preference s úložištěm v paměti, v `localStorage` nebo v JSON souboru |
| `@cassandragargoyle/api`             | Oba moduly jako jmenné prostory `log` a `preferences`                             |

Balíček má ESM a CommonJS build s deklaracemi typů a vyžaduje Node 22 nebo novější. Moduly fungují
i v prohlížeči; kód pro práci se soubory, který potřebuje Node, se načte jen tehdy, když se použije.

## Instalace

```bash
npm install @cassandragargoyle/api
```

## Použití

```ts
import { LogFactory, Logging } from "@cassandragargoyle/api/log";

Logging.initialize("INFO");
const log = LogFactory.getLogger("com.example.Svc");
log.info("Starting {0}", "job-7");
```

Dokumentace modulů:

- [log](https://github.com/cassandragargoyle/api/blob/main/typescript/src/log/README.cs.md)
- [preferences](https://github.com/cassandragargoyle/api/blob/main/typescript/src/preferences/README.md)
  (jen anglicky)

## Verze

Balíček používá semver a má stejnou verzi jako moduly projektu v Javě, Pythonu a Go.

## Licence

MIT, viz [LICENSE](https://github.com/cassandragargoyle/api/blob/main/LICENSE).
