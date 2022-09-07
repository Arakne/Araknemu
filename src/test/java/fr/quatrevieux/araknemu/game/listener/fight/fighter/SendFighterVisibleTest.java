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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterVisible;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class SendFighterVisibleTest extends FightBaseCase {
    private Fight fight;
    private SendFighterVisible listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendFighterVisible(
            fight = createFight()
        );
    }

    @Test
    void onFighterVisible() {
        requestStack.clear();
        listener.on(new FighterVisible(player.fighter(), other.fighter()));

        requestStack.assertAll(
            new FighterPositions(Collections.singleton(player.fighter())),
            ActionEffect.fighterVisible(other.fighter(), player.fighter())
        );
    }
}
