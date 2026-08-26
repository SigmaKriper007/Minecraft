# Opus vs EXE

A Minecraft 1.20.1 Fabric adventure mod about memory-metal, the Great War, Haiku machines, parallel realms, living biomes, settlements, bosses, equipment progression, and a 26-trophy final archive.

## Player documentation

Open [`docs/index.html`](docs/index.html) for the illustrated, searchable field guide. It covers:

- story and progression routes;
- structures, realms, creatures, and boss mechanics;
- equipment abilities, cooldowns, and resurrection rituals;
- the complete generated recipe book;
- the full localized item/block compendium;
- all trophies and the final completion advancement.

Regenerate its live catalogs after resource changes:

```sh
python3 docs/generate_catalog.py
```

## Build

```sh
JAVA_HOME=/path/to/jdk17 ./gradlew build
```

The remapped mod JAR is written to `build/libs/`.
