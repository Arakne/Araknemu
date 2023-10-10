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

package fr.quatrevieux.araknemu.network.game.out.info;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InformationTest {
    @Test
    void chatFlood() {
        assertEquals(
            "Im0115;1234",
            Information.chatFlood(1234).toString()
        );
    }

    @Test
    void heal() {
        assertEquals(
            "Im01;10",
            Information.heal(10).toString()
        );
    }

    @Test
    void spellLearn() {
        assertEquals(
            "Im03;10",
            Information.spellLearn(10).toString()
        );
    }

    @Test
    void characteristicBoosted() {
        assertEquals("Im09;10", Information.characteristicBoosted(Characteristic.WISDOM, 10).toString());
        assertEquals("Im010;10", Information.characteristicBoosted(Characteristic.STRENGTH, 10).toString());
        assertEquals("Im011;10", Information.characteristicBoosted(Characteristic.LUCK, 10).toString());
        assertEquals("Im012;10", Information.characteristicBoosted(Characteristic.AGILITY, 10).toString());
        assertEquals("Im013;10", Information.characteristicBoosted(Characteristic.VITALITY, 10).toString());
        assertEquals("Im014;10", Information.characteristicBoosted(Characteristic.INTELLIGENCE, 10).toString());

        assertNull(Information.characteristicBoosted(Characteristic.FIXED_DAMAGE, 10));
    }

    @Test
    void cannotPostItemOnChannel() {
        assertEquals(
            "Im0114;",
            Information.cannotPostItemOnChannel().toString()
        );
    }

    @Test
    void positionSaved() {
        assertEquals(
            "Im06;",
            Information.positionSaved().toString()
        );
    }

    @Test
    void bankTaxPayed() {
        assertEquals(
            "Im020;42",
            Information.bankTaxPayed(42).toString()
        );
    }

    @Test
    void lastLogin() {
        assertEquals(
            "Im0152;2020~6~20~16~25~141.22.12.34",
            Information.lastLogin(LocalDateTime.ofInstant(Instant.parse("2020-06-20T14:25:00.00Z"), ZoneId.of("Europe/Paris")), "141.22.12.34").toString()
        );
    }

    @Test
    void currentIpAddress() {
        assertEquals(
            "Im0153;141.22.12.34",
            Information.currentIpAddress("141.22.12.34").toString()
        );
    }

    @Test
    void spectatorHasJoinFight() {
        assertEquals("Im036;Bob", Information.spectatorHasJoinFight("Bob").toString());
    }

    @Test
    void teamOptions() {
        assertEquals("Im0103;", Information.helpRequested().toString());
        assertEquals("Im0104;", Information.helpRequestStopped().toString());
        assertEquals("Im095;", Information.joinTeamLocked().toString());
        assertEquals("Im096;", Information.joinTeamReleased().toString());
        assertEquals("Im040;", Information.spectatorsBlocked().toString());
        assertEquals("Im039;", Information.spectatorsAllowed().toString());
    }
}
