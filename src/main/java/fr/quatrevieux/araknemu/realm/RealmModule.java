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
import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.common.account.banishment.network.BanIpCheck;
import fr.quatrevieux.araknemu.common.session.SessionLogService;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.core.network.netty.NettyServer;
import fr.quatrevieux.araknemu.core.network.parser.AggregatePacketParser;
import fr.quatrevieux.araknemu.core.network.parser.AggregateParserLoader;
import fr.quatrevieux.araknemu.core.network.parser.DefaultDispatcher;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.parser.ParserLoader;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import fr.quatrevieux.araknemu.core.network.session.extension.RateLimiter;
import fr.quatrevieux.araknemu.core.network.session.extension.SessionLogger;
import fr.quatrevieux.araknemu.data.living.repository.BanIpRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.BanishmentRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.network.in.CommonParserLoader;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.RealmSessionConfigurator;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.network.realm.in.RealmParserLoader;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.authentication.password.Argon2Hash;
import fr.quatrevieux.araknemu.realm.authentication.password.PasswordManager;
import fr.quatrevieux.araknemu.realm.authentication.password.PlainTextHash;
import fr.quatrevieux.araknemu.realm.handler.CheckDofusVersion;
import fr.quatrevieux.araknemu.realm.handler.CheckQueuePosition;
import fr.quatrevieux.araknemu.realm.handler.CloseInactiveSession;
import fr.quatrevieux.araknemu.realm.handler.PongResponse;
import fr.quatrevieux.araknemu.realm.handler.StartSession;
import fr.quatrevieux.araknemu.realm.handler.StopSession;
import fr.quatrevieux.araknemu.realm.handler.account.Authenticate;
import fr.quatrevieux.araknemu.realm.handler.account.ConnectGame;
import fr.quatrevieux.araknemu.realm.handler.account.ListServers;
import fr.quatrevieux.araknemu.realm.handler.account.SearchFriend;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

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

        configurator.persist(
            RealmService.class,
            container -> new RealmService(
                container.get(RealmConfiguration.class),
                container.get(Server.class),
                container.get(Logger.class),
                container.get(ListenerAggregate.class),
                Arrays.asList(
                    container.get(AuthBanIpSynchronizer.class)
                ),
                Arrays.asList(
                    container.get(AuthBanIpSynchronizer.class)
                )
            )
        );

        configurator.persist(ListenerAggregate.class, container -> new DefaultListenerAggregate(container.get(Logger.class)));
        configurator.factory(fr.quatrevieux.araknemu.core.event.Dispatcher.class, container -> container.get(ListenerAggregate.class));

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
                .add(new BanIpCheck<>(container.get(BanIpService.class)))
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
                        container.get(HostService.class),
                        container.get(SessionLogService.class)
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
                        new RealmParserLoader(),
                    }
                ).load()
            )
        );

        configurator.persist(
            AuthenticationService.class,
            container -> new AuthenticationService(
                container.get(AccountRepository.class),
                container.get(HostService.class),
                container.get(PasswordManager.class),
                container.get(BanishmentService.class)
            )
        );

        configurator.persist(
            HostService.class,
            container -> new HostService(
                container.get(PlayerRepository.class)
            )
        );

        configurator.persist(SessionLogService.class, container -> new SessionLogService(
            container.get(ConnectionLogRepository.class)
        ));

        configurator.persist(
            PasswordManager.class,
            container -> new PasswordManager(
                Arrays.asList(container.get(RealmConfiguration.class).passwordHashAlgorithms()),
                container.get(Argon2Hash.class),
                container.get(PlainTextHash.class)
            )
        );

        configurator.persist(
            BanishmentService.class,
            container -> new BanishmentService(container.get(BanishmentRepository.class), container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class))
        );

        configurator.persist(
            BanIpService.class,
            container -> new BanIpService(container.get(BanIpRepository.class), container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class))
        );

        configurator.persist(
            AuthBanIpSynchronizer.class,
            container -> new AuthBanIpSynchronizer(
                container.get(BanIpService.class),
                () -> container.get(RealmService.class).sessions(),
                container.get(Logger.class),
                container.get(RealmConfiguration.class).banIpRefresh()
            )
        );

        configurator.factory(
            Argon2Hash.class,
            container -> {
                RealmConfiguration.Argon2 config = container.get(RealmConfiguration.class).argon2();

                return new Argon2Hash()
                    .setIterations(config.iterations())
                    .setMemory(config.memory())
                    .setParallelism(config.parallelism())
                    .setType(config.type())
                ;
            }
        );

        configurator.factory(PlainTextHash.class, container -> new PlainTextHash());
    }
}
