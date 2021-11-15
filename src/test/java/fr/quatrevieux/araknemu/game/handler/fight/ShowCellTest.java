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

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.network.game.in.fight.ShowCellRequest;
import fr.quatrevieux.araknemu.network.game.out.fight.CellShown;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShowCellTest extends FightBaseCase {
    @Test
    void notInFight() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new ShowCellRequest(123)));
    }

    @Test
    void duringPlacement() throws Exception {
        Fight fight = createFight();

        handlePacket(new ShowCellRequest(123));
        Thread.sleep(50);

        requestStack.assertLast(new CellShown(gamePlayer().fighter(), 123));
    }

    @Test
    void onActiveFight() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        handlePacket(new ShowCellRequest(123));
        Thread.sleep(50);

        requestStack.assertLast(new CellShown(gamePlayer().fighter(), 123));
    }
}
