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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.CarryingApplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarryingModuleTest extends FightBaseCase {
    private Fight fight;
    private CarryingApplier applier;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = fightBuilder()
            .addSelf(fb -> fb.cell(165))
            .addAlly(fb -> fb.cell(166))
            .addEnemy(fb -> fb.player(other).cell(150))
            .addEnemy(fb -> fb.cell(151))
            .build(true)
        ;
        fight.nextState();
        fight.register(new CarryingModule(fight));
        applier = new CarryingApplier(fight, 3, 8);
    }

    @Test
    void onCarriedFighterDieShouldRemoveCarryingEffect() {
        applier.carry(player.fighter(), other.fighter());

        other.fighter().life().kill(other.fighter());

        assertFalse(applier.active(player.fighter()));
        assertFalse(applier.active(other.fighter()));
    }

    @Test
    void onCarrierFighterDieShouldRemoveCarryingEffectAndSetOnCell() {
        applier.carry(player.fighter(), other.fighter());

        player.fighter().life().kill(player.fighter());

        assertFalse(applier.active(player.fighter()));
        assertFalse(applier.active(other.fighter()));
        assertSame(other.fighter(), other.fighter().cell().fighter());
    }

    @Test
    void onCarrierMoveShouldSynchronizeCarriedCell() {
        applier.carry(player.fighter(), other.fighter());

        player.fighter().move(fight.map().get(167));

        assertTrue(applier.active(player.fighter()));
        assertTrue(applier.active(other.fighter()));
        assertSame(fight.map().get(167), other.fighter().cell());
        assertSame(player.fighter(), fight.map().get(167).fighter());
    }

    @Test
    void onCarriedMoveShouldStopEffect() {
        applier.carry(player.fighter(), other.fighter());

        other.fighter().move(fight.map().get(167));

        assertFalse(applier.active(player.fighter()));
        assertFalse(applier.active(other.fighter()));
        assertSame(fight.map().get(167), other.fighter().cell());
        assertSame(other.fighter(), fight.map().get(167).fighter());
    }
}
