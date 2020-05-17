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

import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.ShutdownService;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.account.Info;
import fr.quatrevieux.araknemu.game.admin.context.ContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.debug.*;
import fr.quatrevieux.araknemu.game.admin.global.GlobalContext;
import fr.quatrevieux.araknemu.game.admin.player.GetItem;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContext;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContextResolver;
import fr.quatrevieux.araknemu.game.admin.player.teleport.*;
import fr.quatrevieux.araknemu.game.admin.server.Online;
import fr.quatrevieux.araknemu.game.admin.server.ServerContext;
import fr.quatrevieux.araknemu.game.admin.server.ServerContextResolver;
import fr.quatrevieux.araknemu.game.admin.server.Shutdown;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;

import java.util.Arrays;

/**
 * Register the admin service and console commands
 *
 * Note: Only commands which needs dependencies from the container should be defined here. "simple" commands may be defined directly on the context
 */
final public class AdminModule implements ContainerModule {
    @Override
    public void configure(ContainerConfigurator configurator) {
        configureService(configurator);
        configureResolvers(configurator);
    }

    private void configureService(ContainerConfigurator configurator) {
        configurator.persist(
            AdminService.class,
            container -> new AdminService(
                container.get(GlobalContext.class),
                Arrays.asList(
                    container.get(PlayerContextResolver.class),
                    container.get(AccountContextResolver.class),
                    container.get(DebugContextResolver.class),
                    container.get(ServerContextResolver.class)
                )
            )
        );

        configurator.persist(
            GlobalContext.class,
            container -> new GlobalContext()
        );
    }

    private void configureResolvers(ContainerConfigurator configurator) {
        configurator.factory(
            PlayerContextResolver.class,
            container -> new PlayerContextResolver(container.get(PlayerService.class), container.get(AccountContextResolver.class))
                .register(new ContextConfigurator<PlayerContext>() {
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
            container -> new AccountContextResolver()
                .register(new ContextConfigurator<AccountContext>() {
                    @Override
                    public void configure(AccountContext context) {
                        add(new Info(context.account(), container.get(AccountRepository.class)));
                    }
                })
        );

        configurator.persist(
            DebugContextResolver.class,
            container -> new DebugContextResolver()
                .register(new ContextConfigurator<DebugContext>() {
                    @Override
                    public void configure(DebugContext context) {
                        add(new GenItem(container.get(ItemService.class)));
                        add(new FightPos(container.get(MapTemplateRepository.class)));
                        add(new Movement(container.get(MapTemplateRepository.class)));
                        add(new MapStats(container.get(MapTemplateRepository.class)));
                        add(new Area(container.get(SpellEffectService.class)));
                    }
                })
        );

        configurator.persist(
            ServerContextResolver.class,
            container -> new ServerContextResolver()
                .register(new ContextConfigurator<ServerContext>() {
                    @Override
                    public void configure(ServerContext context) {
                        add(new Online(
                            container.get(PlayerService.class),
                            container.get(ExplorationMapService.class)
                        ));
                        add(new Shutdown(container.get(ShutdownService.class)));
                    }
                })
        );
    }
}
