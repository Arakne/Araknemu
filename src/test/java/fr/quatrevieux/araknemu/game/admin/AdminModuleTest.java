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
import fr.quatrevieux.araknemu.game.admin.context.SelfContextResolver;
import fr.quatrevieux.araknemu.game.admin.debug.DebugContext;
import fr.quatrevieux.araknemu.game.admin.debug.DebugContextResolver;
import fr.quatrevieux.araknemu.game.admin.debug.FightPos;
import fr.quatrevieux.araknemu.game.admin.debug.LineOfSight;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ExceptionHandler;
import fr.quatrevieux.araknemu.game.admin.executor.CommandExecutor;
import fr.quatrevieux.araknemu.game.admin.executor.DefaultCommandExecutor;
import fr.quatrevieux.araknemu.game.admin.executor.argument.ArgumentsHydrator;
import fr.quatrevieux.araknemu.game.admin.executor.argument.HydratorsAggregate;
import fr.quatrevieux.araknemu.game.admin.global.Echo;
import fr.quatrevieux.araknemu.game.admin.global.GlobalContext;
import fr.quatrevieux.araknemu.game.admin.global.Help;
import fr.quatrevieux.araknemu.game.admin.player.AddXp;
import fr.quatrevieux.araknemu.game.admin.player.GetItem;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContext;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContextResolver;
import fr.quatrevieux.araknemu.game.admin.player.teleport.Goto;
import fr.quatrevieux.araknemu.game.admin.server.Banip;
import fr.quatrevieux.araknemu.game.admin.server.Online;
import fr.quatrevieux.araknemu.game.admin.server.ServerContext;
import fr.quatrevieux.araknemu.game.admin.server.ServerContextResolver;
import fr.quatrevieux.araknemu.game.admin.server.Shutdown;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class AdminModuleTest extends GameBaseCase {
    @Test
    void instances() throws SQLException, CommandNotFoundException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app));

        assertInstanceOf(AdminSessionService.class, container.get(AdminSessionService.class));
        assertInstanceOf(PlayerContextResolver.class, container.get(PlayerContextResolver.class));
        assertInstanceOf(AccountContextResolver.class, container.get(AccountContextResolver.class));
        assertInstanceOf(DebugContextResolver.class, container.get(DebugContextResolver.class));
        assertInstanceOf(GlobalContext.class, container.get(GlobalContext.class));
        assertInstanceOf(Help.class, container.get(GlobalContext.class).command("help"));
        assertInstanceOf(Echo.class, container.get(GlobalContext.class).command("echo"));
        assertInstanceOf(ServerContextResolver.class, container.get(ServerContextResolver.class));
        assertInstanceOf(DefaultCommandExecutor.class, container.get(CommandExecutor.class));
        assertInstanceOf(AdminUser.Factory.class, container.get(AdminUser.Factory.class));
        assertInstanceOf(HydratorsAggregate.class, container.get(ArgumentsHydrator.class));
        assertInstanceOf(SelfContextResolver.class, container.get(SelfContextResolver.class));
        assertInstanceOf(ContextCommandParser.class, container.get(CommandParser.class));
        assertInstanceOf(ExceptionHandler.class, container.get(ExceptionHandler.class));
    }

    @Test
    void playerResolver() throws SQLException, ContextException, CommandNotFoundException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app));

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(PlayerContextResolver.class).resolve(gamePlayer());

        assertInstanceOf(PlayerContext.class, context);
        assertInstanceOf(GetItem.class, context.command("getitem"));
        assertInstanceOf(Goto.class, context.command("goto"));
        assertInstanceOf(AddXp.class, context.command("addxp"));
    }

    @Test
    void playerResolverScripts() throws SQLException, ContextException, CommandNotFoundException, NoSuchFieldException, IllegalAccessException {
        setConfigValue("admin", "player.scripts.enable", "true");
        setConfigValue("admin", "player.scripts.path", "src/test/scripts/commands/player");

        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app));

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(PlayerContextResolver.class).resolve(gamePlayer());

        Command<?> command = context.command("sp");

        assertSame(gamePlayer(), command.getClass().getField("player").get(command));
    }

    @Test
    void accountResolver() throws SQLException, CommandNotFoundException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app));

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(AccountContextResolver.class).resolve(gamePlayer().account());

        assertInstanceOf(AccountContext.class, context);
        assertInstanceOf(Info.class, context.command("info"));
        assertInstanceOf(Ban.class, context.command("ban"));
    }

    @Test
    void accountResolverScripts() throws SQLException, ContextException, CommandNotFoundException, NoSuchFieldException, IllegalAccessException {
        setConfigValue("admin", "account.scripts.enable", "true");
        setConfigValue("admin", "account.scripts.path", "src/test/scripts/commands/account");

        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app));

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(AccountContextResolver.class).resolve(gamePlayer().account());

        Command<?> command = context.command("sa");

        assertSame(gamePlayer().account(), command.getClass().getField("account").get(command));
    }

    @Test
    void debugResolver() throws SQLException, CommandNotFoundException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app));

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(DebugContextResolver.class).resolve(null, null);

        assertInstanceOf(DebugContext.class, context);
        assertInstanceOf(FightPos.class, context.command("fightpos"));
        assertInstanceOf(LineOfSight.class, context.command("lineofsight"));
    }

    @Test
    void debugResolverScripts() throws SQLException, ContextException, CommandNotFoundException, NoSuchFieldException, IllegalAccessException {
        setConfigValue("admin", "debug.scripts.enable", "true");
        setConfigValue("admin", "debug.scripts.path", "src/test/scripts/commands/simple");

        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app));

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(DebugContextResolver.class).resolve(null, null);

        assertNotNull(context.command("simple"));
    }

    @Test
    void serverResolver() throws SQLException, CommandNotFoundException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app));
        container.register(configurator -> configurator.set(RealmConnector.class, Mockito.mock(RealmConnector.class)));

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(ServerContextResolver.class).resolve(null, null);

        assertInstanceOf(ServerContext.class, context);
        assertInstanceOf(Online.class, context.command("online"));
        assertInstanceOf(Shutdown.class, context.command("shutdown"));
        assertInstanceOf(Banip.class, context.command("banip"));
        assertInstanceOf(fr.quatrevieux.araknemu.game.admin.server.Info.class, context.command("info"));
    }

    @Test
    void serverResolverScripts() throws SQLException, ContextException, CommandNotFoundException, NoSuchFieldException, IllegalAccessException {
        setConfigValue("admin", "server.scripts.enable", "true");
        setConfigValue("admin", "server.scripts.path", "src/test/scripts/commands/simple");

        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app));
        container.register(configurator -> configurator.set(RealmConnector.class, Mockito.mock(RealmConnector.class)));

        container.get(PlayerService.class).load(session, gamePlayer().id());

        Context context = container.get(ServerContextResolver.class).resolve(null, null);

        assertNotNull(context.command("simple"));
    }
}
