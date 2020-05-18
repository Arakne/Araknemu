# Araknemu [![Build Status](https://scrutinizer-ci.com/g/Arakne/Araknemu/badges/build.png?b=master)](https://scrutinizer-ci.com/g/Arakne/Araknemu/build-status/master) [![Scrutinizer Code Quality](https://scrutinizer-ci.com/g/Arakne/Araknemu/badges/quality-score.png?b=master)](https://scrutinizer-ci.com/g/Arakne/Araknemu/?branch=master) [![Code Coverage](https://scrutinizer-ci.com/g/Arakne/Araknemu/badges/coverage.png?b=master)](https://scrutinizer-ci.com/g/Arakne/Araknemu/?branch=master)

A simple open source Dofus 1.29 server emulator, implementing only base features, with high quality standard.

## Getting started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java JDK >= 8
- Maven
- Git
- MySQL (or MariaDB)

### Installing

**Note:** Those instruction enable the setup of a development environment, not a production one

- Clone the repository and go to the project directory
    ```
    git clone https://github.com/Arakne/Araknemu.git && cd Araknemu
    ```
- Build the JAR
    ```
    mvn package -DskipTests=true
    ```
- Create database and configure the `conf.ini` file (The database is not provided)

### Running

Launch the built jar.
To run the server, your working directory should contains the `conf.ini` file.

```
java -jar target/araknemu-0.6-alpha-jar-with-dependencies.jar
```

## About the project

### Project state

The project is in pre-alpha development state, and thus, the architecture is not stable, and will change until v1.0 is released. 
It can only be used for testing or development purposes.

### Features

To see all currently implemented features you can go to [closed feature issues](https://github.com/Arakne/Araknemu/issues?q=is%3Aissue+is%3Aclosed+label%3AFeature).

- Monolithic server with game and auth server
- Player
    - Creation and deletion
    - Experience and level up
    - Characteristics (per class)
    - Learn and upgrade spells
    - Exchange
- Maps
    - Teleportation plot
    - Teleport admin on double click
- NPC
    - Dialog
    - Store
    - Exchange
    - Dungeon
- Chat channels (private, map, trade, recruitment, admin...)
- Items
    - Characteristics
    - Usable items (i.e. potions)
    - Item sets
- Bank
- Combat
    - Duel
    - PvM
    - AI
    - [Spell effects](https://github.com/Arakne/Araknemu/issues/27)

### Questions & answers

#### Where is the database ?

This project only provides the server source code. You should create your own database in order to launch the server. 
A command will be created to generate the database structure.

#### Why this project ? There is a lot of other dofus emulators...

All dofus servers I've seen have very bad quality standards, don't follow semantic versioning, 
are always on rolling release (i.e. no maintained version), and no unit and functional tests. 
So, the goal of this project is to provide a reliable open source base for Dofus servers.

## Contributing

Please read [CONTRIBUTING.md](./CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Licence

This project is licensed under the LGPLv3 licence. See [COPYING](./COPYING) and [COPYING.LESSER](./COPYING.LESSER) files for details.