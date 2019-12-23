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

package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterReadyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendFighterReadyStateTest extends FightBaseCase {
    private SendFighterReadyState listener;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        fighter = makePlayerFighter(gamePlayer(true));
        Fight fight = createFight();

        listener = new SendFighterReadyState(fight);
    }

    @Test
    void onFighterReadyStateChanged() {
        listener.on(new FighterReadyStateChanged(fighter));

        requestStack.assertLast(new FighterReadyState(fighter));
    }
}