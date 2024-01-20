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

package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.spell.LaunchedSpells;
import fr.quatrevieux.araknemu.game.fight.castable.closeCombat.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.States;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterHidden;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMoved;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterVisible;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerFighterTest extends FightBaseCase {
    private PlayerFighter fighter;
    private FightMap map;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        fight = createFight();
        fighter = new PlayerFighter(
            gamePlayer(true)
        );

        map = container.get(FightService.class).map(
            container.get(ExplorationMapService.class).load(10340)
        );
    }

    @Test
    void player() throws SQLException, ContainerException {
        assertSame(gamePlayer(), fighter.player());
    }

    @Test
    void invoker() {
        assertNull(fighter.invoker());
        assertFalse(fighter.invoked());
    }

    @Test
    void sprite() {
        assertInstanceOf(PlayerFighterSprite.class, fighter.sprite());
    }

    @Test
    void team() throws Exception {
        FightTeam team = new SimpleTeam(fight, fighter, new ArrayList<>(), 0);
        fighter.setTeam(team);

        assertSame(team, fighter.team());
    }

    @Test
    void fight() {
        fighter.joinFight(fight, map.get(123));

        assertSame(fight, fighter.fight());
    }

    @Test
    void joinFight() {
        assertFalse(fighter.isOnFight());

        fighter.joinFight(fight, map.get(123));

        assertSame(fight, fighter.fight());
        assertSame(map.get(123), fighter.cell());
        assertSame(fighter, fighter.cell().fighter());
        assertTrue(fighter.isOnFight());
    }

    @Test
    void moveFirstTime() {
        fighter.move(map.get(123));

        assertSame(map.get(123), fighter.cell());
        assertSame(fighter, map.get(123).fighter());
    }

    @Test
    void moveShouldDispatchFighterMoved() {
        fighter.joinFight(fight, map.get(123));

        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        fighter.move(map.get(125));

        assertSame(fighter, ref.get().fighter());
        assertSame(map.get(125), ref.get().cell());
    }

    @Test
    void moveWillLeaveLastCell() {
        fighter.move(map.get(123));
        fighter.move(map.get(124));

        assertSame(map.get(124), fighter.cell());
        assertSame(fighter, map.get(124).fighter());

        assertFalse(map.get(123).hasFighter());
    }

    @Test
    void setCell() {
        fighter.joinFight(fight, map.get(123));

        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        fighter.setCell(map.get(124));

        assertSame(map.get(124), fighter.cell());
        assertFalse(map.get(124).hasFighter());
        assertFalse(map.get(123).hasFighter());
        assertNull(ref.get());

        fighter.cell().removeFighter(fighter);
        fighter.setCell(map.get(125));

        assertSame(map.get(125), fighter.cell());
    }

    @Test
    void send() {
        fighter.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void setReady() {
        fighter.joinFight(fight, map.get(123));

        AtomicReference<FighterReadyStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterReadyStateChanged.class, ref::set);

        fighter.setReady(true);
        assertTrue(fighter.ready());
        assertSame(fighter, ref.get().fighter());
        assertTrue(ref.get().ready());
    }

    @Test
    void unsetReady() {
        fighter.joinFight(fight, map.get(123));
        fighter.setReady(true);

        AtomicReference<FighterReadyStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterReadyStateChanged.class, ref::set);

        fighter.setReady(false);
        assertFalse(fighter.ready());
        assertSame(fighter, ref.get().fighter());
        assertFalse(ref.get().ready());
    }

    @Test
    void turnNotSet() {
        assertThrows(FightException.class, () -> fighter.turn());
        assertFalse(fighter.isPlaying());
    }

    @Test
    void playAndStop() {
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
    void life() throws SQLException, ContainerException {
        assertEquals(gamePlayer().properties().life().current(), fighter.life().current());
        assertEquals(gamePlayer().properties().life().max(), fighter.life().max());
    }

    @Test
    void lifeAfterInitIsNotSyncWithPlayer() throws SQLException, ContainerException {
        gamePlayer().properties().life().set(100);
        assertEquals(100, fighter.life().current());
        fighter.joinFight(fight, map.get(123));
        fighter.init();

        gamePlayer().properties().life().set(120);
        assertEquals(100, fighter.life().current());
    }

    @Test
    void dead() {
        fighter.joinFight(fight, map.get(123));
        fighter.init();
        assertFalse(fighter.dead());

        fighter.life().alter(fighter, -10000);

        assertTrue(fighter.dead());
    }

    @Test
    void characteristics() throws SQLException, ContainerException {
        assertEquals(gamePlayer().properties().characteristics().initiative(), fighter.characteristics().initiative());
        assertEquals(gamePlayer().properties().characteristics().get(Characteristic.ACTION_POINT), fighter.characteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void weaponNoWeapon() {
        Castable weapon = fighter.closeCombat().get();

        assertSame(weapon, fighter.player().race().closeCombat());
        assertEquals(4, weapon.apCost());
        assertEquals(2, weapon.effects().get(0).min());
        assertEquals(6, weapon.effects().get(0).max());
    }

    @Test
    void weaponEquiped() throws ContainerException, SQLException, InventoryException {
        equipWeapon(player);

        Castable weapon = fighter.closeCombat().get();

        assertInstanceOf(CastableWeapon.class, weapon);
        assertEquals(4, weapon.apCost());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(6, weapon.effects().get(0).max());
        assertEquals(90, CastableWeapon.class.cast(weapon).ability());
    }

    @Test
    void weaponEquipedWithMaximumAbility() throws ContainerException, SQLException, InventoryException {
        equipWeapon(player, 138);

        Castable weapon = fighter.closeCombat().get();

        assertInstanceOf(CastableWeapon.class, weapon);
        assertEquals(4, weapon.apCost());
        assertEquals(3, weapon.effects().get(0).min());
        assertEquals(7, weapon.effects().get(0).max());
        assertEquals(100, CastableWeapon.class.cast(weapon).ability());
    }

    @Test
    void buffs() {
        assertInstanceOf(BuffList.class, fighter.buffs());
    }

    @Test
    void properties() {
        assertInstanceOf(PlayerFighterProperties.class, fighter.properties());
    }

    @Test
    void states() {
        assertInstanceOf(States.class, fighter.states());
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
    void init() {
        AtomicReference<FighterInitialized> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterInitialized.class, ref::set);

        fighter.joinFight(fight, map.get(123));
        fighter.init();

        assertSame(fighter, ref.get().fighter());
    }

    @Test
    void notInFight() {
        assertThrows(IllegalStateException.class, () -> fighter.fight());
        assertThrows(IllegalStateException.class, () -> fighter.init());
        assertThrows(IllegalStateException.class, () -> fighter.team());
    }

    @Test
    void registerUnregister() {
        fighter.register(session);
        assertSame(fighter, session.fighter());

        fighter.unregister(session);
        assertNull(session.fighter());
    }

    @Test
    void orientation() {
        assertEquals(Direction.SOUTH_EAST, fighter.orientation());

        fighter.setOrientation(Direction.NORTH_EAST);
        assertEquals(Direction.NORTH_EAST, fighter.orientation());
    }

    @Test
    void apply() {
        FighterOperation operation = Mockito.mock(FighterOperation.class);

        assertSame(operation, fighter.apply(operation));
        Mockito.verify(operation).onPlayer(fighter);
    }

    @Test
    void hidden() {
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
