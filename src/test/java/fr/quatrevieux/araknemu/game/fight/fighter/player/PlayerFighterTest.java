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

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.spell.LaunchedSpells;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.States;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighterProperties;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighterSprite;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

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
    void sprite() {
        assertInstanceOf(PlayerFighterSprite.class, fighter.sprite());
    }

    @Test
    void team() {
        FightTeam team = new SimpleTeam(fighter, new ArrayList<>(), 0);
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
        assertSame(fighter, fighter.cell().fighter().get());
        assertTrue(fighter.isOnFight());
    }

    @Test
    void moveFirstTime() {
        fighter.move(map.get(123));

        assertSame(map.get(123), fighter.cell());
        assertSame(fighter, map.get(123).fighter().get());
    }

    @Test
    void moveWillLeaveLastCell() {
        fighter.move(map.get(123));
        fighter.move(map.get(124));

        assertSame(map.get(124), fighter.cell());
        assertSame(fighter, map.get(124).fighter().get());

        assertFalse(map.get(123).fighter().isPresent());
    }

    @Test
    void moveRemoveCell() {
        fighter.move(map.get(123));
        fighter.move(null);

        assertNull(fighter.cell());
        assertFalse(map.get(123).fighter().isPresent());
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
    }

    @Test
    void playAndStop() {
        FightTurn turn = new FightTurn(fighter, fight, Duration.ofSeconds(10));
        turn.start();

        AtomicReference<FightTurn> ref = new AtomicReference<>();
        fighter.play(turn);

        assertSame(turn, fighter.turn());
        fighter.perform(ref::set);
        assertSame(turn, ref.get());

        ref.set(null);
        fighter.stop();

        fighter.perform(ref::set);

        assertNull(ref.get());
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
        assertThrows(FightException.class, () -> fighter.weapon());
    }

    @Test
    void weaponEquiped() throws ContainerException, SQLException, InventoryException {
        equipWeapon(player);

        CastableWeapon weapon = fighter.weapon();

        assertEquals(4, weapon.apCost());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(7, weapon.effects().get(0).max());
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

        LaunchedSpells launchedSpells = new LaunchedSpells();

        fighter.attach(launchedSpells);
        assertSame(launchedSpells, fighter.attachment(LaunchedSpells.class));
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
}
