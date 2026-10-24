# @cassandragargoyle/api

Shared TypeScript modules for CassandraGargoyle projects. The package is the TypeScript part of
the multi-language [CassandraGargoyle API](https://github.com/cassandragargoyle/api), with the
same behavior as the Java, Python, and Go modules.

| Import                               | Content                                                                |
| ------------------------------------ | ---------------------------------------------------------------------- |
| `@cassandragargoyle/api/log`         | Logging with the same format as the Java and Python modules            |
| `@cassandragargoyle/api/preferences` | User preferences with an in-memory, `localStorage`, or JSON file store |
| `@cassandragargoyle/api`             | Both modules as the namespaces `log` and `preferences`                 |

The package has ESM and CommonJS builds with type declarations and requires Node 22 or newer.
The modules also work in the browser; the Node-only file code is loaded only when it is used.

## Installation

```bash
npm install @cassandragargoyle/api
```

## Usage

```ts
import { LogFactory, Logging } from "@cassandragargoyle/api/log";

Logging.initialize("INFO");
const log = LogFactory.getLogger("com.example.Svc");
log.info("Starting {0}", "job-7");
```

Documentation of the modules:

- [log](https://github.com/cassandragargoyle/api/blob/main/typescript/src/log/README.md)
- [preferences](https://github.com/cassandragargoyle/api/blob/main/typescript/src/preferences/README.md)

## Version

The project uses the four-part version `MAJOR.MINOR.PATCH.BUILD` for all modules. npm accepts
only semver, so the npm version is `MAJOR.MINOR.BUILD`: project version `1.0.0.9` is
`1.0.9` on npm.

## License

MIT, see [LICENSE](https://github.com/cassandragargoyle/api/blob/main/LICENSE).
