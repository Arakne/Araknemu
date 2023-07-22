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

package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.spell.LaunchedSpells;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.States;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterHidden;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMoved;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterVisible;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonsterFighterTest extends FightBaseCase {
    private MonsterFighter fighter;
    private MonsterGroupTeam team;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
        ;

        MonsterService service = container.get(MonsterService.class);

        team = new MonsterGroupTeam(
            new MonsterGroup(
                new LivingMonsterGroupPosition(
                    container.get(MonsterGroupFactory.class),
                    container.get(MonsterEnvironmentService.class),
                    container.get(FightService.class),
                    new MonsterGroupData(3, Duration.ofMillis(60000), 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100), 1), new MonsterGroupData.Monster(34, new Interval(1, 100), 1), new MonsterGroupData.Monster(36, new Interval(1, 100), 1)), "", new Position(0, 0), false),
                    new RandomCellSelector(), false
                ),
                5,
                Collections.singletonList(service.load(31).all().get(2)),
                Direction.WEST,
                container.get(ExplorationMapService.class).load(10340).get(123),
                new Position(0, 0)
            ),
            Collections.singletonList(loadFightMap(10340).get(123)),
            1,
            container.get(FighterFactory.class)
        );

        fighter = (MonsterFighter) team.fighters().stream().findFirst().get();
    }

    @Test
    void values() {
        assertSame(team, fighter.team());
        assertEquals(-1, fighter.id());
        assertEquals(Direction.SOUTH_EAST, fighter.orientation());
        assertFalse(fighter.dead());
        assertThrows(FightException.class, fighter::weapon);
        assertInstanceOf(BuffList.class, fighter.buffs());
        assertInstanceOf(States.class, fighter.states());
        assertTrue(fighter.ready());
        assertEquals(4, fighter.level());
        assertInstanceOf(Monster.class, fighter.monster());
        assertNull(fighter.invoker());
        assertFalse(fighter.invoked());

        assertEquals(new Interval(50, 70), fighter.reward().kamas());
        assertEquals(12, fighter.reward().experience());
    }

    @Test
    void equals() throws SQLException {
        assertEquals(fighter, fighter);
        assertEquals(fighter.hashCode(), fighter.hashCode());
        assertNotEquals(fighter, makePlayerFighter(gamePlayer()));

        MonsterFighter other = new MonsterFighter(
            -2,
            container.get(MonsterService.class).load(36).all().get(0),
            team
        );

        assertNotEquals(fighter, other);
    }

    @Test
    void attachments() {
        fighter.attach("key", 42);
        assertSame(42, fighter.attachment("key"));
        assertSame(42, fighter.detach("key"));
        assertNull(fighter.detach(LaunchedSpells.class));
        assertNull(fighter.attachment("key"));

        LaunchedSpells launchedSpells = new LaunchedSpells();

        fighter.attach(launchedSpells);
        assertSame(launchedSpells, fighter.attachment(LaunchedSpells.class));
        assertSame(launchedSpells, fighter.detach(LaunchedSpells.class));
        assertNull(fighter.attachment(LaunchedSpells.class));
    }

    @Test
    void orientation() {
        assertEquals(Direction.SOUTH_EAST, fighter.orientation());

        fighter.setOrientation(Direction.NORTH_EAST);
        assertEquals(Direction.NORTH_EAST, fighter.orientation());
    }

    @Test
    void init() throws Exception {
        Fight fight = createFight();

        AtomicReference<FighterInitialized> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterInitialized.class, ref::set);

        fighter.joinFight(fight, fight.map().get(123));
        fighter.init();

        assertSame(fighter, ref.get().fighter());
    }

    @Test
    void dead() throws Exception {
        Fight fight = createFight();

        fighter.joinFight(fight, fight.map().get(123));
        fighter.init();
        assertFalse(fighter.dead());

        fighter.life().alter(fighter, -10000);

        assertTrue(fighter.dead());
    }

    @Test
    void joinFight() throws Exception {
        Fight fight = createFight();

        assertFalse(fighter.isOnFight());

        fighter.joinFight(fight, fight.map().get(123));

        assertSame(fight, fighter.fight());
        assertSame(fight.map().get(123), fighter.cell());
        assertSame(fighter, fighter.cell().fighter());
        assertTrue(fighter.isOnFight());
    }

    @Test
    void joinFightAlreadyJoinedShouldRaisedException() throws Exception {
        Fight fight = createFight();

        fighter.joinFight(fight, fight.map().get(123));
        assertThrows(IllegalStateException.class, () -> fighter.joinFight(fight, fight.map().get(123)));
    }

    @Test
    void sprite() throws Exception {
        Fight fight = createFight();
        fighter.joinFight(fight, fight.map().get(123));

        assertInstanceOf(MonsterFighterSprite.class, fighter.sprite());
        assertEquals("123;1;0;-1;31;-2;1563^100;3;-1;-1;-1;0,0,0,0;20;4;2;3;7;7;-7;-7;7;5;1", fighter.sprite().toString());
    }

    @Test
    void moveFirstTime() throws Exception {
        Fight fight = createFight();

        fighter.move(fight.map().get(123));

        assertSame(fight.map().get(123), fighter.cell());
        assertSame(fighter, fight.map().get(123).fighter());
    }

    @Test
    void moveWillLeaveLastCell() throws Exception {
        Fight fight = createFight();

        fighter.move(fight.map().get(123));
        fighter.move(fight.map().get(124));

        assertSame(fight.map().get(124), fighter.cell());
        assertSame(fighter, fight.map().get(124).fighter());

        assertFalse(fight.map().get(123).hasFighter());
    }

    @Test
    void moveRemoveCell() throws Exception {
        Fight fight = createFight();

        fighter.move(fight.map().get(123));
        fighter.move(null);

        assertThrows(IllegalStateException.class, fighter::cell);
        assertFalse(fight.map().get(123).hasFighter());
    }

    @Test
    void moveShouldDispatchFighterMoved() throws Exception {
        Fight fight = createFight();
        fighter.joinFight(fight, fight.map().get(123));

        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        fighter.move(fight.map().get(126));

        assertSame(fighter, ref.get().fighter());
        assertSame(fight.map().get(126), ref.get().cell());
    }

    @Test
    void moveShouldNotDispatchFighterMovedWhenNullCellIsGiven() throws Exception {
        Fight fight = createFight();
        fighter.joinFight(fight, fight.map().get(123));

        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        fighter.move(null);

        assertNull(ref.get());
    }

    @Test
    void setCell() throws Exception {
        Fight fight = createFight();
        FightMap map = fight.map();
        fighter.joinFight(fight, map.get(123));

        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        fighter.setCell(map.get(124));

        assertSame(map.get(124), fighter.cell());
        assertFalse(map.get(124).hasFighter());
        assertFalse(map.get(123).hasFighter());
        assertNull(ref.get());

        fighter.move(null);
        fighter.setCell(map.get(125));

        assertSame(map.get(125), fighter.cell());
    }

    @Test
    void spells() {
        assertIterableEquals(fighter.spells(), container.get(MonsterService.class).load(31).all().get(2).spells());
    }

    @Test
    void dispatcher() {
        Object event = new Object();

        AtomicReference<Object> ref = new AtomicReference<>();
        fighter.dispatcher().add(Object.class, ref::set);

        fighter.dispatch(event);

        assertSame(event, ref.get());
    }

    @Test
    void playStop() throws Exception {
        Fight fight = createFight();
        fighter.joinFight(fight, fight.map().get(123));

        FightTurn turn = new FightTurn(fighter, fight, Duration.ofSeconds(10));
        turn.start();

        AtomicReference<FightTurn> ref = new AtomicReference<>();
        fighter.play(turn);

        assertSame(turn, fighter.turn());
        assertTrue(fighter.isPlaying());

        fighter.perform(ref::set);
        assertSame(turn, ref.get());

        ref.set(null);
        fighter.stop();

        fighter.perform(ref::set);

        assertNull(ref.get());
        assertFalse(fighter.isPlaying());
        assertThrows(FightException.class, () -> fighter.turn());
    }

    @Test
    void apply() {
        FighterOperation operation = Mockito.mock(FighterOperation.class);

        assertSame(operation, fighter.apply(operation));
        Mockito.verify(operation).onMonster(fighter);
    }

    @Test
    void hidden() throws Exception {
        Fight fight = createFight();
        fighter.joinFight(fight, fight.map().get(123));

        final List<Object> events = new ArrayList<>();

        fight.dispatcher().add(new Listener<FighterHidden>() {
            @Override
            public void on(FighterHidden event) {
                events.add(event);
            }

            @Override
            public Class<FighterHidden> event() {
                return FighterHidden.class;
            }
        });
        fight.dispatcher().add(new Listener<FighterVisible>() {
            @Override
            public void on(FighterVisible event) {
                events.add(event);
            }

            @Override
            public Class<FighterVisible> event() {
                return FighterVisible.class;
            }
        });

        Fighter caster = Mockito.mock(Fighter.class);

        assertFalse(fighter.hidden());
        fighter.setHidden(caster, true);

        assertTrue(fighter.hidden());
        assertEquals(1, events.size());
        assertEquals(fighter, FighterHidden.class.cast(events.get(0)).fighter());
        assertEquals(caster, FighterHidden.class.cast(events.get(0)).caster());

        fighter.setHidden(caster, true);
        assertEquals(1, events.size());
        assertTrue(fighter.hidden());

        fighter.setHidden(caster, false);
        assertEquals(2, events.size());
        assertFalse(fighter.hidden());
        assertEquals(fighter, FighterVisible.class.cast(events.get(1)).fighter());
        assertEquals(caster, FighterVisible.class.cast(events.get(1)).caster());

        fighter.setHidden(caster, false);
        assertEquals(2, events.size());
        assertFalse(fighter.hidden());
    }
}
