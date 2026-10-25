---
title: Kam VIM směřuje
description: Závěrečná kapitola knihy o VIM o otevřených problémech a budoucnosti formátu. Čti, když plánuješ práci na VIM, hledáš známé problémy vytěžování nebo chceš otevřené otázky architektonických rozhodnutí VIM.
category: reference
ai_load: on-demand
status: active
language: cs
translation_of: 07-where-vim-is-going.md
created: 2026-10-25
last_updated: 2026-10-25
---

# Kam VIM směřuje

> Český překlad [07-where-vim-is-going.md](07-where-vim-is-going.md). Anglická verze je autoritativní.

## Poctivý střed

Každý mladý formát žije v poctivém středu. Je dost dobrý na to, aby se používal každý den,
a ještě ne dost dobrý na to, aby se na něj zapomnělo. VIM je teď přesně tam. Vzorová faktura
této knihy ukázala obě poloviny na jedné stránce: zákazníka a platbu přečtené správně, i s
důkazy, a nadpis, který spolkl dodavatele, a tabulku, jejíž sloupce se slily. Formát unesl
oba druhy výsledků stejně dobře. Samotné čtení má ale ještě dlouhou cestu před sebou.

Tato poslední kapitola se na tu cestu dívá: na problémy, které jsou známé, na místo, kde se
zkoumají, na otázky, které architektonická rozhodnutí záměrně nechala otevřené, a na
myšlenku, která to všechno drží pohromadě.

## Laboratoř pro těžké stránky

V květnu 2026 založil tým pro výzkum parseru samostatný repozitář,
`portunix-vim-parser-research-lab`. Má neobvyklé pravidlo: parser nikdy nemění přímo. Drží
ho jako submodul jen pro čtení, pouští ho nad sbírkami dokumentů, porovnává ho s jinými
nástroji a svá zjištění předává týmu parseru jako issue.

Laboratoř pracuje se dvěma druhy záznamů. **Položka katalogu** popisuje známý problém
vytěžování a žije dlouho; neuzavírá se jen proto, že skončil jeden pokus. **Výzva** je jedno
měření: verzované, opakovatelné srovnání s jasnou podmínkou vítězství a písemným verdiktem.
První tři položky katalogu jsou přesně ty problémy, na které dříve či později narazí každý,
kdo čte PDF:

| Problém | Co se pokazí |
| ------- | ------------ |
| Zápatí stránek | Čísla stránek, živá záhlaví a řádky s copyrightem se objevují v textu na každém zlomu stránky |
| Vícesloupcová sazba | Text čtený v uloženém pořadí kličkuje mezi sloupci |
| Tabulky přes zlom stránky | Z jedné logické tabulky jsou dvě a řádek záhlaví je tam dvakrát |

První výzva porovnává `vim-parser` s nástrojem Marker, open source převodníkem z PDF
do Markdownu, právě na těchto třech problémech. V době psaní je to kostra: pipeline je
definovaná, běhy ještě nezačaly. To není slabina laboratoře. Je to její metoda: nejdřív se
dohodnout, co se bude měřit a co se počítá jako vítězství, a teprve potom měřit.

Slité sloupce naší faktury patří do rodiny problému vícesloupcové sazby a tabulka, kterou
detektor našel přes velkou část stránky, do rodiny problému tabulek. Vzorek z této knihy by se mohl stát malou
položkou té sbírky.

## Otázky nechané otevřené záměrně

Dobrá architektonická rozhodnutí nepředstírají, že vědí všechno. Architektonická rozhodnutí o VIM končí otevřenými
otázkami a vyplatí se je vyjmenovat, protože ukazují, kudy bude formát růst.

**Místo pro pokusy.** Objekt `layers` je uzavřený a nová vrstva potřebuje schéma a dvě
implementace. To chrání spotřebitele, ale nenechává místo pro obsah, který ještě není dost
stabilní na kontrakt. Rozhodnutí o nových vrstvách zmiňuje možnou *oblast rozšíření* pro takový obsah a nechává ji
jako samostatné rozhodnutí.

**Registr vrstev.** Při šesti vrstvách se seznam vrstev s jejich vlastníky a výrobci
pohodlně vejde do specifikace formátu. Při více vrstvách může být potřeba krátký registr.

**Jazyk pro pravidla.** Ustanovení preskriptivní vrstvy může říct, že se ověřuje
automaticky, a nese objekt `rule`. Co smí v tomto objektu stát, kontrakt neurčuje; nástroje
se na tom musí dohodnout mimo něj. Malý jazyk pravidel je otevřenou otázkou rozhodnutí, které zavedlo preskriptivní vrstvu, spolu s
formátem dokumentu hodnocení, který zaznamená výsledek kontroly souladu.

**Domov výrobce s jazykovým modelem.** Preskriptivní vrstvu píše výrobce, který používá
jazykový model. Kde tento výrobce žije a jak se spouští, je také stále otevřené; totéž rozhodnutí
navrhuje, aby nástroje, které používají jazykový model během vývoje, běžely jako skilly
Claude Code.

**Druhá polovina pythonového portu.** Pythonový model vrstvy `extraction` a `prescriptive`
píše, ale zpět je nečte, a rozpoznávací pipeline reco extrakční vrstvu zatím neplní. Dokud
to nedělá, jmenuje kontrakt výrobce, který je spíš plánovaný než skutečný, a dokumentace to
přiznává.

## Drobné pravdy pro velké modely

V době velkých jazykových modelů svádí myšlenka, že na struktuře už nezáleží. Model přečte
PDF, nebo dokonce fotografii stránky, a odpoví na otázky o ní. Proč držet tři skleněné
tabule, ID a spolehlivosti, když se model může prostě podívat?

Pokus z prologu je první odpovědí. Tentýž model nad tímtéž dokumentem si s prostým textem
nebyl jistý a s vrstvami jistý byl. Struktura není berlička pro slabé čtenáře; je to způsob,
jak silný čtenář ukazuje svou práci. Druhou odpovědí je původ. Model, který odpoví *Jana
Příkladová*, je užitečný. Systém, který odpoví *Jana Příkladová, strana 1, blok 4,
spolehlivost 0,8, nalezeno podle popisku Odběratel*, se dá ověřit, opravit a dá se mu věřit.
Když se ponoříme do čtení dokumentů, odhalíme pestrý svět drobných, trpělivých dovedností:
seskupování, popisování, měření, pochybování. Snadno je přehlédneme, když se ženeme za novými
cíli. VIM je uchovává, a to ve tvaru, který přečte člověk, program i jazykový model.

Preskriptivní vrstva ukazuje na třetí odpověď. Tam je jazykový model pozván jako výrobce a
formát mu poctivě dělá místo: každé ustanovení, které navrhne, začíná jako `proposed` a
teprve člověk ho udělá `confirmed`. Model přináší rychlost, vrstvy přinášejí důkazy, člověk
přináší poslední slovo.

Lukáš Sedláček ve své knize o člověku v době umělé inteligence připomíná rozlišení
francouzského antropologa Clauda Lévi-Strausse. Existuje myšlení *divoké*, volné a
asociativní, které hledá podobnosti bez pevných pravidel, a myšlení *domestikované*, které
se přísně drží kategorií a dalo nám vědu, právní řád a nakonec i samotnou AI.
Domestikované myšlení by podle Sedláčka nebylo bez divokého schopné žádné inovace. VIM je
malé místo, kde se obě setkávají. Heuristiky parseru a jazykový model preskriptivní vrstvy
hádají, spojují a navrhují. Uzavřené schéma, stabilní identifikátory a člověk, který
potvrzuje, drží řád. Ani jedna polovina by sama nestačila.

## Zpátky ke sklu

Začínali jsme třemi skleněnými tabulemi nad stránkou. O devět měsíců později jich je šest a
další mohou přibýt, každá přes brány a kritéria žebříku. Myšlenka pod nimi se nezměnila:
uchovat, co tam je, uchovat, jak je to seskupené, uchovat, co to znamená, a nechat každou
hodnotu říct, odkud pochází a jak moc si jsme jistí.

Dokument, který prošel VIM, umí říct, co znamená. To byl slib jména a na tom slibu budou
pracovat i další verze.

## Prameny

- Položky katalogu I-PDF-001, I-PDF-002 a I-PDF-003 a výzva C001: `README.md`,
  `docs/catalog/pdf/` a `challenges/C001-pdf-vs-marker/` repozitáře
  `portunix-vim-parser-research-lab`
- ADR-009 a ADR-010, oddíl *Open Questions*, `portunix-architecture`
- `src/ptx_reco/vim/README.md` v `portunix-reco`
- Lukáš Sedláček, *Nazí v AI době* (2025, ISBN 978-80-53061-04-9): úvod (strany 13 až 16)
  a pasáž o divokém a domestikovaném myšlení (strany 21 a 22)
- Claude Lévi-Strauss, *La Pensée sauvage* (1962), česky *Myšlení přírodních národů*
