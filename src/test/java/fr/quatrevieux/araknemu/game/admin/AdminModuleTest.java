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

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.SqlLivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.SqlWorldRepositoriesModule;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameModule;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.account.Ban;
import fr.quatrevieux.araknemu.game.admin.account.Info;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.debug.*;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.global.GlobalContext;
import fr.quatrevieux.araknemu.game.admin.player.AddXp;
import fr.quatrevieux.araknemu.game.admin.player.GetItem;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContext;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContextResolver;
import fr.quatrevieux.araknemu.game.admin.player.teleport.Goto;
import fr.quatrevieux.araknemu.game.admin.server.Online;
import fr.quatrevieux.araknemu.game.admin.server.ServerContext;
import fr.quatrevieux.araknemu.game.admin.server.ServerContextResolver;
import fr.quatrevieux.araknemu.game.admin.server.Shutdown;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

class AdminModuleTest extends GameBaseCase {
    @Test
    void instances() throws SQLException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule());

        assertInstanceOf(AdminService.class, container.get(AdminService.class));
        assertInstanceOf(PlayerContextResolver.class, container.get(PlayerContextResolver.class));
        assertInstanceOf(AccountContextResolver.class, container.get(AccountContextResolver.class));
        assertInstanceOf(DebugContextResolver.class, container.get(DebugContextResolver.class));
        assertInstanceOf(GlobalContext.class, container.get(GlobalContext.class));
        assertInstanceOf(ServerContextResolver.class, container.get(ServerContextResolver.class));
    }

    @Test
    void playerResolver() throws SQLException, ContextException, CommandNotFoundException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule());

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(PlayerContextResolver.class).resolve(container.get(GlobalContext.class), gamePlayer().name());

        assertInstanceOf(PlayerContext.class, context);
        assertInstanceOf(GetItem.class, context.command("getitem"));
        assertInstanceOf(Goto.class, context.command("goto"));
        assertInstanceOf(AddXp.class, context.command("addxp"));
    }

    @Test
    void accountResolver() throws SQLException, ContextException, CommandNotFoundException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule());

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(AccountContextResolver.class).resolve(container.get(GlobalContext.class), gamePlayer().account());

        assertInstanceOf(AccountContext.class, context);
        assertInstanceOf(Info.class, context.command("info"));
        assertInstanceOf(Ban.class, context.command("ban"));
    }

    @Test
    void debugResolver() throws SQLException, CommandNotFoundException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule());

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(DebugContextResolver.class).resolve(container.get(GlobalContext.class), null);

        assertInstanceOf(DebugContext.class, context);
        assertInstanceOf(GenItem.class, context.command("genitem"));
        assertInstanceOf(FightPos.class, context.command("fightpos"));
        assertInstanceOf(Movement.class, context.command("movement"));
        assertInstanceOf(MapStats.class, context.command("mapstats"));
        assertInstanceOf(Area.class, context.command("area"));
        assertInstanceOf(LineOfSight.class, context.command("lineofsight"));
    }

    @Test
    void serverResolver() throws SQLException, CommandNotFoundException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule());
        container.register(configurator -> configurator.set(RealmConnector.class, Mockito.mock(RealmConnector.class)));

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(ServerContextResolver.class).resolve(container.get(GlobalContext.class), null);

        assertInstanceOf(ServerContext.class, context);
        assertInstanceOf(Online.class, context.command("online"));
        assertInstanceOf(Shutdown.class, context.command("shutdown"));
    }
}
