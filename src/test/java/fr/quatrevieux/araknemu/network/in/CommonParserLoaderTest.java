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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.in;

import fr.quatrevieux.araknemu.core.network.parser.ParserLoader;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.in.CommonParserLoader;
import fr.quatrevieux.araknemu.network.in.PingResponse;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.fail;

class CommonParserLoaderTest {
    @Test
    void packets() {
        ParserLoader loader = new CommonParserLoader();

        Collection<SinglePacketParser> parsers = loader.load();

        assertContainsInstance(PingResponse.Parser.class, parsers);
        assertContainsInstance(AskQueuePosition.Parser.class, parsers);
    }

    public void assertContainsInstance(Class type, Collection collection) {
        for (Object o : collection) {
            if (type.isInstance(o)) {
                return;
            }
        }

        fail("Cannot found instance of " + type.getSimpleName());
    }
}
