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

package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.characteristic.event.LifeChanged;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayerLifeTest extends GameBaseCase {
    private PlayerLife life;

    private GamePlayer player;
    private Player entity;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        life = new PlayerLife(
            player = gamePlayer(true),
            entity = container.get(PlayerRepository.class).get(new Player(player.id()))
        );

        life.init();
    }

    @Test
    void maxSimple() {
        assertEquals(295, life.max());
    }

    @Test
    void maxLifeWithStuffAndBaseVitality() throws SQLException, ContainerException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        player.properties().characteristics().base().set(Characteristic.VITALITY, 50);
        player.inventory().add(
            container.get(ItemService.class).create(2419, true),
            1, 2
        );
        player.properties().characteristics().rebuildStuffStats();

        life.rebuild();
        assertEquals(373, life.max());
    }

    @Test
    void current() {
        entity.setLife(123);

        assertEquals(123, life.current());
    }

    @Test
    void percent() {
        assertEquals(100, life.percent());

        entity.setLife(150);
        assertEquals(50, life.percent());
    }

    @Test
    void rebuildWithFullLife() {
        assertEquals(295, life.max());
        assertEquals(295, life.current());
        assertEquals(100, life.percent());

        player.properties().characteristics().base().set(Characteristic.VITALITY, 100);
        life.rebuild();

        assertEquals(395, life.max());
        assertEquals(395, life.current());
        assertEquals(100, life.percent());
    }

    @Test
    void rebuildWillKeepPercentLife() {
        entity.setLife(59);

        assertEquals(295, life.max());
        assertEquals(20, life.percent());

        player.properties().characteristics().base().set(Characteristic.VITALITY, 100);
        life.rebuild();

        assertEquals(395, life.max());
        assertEquals(79, life.current());
        assertEquals(20, life.percent());
    }

    @Test
    void initWithLifePoints() throws SQLException, ContainerException {
        entity.setLife(65);

        life = new PlayerLife(player, entity);
        life.init();

        assertEquals(65, life.current());
    }

    @Test
    void initWithMaxLifePoints() throws SQLException, ContainerException {
        entity.setLife(-1);

        life = new PlayerLife(player, entity);
        life.init();

        assertEquals(295, life.current());
    }

    @Test
    void initWithLifePointsUpperThanMax() throws SQLException, ContainerException {
        entity.setLife(10000);

        life = new PlayerLife(player, entity);
        life.init();

        assertEquals(295, life.current());
    }

    @Test
    void add() {
        entity.setLife(65);

        life.add(10);

        assertEquals(75, life.current());
    }

    @Test
    void addMoreThanRemaining() {
        entity.setLife(200);

        life.add(100);

        assertEquals(295, life.current());
    }

    @Test
    void setWillDispatchEvent() throws SQLException, ContainerException {
        AtomicReference<LifeChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(LifeChanged.class, ref::set);

        life.set(123);

        assertNotNull(ref.get());
        assertEquals(123, life.current());
        assertEquals(123, ref.get().current());
    }

    @Test
    void lifeRegenerationIsCorrect() throws Exception {
        life.set(5);
        life.startLifeRegeneration(15);
        Thread.sleep(30);
        life.stopLifeRegeneration();
        assertBetween(6, 8, life.current());
    }

    @Test
    void lifeRegenerationIsCorrectWithDifferentSpeed() throws Exception {
        life.set(5);
        life.startLifeRegeneration(10);
        Thread.sleep(90);
        life.stopLifeRegeneration();
        assertBetween(14, 16, life.current());
    }

    @Test
    void callCurrentDuringRegeneration() throws Exception {
        life.set(5);
        life.startLifeRegeneration(30);

        Thread.sleep(90);
        assertBetween(7, 9, life.current());

        Thread.sleep(90);
        life.stopLifeRegeneration();

        assertBetween(10, 12, life.current());
    }

    @Test
    void setLifeRegeneration() throws Exception {
        life.set(5);
        life.setLifeWithCurrentRegeneration();

        assertEquals(5, entity.life());

        life.startLifeRegeneration(50);
        Thread.sleep(100);
        
        life.setLifeWithCurrentRegeneration();
        assertBetween(7,8, entity.life());
    }
}
