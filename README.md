# Araknemu [![Java CI](https://github.com/Arakne/Araknemu/actions/workflows/ci.yaml/badge.svg)](https://github.com/Arakne/Araknemu/actions/workflows/ci.yaml) [![Scrutinizer Code Quality](https://scrutinizer-ci.com/g/Arakne/Araknemu/badges/quality-score.png?b=master)](https://scrutinizer-ci.com/g/Arakne/Araknemu/?branch=master) [![codecov](https://codecov.io/gh/Arakne/Araknemu/branch/master/graph/badge.svg?token=PFK2YM1T6W)](https://codecov.io/gh/Arakne/Araknemu)

A simple and modular open source Dofus 1.29 server emulator, implementing only base features.

## Getting started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

First, you need to get the code from the repository.
- Clone the repository (if you haven't already)
    ```
    git clone https://github.com/Arakne/Araknemu.git
    ```
- Go to the project directory, and make updates if needed
    ```
    cd Araknemu
    git pull
    ```

### Docker

You can use the provided Dockerfile to build and run the server in a container.
This image is a multi-stage build, with the first stage building the server, and the second stage running it.

Build the image:
```
docker build --target production -t araknemu .
```

Then you should create your database. The database structure can be created using:
```
mysql -u <user> -p <database_name> < resources/db/000-init_mysql.sql
```

Now you can run the server (environment variables are listed below):
```
docker run -p 5555:5555 -p 4444:4444 -e GAME_HOST=172.17.0.1 -e DB_HOST=172.17.0.1 -e DB_PASSWORD=araknemu -v './logs:/srv/logs' araknemu
```

Environment variables:

| Variable                    | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | Default value                           |
|-----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| `AUTH_PORT`                 | The port used by the login server. This port should be exposed using `-p` option.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           | `4444`                                  |
| `CLIENT_VERSION`            | The required client version.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                | `1.29.1`                                |
| `PASSWORD_HASH`             | List of available password hash algorithms, separated by a coma. The first of the list will be used for re-hash the password, so make sure to define the best algorithm at first position. Available values : `argon2`, `plain`.                                                                                                                                                                                                                                                                                                                                            | `argon2, plain`                         |
| `DB_TYPE`                   | Database driver to use. Available types : `mysql`, `sqlite`.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                | `mysql`                                 |
| `DB_HOST`                   | Database host name (only mysql). You can set a custom port by suffixing it by `:[port]`. If you want to use your locale database, you can set it to `172.17.0.1`.                                                                                                                                                                                                                                                                                                                                                                                                           | `127.0.0.1`                             |
| `DB_USER`                   | Database username (only mysql).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | `araknemu`                              |
| `DB_PASSWORD`               | Database password (only mysql).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | empty                                   |
| `DB_NAME`                   | Database name in case of mysql, of the file path in case of sqlite. Don't forget to declare a volume if you use sqlite driver (e.g. `-v araknemu.db:/srv/araknemu.db`).                                                                                                                                                                                                                                                                                                                                                                                                     | mysql: `araknemu` sqlite: `araknemu.db` |
| `DB_POOL_SIZE`              | Number of opened connections. Increase this value if the message "Pool is empty" occurs too often on logs.                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | `8`                                     |
| `GAME_PORT`                 | The port used by the game server. This port should be exposed using `-p` option.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | `5555`                                  |
| `GAME_HOST`                 | The public host name of the game server. This host must be accessible from internet if you want to open your server to users.                                                                                                                                                                                                                                                                                                                                                                                                                                               | `127.0.0.1`                             |
| `TIMEZONE`                  | Timezone used for in game clock and database times. See: https://docs.oracle.com/middleware/12211/wcs/tag-ref/MISC/TimeZones.html                                                                                                                                                                                                                                                                                                                                                                                                                                           | `Europe/Paris`                          |
| `GAME_SERVER_ID`            | The server id to launch. The server must be present in langs, and should be available for the target community. Data are not shared between 2 servers, so if you change the server ID, data will not be present.                                                                                                                                                                                                                                                                                                                                                            | `1` (Jiva)                              |
| `ACTIVITY_THREADS`          | Number of threads to launch for run "activities" (e.g. moving monsters, respawn...). Increase this value only if the respawn is to slow.                                                                                                                                                                                                                                                                                                                                                                                                                                    | `1`                                     |
| `RESPAWN_SPEED_FACTOR`      | Divide the respawn time of monster groups defined in database. For example, if the monster group is defined to respawn after 1 minute, with a factor of 2, it will respawn after 30 seconds.                                                                                                                                                                                                                                                                                                                                                                                | `1`                                     |
| `FIGHT_THREADS`             | Number of threads used to execute fight actions and AI. The minimal value should be 2. A good value may be around 1 thread per 100 active fights.                                                                                                                                                                                                                                                                                                                                                                                                                           | `4`                                     |
| `RATE_XP`                   | The experience gain multiplier. The value should be a positive decimal number.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | `1.0`                                   |
| `RATE_DROP`                 | The object drop multiplier. The value should be a positive decimal number. This value will modify the object drop chance, but not the maximum dropped items per monster nor minimal discernment requirement.                                                                                                                                                                                                                                                                                                                                                                | `1.0`                                   |
| `AI_SCRIPT_PATH`            | The path where AI scripts are stored. This path must be relative to the docker image. If you want to define custom scripts, don't forget to declare a volume (e.g. `-v ./scripts:/srv/scripts`). If the path doesn't exists yet, it will be ignored.                                                                                                                                                                                                                                                                                                                        | `scripts/ai`                            |
| `PRELOAD`                   | Enable data preloading or not. Values `1`, `true`, `yes`, `on` will enable preloading. Any other values will disable it. Disable preloading allows faster startup, and lower memory usage, but adds some performance penalty. It's advisable to enable it on production with long uptime, and disable it if the server is restarted regularly.                                                                                                                                                                                                                              | `true`                                  |
| `ADMIN_COMMAND_SCRIPT_PATH` | Base path for admin command scripts. The following sub-directories should be used to declare commands on the respective scope : `account`, `player`, `debug`, `server`. Don't forget to declare a volume (e.g. `-v ./scripts:/srv/scripts`). If the path doesn't exists yet, it will be ignored.                                                                                                                                                                                                                                                                            | `scripts/commands`                      |
| `JAVA_MEMORY`               | Define the heap memory used by java (same amount is used for start and max memory). If you set to empty value, the memory will be allocated dynamically. This value is a number followed by a letter to define the unit (i.e. `M` for megabyte, `G` for gigabyte). High value will reduce the GC usage and improve performance, but GC pause time can be higher. Note: this is the heap size, not the actual allocated memory by java. The actual memory will be around 1.5 to 2 times the value of the heap size (for example, `512m` will takes around 800 MB of memory). | `512m`                                  |
| `JAVA_GC`                   | Use a custom garbage collector algorithm. Available values (not exhaustive) `G1GC`, `ZGC`. `G1GC` seems to works well, even with low heap size (most of the pause time is less than 10ms), but non-predictable pause time may cause random lags. `ZGC` will minimize pause time (most are less than 1ms), but use more memory, and quickly trigger out of memory with low heap size.                                                                                                                                                                                        | `G1GC`                                  |
| `JAVA_GC_LOG`               | If set to `1`, will enable GC logs into `/srv/logs/gc-*.log`. Don't forget to declare a volume on logs (e.g. `-v ./logs:/srv/logs`).                                                                                                                                                                                                                                                                                                                                                                                                                                        | none (disabled)                         |
| `JAVA_OPTS`                 | Custom java options.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        | none                                    |

Advanced configuration:

If provided environment variables are not enough, you can create a custom `config.ini` file, and mount it into the container.
The file should be placed in the `/srv/config.ini` path.

- Copy the default configuration file
    ```
    cp config.ini.dist config.ini
    ```
- Edit the configuration file
- Run the container with the volume option
    ```
    docker run -v './config.ini:/srv/config.ini' [...] araknemu
    ```

### Docker-compose

You can use the provided `docker-compose.yml` file to run the server in a container.

- Create a `.env` file with desired configuration (See [Docker section](#docker) for environment variables)
    ```
    # Use the mariadb server from the db service
    DB_HOST=db
  
    # You can define any other environment variables here
    GAME_HOST=myserver.example.com
    JAVA_MEMORY=1G
    ```
- Configure the `docker-compose.yml` file if needed (e.g. change the ports, add volumes, etc.)
- Build the server
  ```
  docker-compose build
  ```
- Setup the database by copying the SQL file containing the dataset into `resources/db/`. The database structure is already created by the file `000-init_mysql.sql`.
- Run the server (can take some time to start the first time because of the database setup)
  ```
  docker-compose up
  ```

### Local

#### Prerequisites

- Java JDK >= 8
- Maven
- Git
- MySQL (or MariaDB)

#### Installing

**Note:** Those instruction enable the setup of a development environment, not a production one

- Build the JAR
    ```
    mvn package -DskipTests=true
    ```
- Create Mysql database structure (the data are not provided)
    ```
    mysql -u <user> -p <database_name> < resources/db/000-init_mysql.sql
    ```
- Copy the `config.ini.dist` file
    ```
    cp conf.ini.dist conf.ini
    ``` 
- Configure the `config.ini` file 

#### Running

Run Mysql server.
Launch the built jar.
To run the server, your working directory should contain the `config.ini` file.

```
java -jar target/araknemu-0.12-alpha-jar-with-dependencies.jar
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
You can find the database structure in the file `resources/db/000-init_mysql.sql`.

To create your own database, you must either convert an existing database, or extract data from dofus lang files and maps.

> [!NOTE]
> In the future, a command may be created to partially generate the database.

#### Why this project ? There is a lot of other dofus emulators...

Most other servers are designed to be feature-rich, and provide an opinionated version of the Dofus experience.
Instead of that, this project is designed to be modular, and provide a reliable base for developers 
to build their own experience.

The reliability of the server is based on automated tests (CI), continuous stability tests, 
and mostly following semantic versioning.

The key goals of this project are (at least):
- Modularity and extensibility for developers to build their own experience
- At least 1 month of uptime without any instabilities
- No detectable memory leaks
- Handle at least 1000 players connected at the same time with less than 1GB of memory and without noticeable lags

## Contributing

Please read [CONTRIBUTING.md](./CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Licence

This project is licensed under the LGPLv3 licence. See [COPYING](./COPYING) and [COPYING.LESSER](./COPYING.LESSER) files for details.
