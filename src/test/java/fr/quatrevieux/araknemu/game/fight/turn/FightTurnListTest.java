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

package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.turn.event.NextTurnInitiated;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnListChanged;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FightTurnListTest extends FightBaseCase {
    private Fight fight;
    private FightTurnList turnList;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        turnList = new FightTurnList(fight, new AlternateTeamFighterOrder());
    }

    @Test
    void constructorWillOrderFighters() {
        assertEquals(
            Arrays.asList(player.fighter(), other.fighter()),
            turnList.fighters()
        );

        assertSame(player.fighter(), turnList.currentFighter());
    }

    @Test
    void constructorWithoutFighters() {
        fight.cancel(true);

        assertThrowsWithMessage(IllegalStateException.class, "Cannot initialise turn list without fighters", () -> new FightTurnList(fight, new AlternateTeamFighterOrder()));
    }

    @Test
    void currentNotStarted() {
        assertFalse(turnList.current().isPresent());
    }

    @Test
    void start() {
        turnList.start();

        assertTrue(turnList.current().isPresent());
        assertSame(player.fighter(), turnList.current().get().fighter());
        assertSame(turnList.current().get(), player.fighter().turn());
    }

    @Test
    void startAlreadyStartedShouldRaiseException() {
        turnList.start();

        assertThrows(IllegalStateException.class, turnList::start);
    }

    @Test
    void nextWillStartNextFighterTurn() {
        turnList.start();

        AtomicReference<TurnStarted> ref1 = new AtomicReference<>();
        AtomicReference<NextTurnInitiated> ref2 = new AtomicReference<>();
        fight.dispatcher().add(
            new Listener<TurnStarted>() {
                @Override
                public void on(TurnStarted event) {
                    ref1.set(event);
                }

                @Override
                public Class<TurnStarted> event() {
                    return TurnStarted.class;
                }
            }
        );
        fight.dispatcher().add(
            new Listener<NextTurnInitiated>() {
                @Override
                public void on(NextTurnInitiated event) {
                    ref2.set(event);
                }

                @Override
                public Class<NextTurnInitiated> event() {
                    return NextTurnInitiated.class;
                }
            }
        );

        turnList.next();

        assertSame(other.fighter(), turnList.current().get().fighter());
        assertSame(other.fighter().turn(), turnList.current().get());
        assertSame(other.fighter().turn(), ref1.get().turn());
        assertSame(other.fighter(), turnList.currentFighter());
        assertNotNull(ref2.get());
    }

    @Test
    void nextOnEndOfListWillRestartToFirst() {
        turnList.start();

        turnList.next();
        turnList.next();

        assertSame(player.fighter(), turnList.current().get().fighter());
    }

    @Test
    void nextWillSkipDeadFighter() {
        fight.fighters().forEach(Fighter::init);
        turnList.start();

        other.fighter().life().alter(other.fighter(), -1000);
        assertTrue(other.fighter().dead());

        turnList.next();
        assertSame(player.fighter(), turnList.current().get().fighter());
        assertSame(player.fighter(), turnList.currentFighter());
    }

    @Test
    void stop() {
        // @todo to remove : because turn is linked to fight and not to turn list, calling turn.stop() will stop the incorrect turnlist
        // so it will be not initialized
        fight.start(new AlternateTeamFighterOrder());
        turnList.start();

        turnList.stop();

        assertFalse(turnList.current().isPresent());
        assertSame(player.fighter(), turnList.currentFighter());
    }

    @Test
    void stopNotActive() {
        turnList.stop();
    }

    @Test
    void remove() {
        turnList.start();

        AtomicReference<TurnListChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(TurnListChanged.class, ref::set);

        turnList.remove(player.fighter());
        assertSame(player.fighter(), turnList.currentFighter()); // Remove do not change the current fighter

        assertSame(turnList, ref.get().turnList());
        assertFalse(turnList.fighters().contains(player.fighter()));
    }

    @Test
    void removeAndNext() throws SQLException {
        PlayerFighter third = makePlayerFighter(makeSimpleGamePlayer(5));
        fight.team(0).join(third);

        turnList = new FightTurnList(fight, new AlternateTeamFighterOrder());
        turnList.start();
        assertEquals(player.fighter(), turnList.currentFighter());

        turnList.next();
        assertEquals(other.fighter(), turnList.currentFighter());

        turnList.next();
        assertEquals(third, turnList.currentFighter());

        turnList.remove(third);
        assertEquals(third, turnList.currentFighter());
        turnList.next();
        assertEquals(player.fighter(), turnList.currentFighter());
    }

    /**
     * Bug: https://github.com/Arakne/Araknemu/issues/127
     */
    @Test
    void removeCurrentFighterShouldNotSkipFighterTurn() throws SQLException {
        PlayerFighter third = makePlayerFighter(makeSimpleGamePlayer(5));
        fight.team(0).join(third);

        turnList = new FightTurnList(fight, new AlternateTeamFighterOrder());
        turnList.start();
        assertEquals(player.fighter(), turnList.currentFighter());

        turnList.next();
        assertEquals(other.fighter(), turnList.currentFighter());

        turnList.remove(other.fighter());
        turnList.next();
        assertEquals(third, turnList.currentFighter());

        turnList.next();
        assertEquals(player.fighter(), turnList.currentFighter());
    }

    /**
     * Bug: https://github.com/Arakne/Araknemu/issues/127
     */
    @Test
    void removeFighterBeforeCurrentShouldNotSkipFighterTurn() throws SQLException {
        PlayerFighter third = makePlayerFighter(makeSimpleGamePlayer(5));
        fight.team(0).join(third);
        PlayerFighter fourth = makePlayerFighter(makeSimpleGamePlayer(6));
        fight.team(0).join(fourth);

        turnList = new FightTurnList(fight, teams -> new ArrayList<>(Arrays.asList(player.fighter(), other.fighter(), third, fourth)));
        turnList.start();
        assertEquals(player.fighter(), turnList.currentFighter());

        turnList.next();
        assertEquals(other.fighter(), turnList.currentFighter());

        turnList.next();
        turnList.remove(other.fighter());
        assertEquals(third, turnList.currentFighter());

        turnList.next();
        assertEquals(fourth, turnList.currentFighter());

        turnList.next();
        assertEquals(player.fighter(), turnList.currentFighter());
    }

    @Test
    void removeFighterNotFound() throws SQLException {
        PlayerFighter third = makePlayerFighter(makeSimpleGamePlayer(5));

        turnList.start();
        turnList.remove(third); // Do nothing

        assertCount(2, turnList.fighters());
    }

    @Test
    void add() throws NoSuchFieldException, IllegalAccessException {
        turnList.start();

        assertSame(player.fighter(), turnList.currentFighter());

        PlayableFighter f1 = Mockito.mock(PlayableFighter.class);
        PlayableFighter f2 = Mockito.mock(PlayableFighter.class);
        PlayableFighter f3 = Mockito.mock(PlayableFighter.class);

        turnList.add(f1);
        assertSame(player.fighter(), turnList.currentFighter());
        assertIterableEquals(turnList.fighters(), Arrays.asList(player.fighter(), f1, other.fighter()));

        turnList.add(f2);
        assertIterableEquals(turnList.fighters(), Arrays.asList(player.fighter(), f2, f1, other.fighter()));

        setIndex(3);
        turnList.add(f3);
        assertIterableEquals(turnList.fighters(), Arrays.asList(player.fighter(), f2, f1, other.fighter(), f3));
    }

    private void setIndex(int index) throws NoSuchFieldException, IllegalAccessException {
        Field field = FightTurnList.class.getDeclaredField("index");
        field.setAccessible(true);

        field.set(turnList, index);
    }
}
