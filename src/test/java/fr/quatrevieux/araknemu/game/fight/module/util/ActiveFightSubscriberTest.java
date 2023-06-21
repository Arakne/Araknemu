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

package fr.quatrevieux.araknemu.game.fight.module.util;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActiveFightSubscriberTest extends FightBaseCase {
    class MyListener implements Listener<FighterChangePlace> {
        @Override
        public void on(FighterChangePlace event) {

        }

        @Override
        public Class<FighterChangePlace> event() {
            return FighterChangePlace.class;
        }
    }

    @Test
    void listeners() throws Exception {
        Fight fight = createFight(false);

        fight.dispatcher().register(new ActiveFightSubscriber(new Listener[] {new MyListener()}));

        // Call join fight on fighters
        new PlacementState().start(fight);

        fight.start(new AlternateTeamFighterOrder());
        assertTrue(fight.dispatcher().has(MyListener.class));

        fight.stop();
        assertFalse(fight.dispatcher().has(MyListener.class));
    }
}
