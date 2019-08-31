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

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.ChallengeRewardsGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTypeTest extends TestCase {
    @Test
    void values() {
        ChallengeType type = new ChallengeType();

        assertEquals(0, type.id());
        assertFalse(type.hasPlacementTimeLimit());
        assertTrue(type.canCancel());
        assertEquals(Duration.ofSeconds(30), type.turnDuration());
        assertInstanceOf(ChallengeRewardsGenerator.class, type.rewards());
    }
}