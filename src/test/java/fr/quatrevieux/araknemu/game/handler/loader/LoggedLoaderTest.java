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

package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.handler.EnsureLogged;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.in.account.ChoosePlayingCharacter;
import fr.quatrevieux.araknemu.network.game.in.account.DeleteCharacterRequest;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoggedLoaderTest extends LoaderTestCase {
    private LoggedLoader loader;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        loader = new LoggedLoader();
    }

    @Test
    void load() throws ContainerException {
        PacketHandler[] handlers = loader.load(container);

        assertContainsOnly(EnsureLogged.class, handlers);

        assertHandlePacket(AskCharacterList.class, handlers);
        assertHandlePacket(AddCharacterRequest.class, handlers);
        assertHandlePacket(ChoosePlayingCharacter.class, handlers);
        assertHandlePacket(DeleteCharacterRequest.class, handlers);
    }
}
