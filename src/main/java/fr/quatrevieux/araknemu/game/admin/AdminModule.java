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

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameService;
import fr.quatrevieux.araknemu.game.ShutdownService;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.account.Ban;
import fr.quatrevieux.araknemu.game.admin.account.Info;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.debug.Area;
import fr.quatrevieux.araknemu.game.admin.debug.DebugContext;
import fr.quatrevieux.araknemu.game.admin.debug.DebugContextResolver;
import fr.quatrevieux.araknemu.game.admin.debug.FightPos;
import fr.quatrevieux.araknemu.game.admin.debug.LineOfSight;
import fr.quatrevieux.araknemu.game.admin.debug.MapStats;
import fr.quatrevieux.araknemu.game.admin.debug.Movement;
import fr.quatrevieux.araknemu.game.admin.executor.CommandExecutor;
import fr.quatrevieux.araknemu.game.admin.executor.DefaultCommandExecutor;
import fr.quatrevieux.araknemu.game.admin.executor.argument.ArgumentsHydrator;
import fr.quatrevieux.araknemu.game.admin.executor.argument.HydratorsAggregate;
import fr.quatrevieux.araknemu.game.admin.global.GlobalContext;
import fr.quatrevieux.araknemu.game.admin.player.GetItem;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContext;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContextResolver;
import fr.quatrevieux.araknemu.game.admin.player.teleport.CellResolver;
import fr.quatrevieux.araknemu.game.admin.player.teleport.Goto;
import fr.quatrevieux.araknemu.game.admin.player.teleport.LocationResolver;
import fr.quatrevieux.araknemu.game.admin.player.teleport.MapResolver;
import fr.quatrevieux.araknemu.game.admin.player.teleport.PlayerResolver;
import fr.quatrevieux.araknemu.game.admin.player.teleport.PositionResolver;
import fr.quatrevieux.araknemu.game.admin.server.Banip;
import fr.quatrevieux.araknemu.game.admin.server.Online;
import fr.quatrevieux.araknemu.game.admin.server.ServerContext;
import fr.quatrevieux.araknemu.game.admin.server.ServerContextResolver;
import fr.quatrevieux.araknemu.game.admin.server.Shutdown;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;

/**
 * Register the admin service and console commands
 *
 * Note: Only commands which needs dependencies from the container should be defined here. "simple" commands may be defined directly on the context
 */
public final class AdminModule implements ContainerModule {
    private final Araknemu app;

    public AdminModule(Araknemu app) {
        this.app = app;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configureService(configurator);
        configureResolvers(configurator);
    }

    private void configureService(ContainerConfigurator configurator) {
        configurator.factory(Araknemu.class, container -> app);

        configurator.persist(
            AdminService.class,
            container -> new AdminService(
                container.get(GlobalContext.class),
                Arrays.asList(
                    container.get(PlayerContextResolver.class),
                    container.get(AccountContextResolver.class),
                    container.get(DebugContextResolver.class),
                    container.get(ServerContextResolver.class)
                ),
                container.get(AdminUser.Factory.class)
            )
        );

        configurator.persist(
            AdminUser.Factory.class,
            container -> ((service, player) -> new AdminUser(
                service,
                player,
                container.get(CommandExecutor.class),
                LogManager.getLogger(AdminService.class)
            ))
        );

        configurator.persist(
            GlobalContext.class,
            container -> new GlobalContext()
        );

        configurator.persist(
            CommandExecutor.class,
            container -> new DefaultCommandExecutor(container.get(ArgumentsHydrator.class))
        );

        configurator.persist(
            ArgumentsHydrator.class,
            container -> new HydratorsAggregate()
        );
    }

    private void configureResolvers(ContainerConfigurator configurator) {
        configurator.factory(
            PlayerContextResolver.class,
            container -> new PlayerContextResolver(container.get(PlayerService.class), container.get(AccountContextResolver.class))
                .register(new AbstractContextConfigurator<PlayerContext>() {
                    @Override
                    public void configure(PlayerContext context) {
                        add(new GetItem(context.player(), container.get(ItemService.class)));
                        add(new Goto(context.player(), container.get(ExplorationMapService.class), new LocationResolver[] {
                            new MapResolver(container.get(ExplorationMapService.class)),
                            new PositionResolver(context.player(), container.get(GeolocationService.class)),
                            new PlayerResolver(container.get(PlayerService.class), container.get(ExplorationMapService.class)),
                            new CellResolver(),
                        }));
                    }
                })
        );

        configurator.persist(
            AccountContextResolver.class,
            container -> new AccountContextResolver(container.get(AccountService.class))
                .register(new AbstractContextConfigurator<AccountContext>() {
                    @Override
                    public void configure(AccountContext context) {
                        add(new Info(context.account(), container.get(AccountRepository.class)));
                        add(new Ban(context.account(), container.get(BanishmentService.class)));
                    }
                })
        );

        configurator.persist(
            DebugContextResolver.class,
            container -> new DebugContextResolver()
                .register(new AbstractContextConfigurator<DebugContext>() {
                    @Override
                    public void configure(DebugContext context) {
                        add(new FightPos());
                        add(new Movement(container.get(MapTemplateRepository.class)));
                        add(new MapStats(container.get(MapTemplateRepository.class)));
                        add(new Area(container.get(SpellEffectService.class)));
                        add(new LineOfSight(container.get(MapTemplateRepository.class)));
                    }
                })
        );

        configurator.persist(
            ServerContextResolver.class,
            container -> new ServerContextResolver()
                .register(new AbstractContextConfigurator<ServerContext>() {
                    @Override
                    public void configure(ServerContext context) {
                        add(new Online(
                            container.get(PlayerService.class),
                            container.get(ExplorationMapService.class),
                            container.get(GameService.class)
                        ));
                        add(new Shutdown(container.get(ShutdownService.class)));
                        add(new Banip(container.get(BanIpService.class)));
                        add(new fr.quatrevieux.araknemu.game.admin.server.Info(
                            container.get(Araknemu.class),
                            container.get(PlayerService.class),
                            container.get(GameService.class),
                            container.get(FightService.class)
                        ));
                    }
                })
        );
    }
}
