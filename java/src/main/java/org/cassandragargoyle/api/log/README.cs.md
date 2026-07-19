# `org.cassandragargoyle.api.log`

Logovací infrastruktura sdílená všemi Java moduly CassandraGargoyle. Obaluje
`java.util.logging` (JUL) projektovým formátovačem, neblokujícím souborovým
handlerem a sadou pomocných tříd a anotací používaných při integraci se
souborem NetBeans `layer.xml`.

Python modul v adresáři
[`python/src/cassandragargoyle/api/log/`](../../../../../../../../python/src/cassandragargoyle/api/log)
zrcadlí veřejné API tohoto balíčku — stejné jméno kořenového loggeru, stejný
výstupní formát, stejné aliasy úrovní — takže výstup z Java a Python
komponent vypadá identicky a je zpracovatelný stejnými nástroji.

> **Pozn.:** Tento dokument je překladem
> [README.md](README.md). Při rozporu má přednost anglická verze
> (oficiální jazyk projektu).

## Veřejné API

| Typ | Druh | Účel |
| --- | --- | --- |
| [`LogFactory`](LogFactory.java) | utility class | Vytváří instance `Logger` napojené na resource bundle volající třídy a poskytuje pomocnou metodu `args(...)` pro parametrizovaná volání |
| [`Logging`](Logging.java) | utility class | Inicializuje JUL handlery (konzole + rotující per-PID soubor), nastavuje úrovně, dodává projektový formátovač a výchozí filtr |
| [`LogSkipException`](LogSkipException.java) | marker interface | Třídy výjimek označené tímto rozhraním filtr potlačí a do logu se nedostanou |
| `LogFactory.IgnoreLoggerForUI` | anotace (`@Target FIELD`) | Označuje pole loggerů, jejichž jména patří do `CassandraGargoyle/Log/IngoredLoggers/` v NetBeans layeru |
| `LogFactory.ForcedLogMessages` | anotace (`@Target TYPE / METHOD / CONSTRUCTOR`) | Vyjmenovává message id vynucené do uživatelského logu pod `CassandraGargoyle/Log/ForcedMessages/` |
| `LogFactory.ShownLogMessages` | anotace (`@Target TYPE / METHOD / CONSTRUCTOR`) | Vyjmenovává message id povýšená ze stavového řádku do hlavního logu pod `CassandraGargoyle/Log/ShownMessages/` |

Obě vnitřní anotace mají retenci pouze ve zdrojovém kódu
(`RetentionPolicy.SOURCE`) a čte je pouze NetBeans annotation processor pro
`layer.xml` — v runtime classpath nejsou.

## Typické použití

### Vytvoření loggeru

```java
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cassandragargoyle.api.log.LogFactory;

public class MyService
{
    private static final Logger LOG = LogFactory.getLogger(MyService.class);

    public void doSomething(String entityName)
    {
        LOG.log(Level.INFO, "Starting operation for {0}", entityName);
        LOG.log(Level.SEVERE, "Failed: {0}, cause: {1}", LogFactory.args(entityName, ex.getMessage()));
    }
}
```

`LogFactory.getLogger(MyService.class)` hledá `Bundle.properties` ve stejném
balíčku jako `MyService` a logger k němu připojí; pokud bundle neexistuje,
logger se vytvoří bez lokalizace. Jméno pole loggeru musí být `LOG` podle
Java code style projektu.

### Inicializace logování při startu

```java
import org.cassandragargoyle.api.log.Logging;

Logging.initialize("INFO", /* console */ true, /* file */ true);
```

Po `initialize` tečou logové záznamy:

- **Konzole** — formátováno přes `Logging.FORMATTER` (projektový `CustomFormatter`)
- **Soubor** — `~/.myapp/var/log/messages.<PID>.log`, se dvěma rotovanými
  kopiemi (`.1`, `.2`); soubory starší než 10 dní jsou při startu smazány.
  Handler je obalen v `NonCloseHandler` a zapisuje asynchronně přes
  ohraničenou frontu, aby logování nezdržovalo hlavní vlákno aplikace.

### Aliasy úrovní

`Logging.setLogLevel(String)` přijímá kromě standardních JUL jmen i krátké aliasy:

| Alias | Mapuje na |
| ----- | --------- |
| `TRACE`, `T`, `4` | `Level.ALL` |
| `DEBUG`, `D`, `3` | `Level.FINE` |
| `INFO`, `I`, `2` | `Level.INFO` |
| `WARNING`, `W`, `1` | `Level.WARNING` |
| `ERROR`, `E`, `0` | `Level.SEVERE` |

Python modul přijímá tytéž aliasy (mapované na nejbližší úroveň `logging`)
— vhodné, když jeden CLI přepínač řídí oba runtimy.

### Potlačení nevýznamných výjimek

Pokud je nějaký stack trace třetí strany očekávaný a nezajímavý, označ jeho
typ výjimky markerem [`LogSkipException`](LogSkipException.java);
`DefaultLoggerFilter` odpovídající záznamy zahodí dřív, než dorazí na
jakýkoli handler.

```java
public class HarmlessBootError extends RuntimeException implements LogSkipException
{
    public HarmlessBootError(String message) { super(message); }
}
```

## Výstupní formát

```text
dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message
```

Víceřádkové zprávy a stack trace jsou připojovány doslovně. Prefix `[catch]`
se vkládá u rámce, kde byla výjimka **chycena** (na rozdíl od místa
vyhození), což pomáhá při čtení hluboko zanořených příčin.

## Konvence úrovní

Podle Javadocu [`LogFactory`](LogFactory.java) projekt přiřazuje JUL úrovním tento význam:

- `CONFIG` — nelokalizovaný výpis konfigurace
- `FINEST` / `FINER` / `FINE` — nelokalizovaný debug detail (jen do souboru)
- `INFO` — lokalizovaná zpráva, ve výchozím nastavení do stavového řádku
- `WARNING` — lokalizované varování, ve výchozím nastavení do stavového řádku
- `SEVERE` — lokalizovaná chyba, ve výchozím nastavení do dialogu

## Související

- Java code style pro logování: [`docs/contributing/CODE-STYLE-JAVA.md`](../../../../../../../../docs/contributing/CODE-STYLE-JAVA.md#logging)
- Python parity modul: [`python/src/cassandragargoyle/api/log/`](../../../../../../../../python/src/cassandragargoyle/api/log)
