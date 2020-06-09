/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.core.network.netty.NettyServer;
import fr.quatrevieux.araknemu.core.network.parser.*;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import fr.quatrevieux.araknemu.core.network.session.extension.RateLimiter;
import fr.quatrevieux.araknemu.core.network.session.extension.SessionLogger;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.network.in.CommonParserLoader;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.RealmSessionConfigurator;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.network.realm.in.RealmParserLoader;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.handler.*;
import fr.quatrevieux.araknemu.realm.handler.account.Authenticate;
import fr.quatrevieux.araknemu.realm.handler.account.ConnectGame;
import fr.quatrevieux.araknemu.realm.handler.account.ListServers;
import fr.quatrevieux.araknemu.realm.handler.account.SearchFriend;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
            container -> LogManager.getLogger(RealmService.class)
        );

        configurator.factory(
            RealmService.class,
            container -> new RealmService(
                container.get(RealmConfiguration.class),
                container.get(Server.class),
                container.get(Logger.class)
            )
        );

        configurator.factory(
            RealmConfiguration.class,
            container -> app.configuration().module(RealmConfiguration.class)
        );

        configurator.factory(
            Server.class,
            container -> new NettyServer(
                container.get(SessionFactory.class),
                container.get(RealmConfiguration.class).port(),
                container.get(RealmConfiguration.class).inactivityTime()
            )
        );

        configurator.factory(
            SessionFactory.class,
            container -> new SessionConfigurator<>(RealmSession::new)
                .add(new RateLimiter.Configurator<>(container.get(RealmConfiguration.class).packetRateLimit()))
                .add(new SessionLogger.Configurator<>(container.get(Logger.class)))
                .add(new RealmSessionConfigurator(
                    container.get(Dispatcher.class),
                    new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
                    container.get(PacketParser.class),
                    container.get(Logger.class)
                ))
        );

        configurator.factory(
            Dispatcher.class,
            container -> new DefaultDispatcher<RealmSession>(
                new PacketHandler[] {
                    new StartSession(),
                    new StopSession(container.get(AuthenticationService.class)),
                    new CloseInactiveSession(),
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
                    ),
                    new SearchFriend(container.get(HostService.class)),
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
