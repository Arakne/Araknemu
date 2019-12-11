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
import fr.quatrevieux.araknemu.core.network.SessionIdle;
import fr.quatrevieux.araknemu.game.handler.CheckQueuePosition;
import fr.quatrevieux.araknemu.game.handler.SendPong;
import fr.quatrevieux.araknemu.game.handler.StartSession;
import fr.quatrevieux.araknemu.game.handler.StopSession;
import fr.quatrevieux.araknemu.game.handler.account.GenerateName;
import fr.quatrevieux.araknemu.game.handler.account.Login;
import fr.quatrevieux.araknemu.game.handler.account.SendRegionalVersion;
import fr.quatrevieux.araknemu.game.handler.basic.SendDateAndTime;
import fr.quatrevieux.araknemu.network.game.in.QuickPing;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommonLoaderTest extends LoaderTestCase {
    private CommonLoader loader;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        loader = new CommonLoader();
    }

    @Test
    void load() throws ContainerException {
        PacketHandler[] handlers = loader.load(container);

        assertContainsType(StartSession.class, handlers);
        assertContainsType(StopSession.class, handlers);
        assertContainsType(CheckQueuePosition.class, handlers);
        assertContainsType(Login.class, handlers);
        assertContainsType(SendRegionalVersion.class, handlers);
        assertContainsType(SendDateAndTime.class, handlers);
        assertContainsType(SendPong.class, handlers);
        assertContainsType(GenerateName.class, handlers);
        assertHandlePacket(QuickPing.class, handlers);
        assertHandlePacket(SessionIdle.class, handlers);
    }
}
