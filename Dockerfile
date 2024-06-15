ARG MAVEN_VERSION=3.8
ARG JAVA_VERSION=17

# First stage: compile the application
FROM maven:${MAVEN_VERSION}-openjdk-${JAVA_VERSION} AS development

WORKDIR srv

COPY src src
COPY pom.xml .
COPY config.ini.dist config.ini.dist
COPY scripts scripts

# Ignore tests and checkerframework
RUN mvn package -DskipTests -P!checkerframework

# Copy the compiled jar to /srv/araknemu.jar
RUN cp target/araknemu-*-with-dependencies.jar araknemu.jar

# Second stage: contains only runtime dependencies
FROM openjdk:${JAVA_VERSION}-jdk-slim AS production

ENV AUTH_PORT=4444
ENV GAME_PORT=5555

WORKDIR srv

COPY --from=development /srv/araknemu.jar araknemu.jar
COPY config.ini.dist config.ini.dist
COPY scripts scripts
COPY start.sh start.sh

# Install netcat, used in start.sh to wait for mariadb to be ready
RUN apt-get update && apt-get install -y netcat-openbsd

RUN mkdir -p logs
RUN chmod +x start.sh

VOLUME /srv/logs

EXPOSE ${AUTH_PORT}
EXPOSE ${GAME_PORT}

ENTRYPOINT ["./start.sh"]
