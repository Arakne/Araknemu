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

package fr.quatrevieux.araknemu.game.handler.basic;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.in.basic.AskDate;
import fr.quatrevieux.araknemu.network.game.out.basic.ServerDate;
import fr.quatrevieux.araknemu.network.game.out.basic.ServerTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendDateAndTimeTest extends GameBaseCase {
    private SendDateAndTime handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new SendDateAndTime();
    }

    @Test
    void handler() throws Exception {
        handler.handle(session, new AskDate());

        requestStack.assertContains(ServerDate.class);
        requestStack.assertContains(ServerTime.class);
    }
}
