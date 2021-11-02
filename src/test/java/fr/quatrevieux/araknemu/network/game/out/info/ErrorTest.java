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

import fr.arakne.utils.value.Interval;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {
    @Test
    void welcome() {
        assertEquals("Im189;", Error.welcome().toString());
        assertSame(Error.welcome(), Error.welcome());
    }

    @Test
    void cantDoOnServer() {
        assertEquals("Im1226;", Error.cantDoOnServer().toString());
        assertSame(Error.cantDoOnServer(), Error.cantDoOnServer());
    }

    @Test
    void cantLearnSpell() {
        assertEquals("Im17;123", Error.cantLearnSpell(123).toString());
    }

    @Test
    void cantCastNotFound() {
        assertEquals("Im1169;", Error.cantCastNotFound().toString());
        assertSame(Error.cantCastNotFound(), Error.cantCastNotFound());
    }

    @Test
    void cantCastInvalidCell() {
        assertEquals("Im1193;", Error.cantCastInvalidCell().toString());
        assertSame(Error.cantCastInvalidCell(), Error.cantCastInvalidCell());
    }

    @Test
    void cantCastCellNotAvailable() {
        assertEquals("Im1172;", Error.cantCastCellNotAvailable().toString());
        assertSame(Error.cantCastCellNotAvailable(), Error.cantCastCellNotAvailable());
    }

    @Test
    void cantCastLineLaunch() {
        assertEquals("Im1173;", Error.cantCastLineLaunch().toString());
        assertSame(Error.cantCastLineLaunch(), Error.cantCastLineLaunch());
    }

    @Test
    void cantCastNotEnoughActionPoints() {
        assertEquals("Im1170;4~5", Error.cantCastNotEnoughActionPoints(4, 5).toString());
    }

    @Test
    void cantCastBadState() {
        assertEquals("Im1116;", Error.cantCastBadState().toString());
        assertSame(Error.cantCastBadState(), Error.cantCastBadState());
    }


    @Test
    void cantDoOnCurrentState() {
        assertEquals("Im1116;", Error.cantDoOnCurrentState().toString());
        assertSame(Error.cantDoOnCurrentState(), Error.cantDoOnCurrentState());
    }

    @Test
    void cantCastBadRange() {
        assertEquals("Im1171;2~5~1", Error.cantCastBadRange(new Interval(2, 5), 1).toString());
    }

    @Test
    void cantCast() {
        assertEquals("Im1175;", Error.cantCast().toString());
        assertSame(Error.cantCast(), Error.cantCast());
    }

    @Test
    void cantCastSightBlocked() {
        assertEquals("Im1174;", Error.cantCastSightBlocked().toString());
        assertSame(Error.cantCastSightBlocked(), Error.cantCastSightBlocked());
    }

    @Test
    void cantDoDuringFight() {
        assertEquals("Im191;", Error.cantDoDuringFight().toString());
        assertSame(Error.cantDoDuringFight(), Error.cantDoDuringFight());
    }

    @Test
    void cantMoveOverweight() {
        assertEquals("Im112;", Error.cantMoveOverweight().toString());
        assertSame(Error.cantMoveOverweight(), Error.cantMoveOverweight());
    }

    @Test
    void shutdownScheduled() {
        assertEquals("Im115;10min", Error.shutdownScheduled("10min").toString());
    }

    @Test
    void saveInProgress() {
        assertEquals("Im1164;", Error.saveInProgress().toString());
        assertSame(Error.saveInProgress(), Error.saveInProgress());
    }

    @Test
    void saveTerminated() {
        assertEquals("Im1165;", Error.saveTerminated().toString());
        assertSame(Error.saveTerminated(), Error.saveTerminated());
    }

    @Test
    void cantJoinFightAsSpectator() {
        assertEquals("Im157;", Error.cantJoinFightAsSpectator().toString());
    }
}
