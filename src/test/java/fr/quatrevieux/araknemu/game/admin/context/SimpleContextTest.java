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

package fr.quatrevieux.araknemu.game.admin.context;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

class SimpleContextTest extends GameBaseCase {
    class DummyCommand implements Command<Void> {
        final String name;

        public DummyCommand(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public String help() {
            return null;
        }

        @Override
        public void execute(AdminPerformer output, Void arguments) {

        }

        @Override
        public Set<Permission> permissions() {
            return null;
        }

        @Override
        public String toString() {
            return "Dummy command " + name;
        }
    }

    @Test
    void addCommand() throws CommandNotFoundException {
        Command command = Mockito.mock(Command.class);

        Mockito.when(command.name()).thenReturn("cmd");

        SimpleContext context = new SimpleContext(new NullContext());

        assertSame(context, context.add(command));
        assertSame(command, context.command("cmd"));
    }

    @Test
    void commandFromParent() throws CommandNotFoundException {
        SimpleContext parent = new SimpleContext(new NullContext());

        Command command = Mockito.mock(Command.class);
        Mockito.when(command.name()).thenReturn("cmd");
        parent.add(command);

        SimpleContext context = new SimpleContext(parent);

        assertSame(command, context.command("cmd"));
    }

    @Test
    void commands() {
        SimpleContext parent = new SimpleContext(new NullContext());

        Command p1, p2, c1, c2, c3;

        parent.add(p1 = new DummyCommand("p1"));
        parent.add(p2 = new DummyCommand("p2"));

        SimpleContext context = new SimpleContext(parent);

        context.add(c1 = new DummyCommand("c1"));
        context.add(c2 = new DummyCommand("c2"));
        context.add(c3 = new DummyCommand("c3"));

        assertCollectionEquals(context.commands(), p1, p2, c1, c2, c3);
    }

    @Test
    void commandsShouldFilterOverriddenParentCommands() {
        SimpleContext parent = new SimpleContext(new NullContext());

        Command p1, p2, c1, c2;

        parent.add(p1 = new DummyCommand("foo"));
        parent.add(p2 = new DummyCommand("bar"));

        SimpleContext context = new SimpleContext(parent);

        context.add(c1 = new DummyCommand("foo"));
        context.add(c2 = new DummyCommand("baz"));

        assertCollectionEquals(context.commands(), p2, c1, c2);
    }

    @Test
    void addChild() throws ContextNotFoundException {
        SimpleContext context = new SimpleContext(new NullContext());

        Context child = new NullContext();

        assertSame(context, context.add("child", child));
        assertSame(child, context.child("child"));
    }

    @Test
    void childFromParent() throws ContextNotFoundException {
        SimpleContext parent = new SimpleContext(new NullContext());

        Context child = new NullContext();
        parent.add("child", child);

        SimpleContext context = new SimpleContext(parent);

        assertSame(child, context.child("child"));
    }
}
