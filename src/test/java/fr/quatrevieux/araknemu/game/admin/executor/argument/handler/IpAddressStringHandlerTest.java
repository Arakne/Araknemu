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

package fr.quatrevieux.araknemu.game.admin.executor.argument.handler;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.AnnotationHydrator;
import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IpAddressStringHandlerTest extends TestCase {
    static class Arguments {
        @Argument
        public IPAddressString ip;
    }

    static class Command extends AbstractCommand<IpAddressStringHandlerTest.Arguments> {
        @Override
        protected void build(AbstractCommand<IpAddressStringHandlerTest.Arguments>.Builder builder) {}

        @Override
        public String name() {
            return "foo";
        }

        @Override
        public void execute(AdminPerformer performer, IpAddressStringHandlerTest.Arguments arguments) throws AdminException {}

        @Override
        public IpAddressStringHandlerTest.Arguments createArguments() {
            return new IpAddressStringHandlerTest.Arguments();
        }
    }

    @Test
    void hydrate() throws Exception {
        IpAddressStringHandlerTest.Command command = new IpAddressStringHandlerTest.Command();
        AnnotationHydrator hydrator = new AnnotationHydrator();

        assertEquals(new IPAddressString("14.25.32.12"), hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "14.25.32.12"), null)).ip);
        assertEquals(new IPAddressString("f806:2d06:0032:e4ff:f72e:6b14:a21b:ab6b"), hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "f806:2d06:0032:e4ff:f72e:6b14:a21b:ab6b"), null)).ip);

        assertThrowsWithMessage(CmdLineException.class, "Invalid IP address given", () -> hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "invalid"), null)));
    }
}
