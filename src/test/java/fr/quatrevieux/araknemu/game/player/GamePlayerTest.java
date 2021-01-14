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

package fr.quatrevieux.araknemu.game.player;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.common.session.SessionLogService;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerLife;
import fr.quatrevieux.araknemu.game.player.experience.PlayerExperienceService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerTest extends GameBaseCase {
    private GamePlayer player;
    private Player entity;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushRaces()
            .pushSpells()
            .use(PlayerItem.class)
            .use(PlayerSpell.class)
        ;

        MutableCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.INTELLIGENCE, 150);
        characteristics.set(Characteristic.VITALITY, 50);

        entity = dataSet.push(new Player(5, 2, 1, "Other", Race.CRA, Gender.MALE, new Colors(-1, -1, -1), 50, characteristics, new Position(10300, 308), EnumSet.allOf(ChannelType.class), 0, 0, -1, 0, new Position(10300, 308), 0));

        player = new GamePlayer(
            new GameAccount(
                new Account(2),
                container.get(AccountService.class),
                1
            ),
            entity,
            container.get(PlayerRaceService.class).get(Race.CRA),
            session,
            container.get(PlayerService.class),
            container.get(InventoryService.class).load(entity),
            container.get(SpellBookService.class).load(session, entity),
            container.get(PlayerExperienceService.class).load(session, entity)
        );
    }

    @Test
    void send() {
        player.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void characteristics() {
        assertEquals(150, player.properties().characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(3, player.properties().characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(6, player.properties().characteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void position() {
        assertEquals(new Position(10300, 308), player.position());
    }

    @Test
    void isExploring() {
        assertFalse(player.isExploring());

        session.setExploration(
            new ExplorationPlayer(player)
        );

        assertTrue(player.isExploring());
    }

    @Test
    void stopExploringSuccess() {
        ExplorationPlayer exploration = new ExplorationPlayer(player);
        player.start(exploration);
        player.stop(exploration);

        assertNull(session.exploration());
        assertFalse(player.isExploring());
        assertSame(player, player.scope());
    }

    @Test
    void stopExploringWithFightSession() {
        ExplorationPlayer exploration = new ExplorationPlayer(player);
        player.start(exploration);
        player.start(new PlayerFighter(player));
        player.stop(exploration);

        assertNull(session.exploration());
        assertFalse(player.isExploring());
        assertTrue(player.isFighting());
        assertSame(player.fighter(), player.scope());
    }

    @Test
    void explorationNotExploring() {
        assertThrows(IllegalStateException.class, () -> player.exploration(), "The current player is not an exploration state");
    }

    @Test
    void exploration() {
        ExplorationPlayer exploration = new ExplorationPlayer(player);
        player.start(exploration);

        assertSame(session.exploration(), player.exploration());
    }

    @Test
    void isFighting() {
        assertFalse(player.isFighting());

        player.start(new PlayerFighter(player));

        assertTrue(player.isFighting());
    }

    @Test
    void startAlreadyOnScope() {
        PlayerFighter fighter = new PlayerFighter(player);
        player.start(fighter);

        assertThrows(IllegalStateException.class, () -> player.start(fighter));
    }

    @Test
    void attachFighter() {
        PlayerFighter fighter = new PlayerFighter(player);
        player.start(fighter);

        assertSame(fighter, session.fighter());
        assertSame(fighter, player.fighter());
        assertSame(fighter, player.scope());
    }

    @Test
    void fighterNotInFight() {
        assertThrows(IllegalStateException.class, () -> player.fighter());
    }

    @Test
    void stopFighting() {
        PlayerFighter fighter = new PlayerFighter(player);
        player.start(fighter);
        player.stop(fighter);

        assertNull(session.fighter());
        assertFalse(player.isFighting());
        assertSame(player, player.scope());
    }

    @Test
    void registerUnregister() {
        assertNull(session.player());

        player.register(session);
        assertSame(player, session.player());

        player.unregister(session);
        assertNull(session.player());
    }

    @Test
    void save() throws Exception {
        player.setPosition(
            new Position(7894, 12)
        );

        player.entity().stats().set(Characteristic.AGILITY, 123);
        player.properties().life().set(5);
        player.properties().life().startLifeRegeneration(50);
        Thread.sleep(100);

        player.save();

        assertEquals(new Position(7894, 12), dataSet.refresh(entity).position());
        assertEquals(123, player.properties().characteristics().get(Characteristic.AGILITY));
        assertBetween(6, 8, dataSet.refresh(entity).life());
    }

    @Test
    void life() {
        assertInstanceOf(PlayerLife.class, player.properties().life());
        assertEquals(345, player.properties().life().current());
        assertEquals(345, player.properties().life().max());

        player.entity().setLife(10);
        assertEquals(10, player.properties().life().current());
    }

    @Test
    void restrictionsDefaults() {
        assertTrue(player.restrictions().canMoveAllDirections());
        assertFalse(player.restrictions().canBeMerchant());
        assertTrue(player.restrictions().canExchange());
    }

    @Test
    void savedPosition() {
        assertEquals(new Position(10300, 308), player.savedPosition());

        player.setSavedPosition(new Position(10340, 255));
        assertEquals(new Position(10340, 255), player.savedPosition());
    }

    @Test
    void isNew() {
        dataSet.use(ConnectionLog.class);
        session.attach(player.account());
        session.setLog(container.get(SessionLogService.class).load(session));

        assertTrue(player.isNew());

        ConnectionLog log = dataSet.push(new ConnectionLog(entity.accountId(), Instant.now(), ""));
        log.setEndDate(Instant.now());
        log.setPlayerId(entity.id());
        container.get(ConnectionLogRepository.class).save(log);

        assertFalse(player.isNew());
    }
}
