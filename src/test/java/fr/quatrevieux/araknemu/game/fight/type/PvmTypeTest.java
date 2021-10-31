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

package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.PvmRewardsGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvmTypeTest extends GameBaseCase {
    @Test
    void values() {
        RewardsGenerator generator = new PvmRewardsGenerator(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        PvmType type = new PvmType(generator, configuration.fight());

        assertEquals(4, type.id());
        assertTrue(type.hasPlacementTimeLimit());
        assertFalse(type.canCancel());
        assertEquals(Duration.ofSeconds(30), type.turnDuration());
        assertEquals(Duration.ofSeconds(45), type.placementDuration());
        assertSame(generator, type.rewards());

        setConfigValue("fight.turnDuration", "45s");
        setConfigValue("fight.pvm.placementDuration", "1m");
        assertEquals(Duration.ofSeconds(45), type.turnDuration());
        assertEquals(Duration.ofSeconds(60), type.placementDuration());
    }
}
