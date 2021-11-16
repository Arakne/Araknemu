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
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.handler.EnsureFighting;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterReady;
import fr.quatrevieux.araknemu.network.game.in.fight.KickFighterRequest;
import fr.quatrevieux.araknemu.network.game.in.fight.TurnEnd;
import fr.quatrevieux.araknemu.network.game.in.fight.option.BlockSpectatorRequest;
import fr.quatrevieux.araknemu.network.game.in.fight.option.LockTeamRequest;
import fr.quatrevieux.araknemu.network.game.in.fight.option.NeedHelpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FightingLoaderTest extends LoaderTestCase {
    private FightingLoader loader;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        loader = new FightingLoader();
    }

    @Test
    void load() throws ContainerException {
        PacketHandler[] handlers = loader.load(container);

        assertContainsOnly(EnsureFighting.class, handlers);

        assertHandlePacket(FighterChangePlace.class, handlers);
        assertHandlePacket(FighterReady.class, handlers);
        assertHandlePacket(TurnEnd.class, handlers);
        assertHandlePacket(BlockSpectatorRequest.class, handlers);
        assertHandlePacket(LockTeamRequest.class, handlers);
        assertHandlePacket(NeedHelpRequest.class, handlers);
        assertHandlePacket(KickFighterRequest.class, handlers);
    }
}
