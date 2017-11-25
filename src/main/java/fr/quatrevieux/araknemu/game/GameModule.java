package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.game.handler.StartSession;
import fr.quatrevieux.araknemu.network.LoggedIoHandler;
import fr.quatrevieux.araknemu.network.game.GameIoHandler;
import fr.quatrevieux.araknemu.network.game.in.GameParserLoader;
import fr.quatrevieux.araknemu.network.in.*;
import org.apache.mina.core.service.IoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Module for game service
 */
final public class GameModule implements ContainerModule {
    final private Araknemu app;

    public GameModule(Araknemu app) {
        this.app = app;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.factory(
            Logger.class,
            container -> LoggerFactory.getLogger(GameService.class)
        );

        configurator.factory(
            GameService.class,
            container -> new GameService(
                container.get(GameConfiguration.class),
                container.get(RealmConnector.class),
                container.get(IoHandler.class)
            )
        );

        configurator.factory(
            GameConfiguration.class,
            container -> app.configuration().module(GameConfiguration.class)
        );

        configurator.factory(
            IoHandler.class,
            container -> new LoggedIoHandler(
                new GameIoHandler(
                    container.get(Dispatcher.class),
                    container.get(PacketParser.class)
                ),
                container.get(Logger.class)
            )
        );

        configurator.factory(
            Dispatcher.class,
            container -> new DefaultDispatcher(
                new PacketHandler[]{
                    new StartSession()
                }
            )
        );

        configurator.factory(
            PacketParser.class,
            container ->  new AggregatePacketParser(
                new AggregateParserLoader(
                    new ParserLoader[]{
                        new CommonParserLoader(),
                        new GameParserLoader()
                    }
                ).load()
            )
        );

        configurator.persist(
            ConnectorService.class,
            container -> new ConnectorService()
        );
    }
}
