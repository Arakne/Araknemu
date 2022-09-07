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
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterHidden;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendFighterHiddenTest extends FightBaseCase {
    private Fight fight;
    private SendFighterHidden listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendFighterHidden(
            fight = createFight()
        );
    }

    @Test
    void onFighterHidden() {
        requestStack.clear();
        listener.on(new FighterHidden(player.fighter(), other.fighter()));

        requestStack.assertAll(
            ActionEffect.fighterHidden(other.fighter(), player.fighter())
        );
    }
}
