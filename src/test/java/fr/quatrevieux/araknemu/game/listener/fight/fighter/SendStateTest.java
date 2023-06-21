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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterStateChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendStateTest extends FightBaseCase {
    private Fight fight;
    private SendState listener;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        listener = new SendState(fight);
    }

    @Test
    void onStateAdd() {
        listener.on(new FighterStateChanged(fighter, 5, FighterStateChanged.Type.ADD));

        requestStack.assertLast(ActionEffect.addState(fighter, 5));
    }

    @Test
    void onStateRemove() {
        listener.on(new FighterStateChanged(fighter, 5, FighterStateChanged.Type.REMOVE));

        requestStack.assertLast(ActionEffect.removeState(fighter, 5));
    }

    @Test
    void onStateUpdate() {
        requestStack.clear();
        listener.on(new FighterStateChanged(fighter, 5, FighterStateChanged.Type.UPDATE));

        requestStack.assertEmpty();
    }
}