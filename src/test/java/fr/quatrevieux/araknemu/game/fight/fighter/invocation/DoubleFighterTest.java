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

package fr.quatrevieux.araknemu.game.fight.fighter.invocation;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.spell.LaunchedSpells;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.States;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterHidden;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMoved;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterVisible;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighterSprite;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
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

class DoubleFighterTest extends FightBaseCase {
    private DoubleFighter fighter;
    private FightTeam team;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
            .pushMonsterSpells()
        ;

        fight = createFight();
        team = fight.team(0);

        fighter = new DoubleFighter(-5, player.fighter());
    }

    @Test
    void shouldNotInheritBuffs() {
        player.fighter().characteristics().alter(Characteristic.STRENGTH, 100);

        fighter = new DoubleFighter(-5, player.fighter());
        assertEquals(50, fighter.characteristics().get(Characteristic.STRENGTH));

        fighter.characteristics().alter(Characteristic.STRENGTH, 50);
        assertEquals(100, fighter.characteristics().get(Characteristic.STRENGTH));
        assertEquals(150, player.fighter().characteristics().get(Characteristic.STRENGTH));
    }

    @Test
    void shouldCopyLife() {
        player.fighter().init();
        player.fighter().life().alter(player.fighter(), -50);
        assertEquals(245, player.fighter().life().current());
        assertEquals(295, player.fighter().life().max());

        fighter = new DoubleFighter(-5, player.fighter());
        fighter.joinFight(fight, fight.map().get(123));
        assertEquals(245, fighter.life().current());
        assertEquals(295, fighter.life().max());

        player.fighter().life().alter(player.fighter(), -100);
        assertEquals(145, player.fighter().life().current());
        assertEquals(245, fighter.life().current());

        fighter.life().alter(fighter, -50);
        assertEquals(145, player.fighter().life().current());
        assertEquals(195, fighter.life().current());
    }

    @Test
    void values() {
        assertSame(team, fighter.team());
        assertEquals(-5, fighter.id());
        assertEquals(Direction.SOUTH_EAST, fighter.orientation());
        assertFalse(fighter.dead());
        assertFalse(fighter.closeCombat().isPresent());
        assertInstanceOf(BuffList.class, fighter.buffs());
        assertInstanceOf(States.class, fighter.states());
        assertTrue(fighter.ready());
        assertEquals(player.fighter().level(), fighter.level());
        assertSame(player.fighter(), fighter.invoker());
        assertTrue(fighter.invoked());
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

        assertInstanceOf(PlayerFighterSprite.class, fighter.sprite());
        assertEquals("123;1;0;-5;Bob;1;10^100x100;0;50;0,0,0,0;7b;1c8;315;,,,,;295;6;3;0;0;0;0;0;0;0;0;;", fighter.sprite().toString());

        fighter.move(fight.map().get(124));
        assertEquals("124;1;0;-5;Bob;1;10^100x100;0;50;0,0,0,0;7b;1c8;315;,,,,;295;6;3;0;0;0;0;0;0;0;0;;", fighter.sprite().toString());
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
    void moveShouldDispatchFighterMoved() {
        fighter.joinFight(fight, fight.map().get(123));

        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        fighter.move(fight.map().get(126));

        assertSame(fighter, ref.get().fighter());
        assertSame(fight.map().get(126), ref.get().cell());
    }

    @Test
    void moveShouldNotDispatchFighterMovedWhenNullCellIsGiven() {
        fighter.joinFight(fight, fight.map().get(123));

        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        fighter.move(null);

        assertNull(ref.get());
    }

    @Test
    void setCell() {
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
        assertIterableEquals(fighter.spells(), new ArrayList<>());
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
        Mockito.verify(operation).onDouble(fighter);
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
