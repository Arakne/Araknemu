package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.network.adapter.Server;
import fr.quatrevieux.araknemu.network.adapter.SessionHandler;
import fr.quatrevieux.araknemu.network.adapter.mina.MinaServer;
import fr.quatrevieux.araknemu.network.adapter.util.LoggingSessionHandler;
import fr.quatrevieux.araknemu.network.in.*;
import fr.quatrevieux.araknemu.network.realm.RealmSessionHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.network.realm.in.RealmParserLoader;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.handler.*;
import fr.quatrevieux.araknemu.realm.handler.account.Authenticate;
import fr.quatrevieux.araknemu.realm.handler.account.ConnectGame;
import fr.quatrevieux.araknemu.realm.handler.account.ListServers;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DI module for RealmService
 */
final public class RealmModule implements ContainerModule {
    final private Araknemu app;

    public RealmModule(Araknemu app) {
        this.app = app;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.factory(
            Logger.class,
            container -> LoggerFactory.getLogger(RealmService.class)
        );

        configurator.factory(
            RealmService.class,
            container -> new RealmService(
                container.get(RealmConfiguration.class),
                container.get(Server.class)
            )
        );

        configurator.factory(
            RealmConfiguration.class,
            container -> app.configuration().module(RealmConfiguration.class)
        );

        configurator.factory(
            Server.class,
            container -> new MinaServer(
                container.get(SessionHandler.class),
                container.get(RealmConfiguration.class).port()
            )
        );

        configurator.factory(
            SessionHandler.class,
            container -> new LoggingSessionHandler(
                new RealmSessionHandler(
                    container.get(Dispatcher.class),
                    new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
                    container.get(PacketParser.class)
                ),
                container.get(Logger.class)
            )
        );

        configurator.factory(
            Dispatcher.class,
            container -> new DefaultDispatcher<RealmSession>(
                new PacketHandler[] {
                    new StartSession(),
                    new StopSession(container.get(AuthenticationService.class)),
                    new CheckDofusVersion(container.get(RealmConfiguration.class)),
                    new Authenticate(
                        container.get(AuthenticationService.class),
                        container.get(HostService.class)
                    ),
                    new CheckQueuePosition(),
                    new ListServers(
                        container.get(HostService.class)
                    ),
                    new PongResponse(),
                    new ConnectGame(
                        container.get(HostService.class)
                    )
                }
            )
        );

        configurator.factory(
            PacketParser.class,
            container ->  new AggregatePacketParser(
                new AggregateParserLoader(
                    new ParserLoader[]{
                        new CommonParserLoader(),
                        new RealmParserLoader()
                    }
                ).load()
            )
        );

        configurator.persist(
            AuthenticationService.class,
            container -> new AuthenticationService(
                container.get(AccountRepository.class),
                container.get(HostService.class)
            )
        );

        configurator.persist(
            HostService.class,
            container -> new HostService(
                container.get(PlayerRepository.class)
            )
        );
    }
}
