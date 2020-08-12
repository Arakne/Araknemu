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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.common.account.banishment;

import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BanEntryTest extends GameBaseCase {
    @Test
    void active() {
        login();

        assertTrue(new BanEntry<>(session.account(), new Banishment(1, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "", -1)).active());
        assertTrue(new BanEntry<>(session.account(), new Banishment(1, Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS), "", -1)).active());
        assertTrue(new BanEntry<>(session.account(), new Banishment(1, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now(), "", -1)).active());
        assertFalse(new BanEntry<>(session.account(), new Banishment(1, Instant.now().plus(1, ChronoUnit.MICROS), Instant.now().plus(1, ChronoUnit.HOURS), "", -1)).active());
        assertFalse(new BanEntry<>(session.account(), new Banishment(1, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().minus(1, ChronoUnit.MICROS), "", -1)).active());
    }
}
