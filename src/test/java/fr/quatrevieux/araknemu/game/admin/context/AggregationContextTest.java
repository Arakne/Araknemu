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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.context;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.game.admin.global.Help;
import fr.quatrevieux.araknemu.game.admin.player.Info;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContextResolver;
import fr.quatrevieux.araknemu.game.admin.server.ServerContextResolver;
import fr.quatrevieux.araknemu.game.admin.server.Shutdown;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AggregationContextTest extends GameBaseCase {
    private AggregationContext context;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        context = new AggregationContext(
            container.get(PlayerContextResolver.class).resolve(gamePlayer()),
            container.get(ServerContextResolver.class).resolve(null, null)
        );
    }

    @Test
    void command() throws CommandNotFoundException {
        assertInstanceOf(Help.class, context.command("help"));
        assertInstanceOf(Shutdown.class, context.command("shutdown"));
        assertInstanceOf(Info.class, context.command("info"));

        assertThrows(CommandNotFoundException.class, () -> context.command("not_found"));
    }

    @Test
    void commands() {
        Collection<Command> commands = context.commands();

        assertContainsType(Help.class, commands);
        assertContainsType(Shutdown.class, commands);
        assertContainsType(Info.class, commands);

        assertFalse(commands.stream().anyMatch(fr.quatrevieux.araknemu.game.admin.server.Info.class::isInstance));
    }

    @Test
    void child() throws ContextNotFoundException {
        assertInstanceOf(AccountContext.class, context.child("account"));
        assertThrows(ContextException.class, () -> context.child("not_found"));
    }
}
