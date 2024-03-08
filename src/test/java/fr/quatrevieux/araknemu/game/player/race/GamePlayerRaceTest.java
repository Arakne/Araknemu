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

package fr.quatrevieux.araknemu.game.player.race;

import fr.arakne.utils.value.Interval;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GamePlayerRaceTest extends GameBaseCase {
    private PlayerRaceService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushRaces()
            .pushSpells()
        ;

        service = container.get(PlayerRaceService.class);
    }

    @Test
    void life() {
        GamePlayerRace race = service.get(Race.CRA);

        assertEquals(50, race.life(1));
        assertEquals(55, race.life(2));
        assertEquals(795, race.life(150));
    }

    @Test
    void initiativeBase() {
        GamePlayerRace race = service.get(Race.CRA);

        assertEquals(100, race.initiative(400));
    }

    @Test
    void initiativeSacri() {
        GamePlayerRace race = service.get(Race.SACRIEUR);

        assertEquals(50, race.initiative(400));
    }

    @Test
    void spells() {
        GamePlayerRace race = service.get(Race.FECA);

        List<SpellLevels> levels = new ArrayList<>(race.spells());

        assertEquals(3, levels.get(0).id());
        assertEquals(6, levels.get(1).id());
        assertEquals(17, levels.get(2).id());
    }

    @Test
    void getters() {
        GamePlayerRace race = service.get(Race.ENUTROF);

        assertEquals(Race.ENUTROF, race.race());
        assertEquals("Enutrof", race.name());
        assertEquals(120, race.startDiscernment());
        assertEquals(1000, race.startPods());
    }

    @Test
    void baseStatsLowLevel() {
        GamePlayerRace race = service.get(Race.ENUTROF);

        Characteristics characteristics = race.baseStats(1);

        assertEquals(6, characteristics.get(Characteristic.ACTION_POINT));
        assertEquals(3, characteristics.get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, characteristics.get(Characteristic.MAX_SUMMONED_CREATURES));

        assertSame(characteristics, race.baseStats(50));
    }

    @Test
    void baseStatsHighLevel() {
        GamePlayerRace race = service.get(Race.ENUTROF);

        Characteristics characteristics = race.baseStats(100);

        assertEquals(7, characteristics.get(Characteristic.ACTION_POINT));
        assertEquals(3, characteristics.get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, characteristics.get(Characteristic.MAX_SUMMONED_CREATURES));

        assertSame(characteristics, race.baseStats(150));
    }

    @Test
    void astrubPosition() {
        assertEquals(new Position(10340, 250), service.get(Race.ENUTROF).astrubPosition());
    }

    @Test
    void closeCombat() {
        GamePlayerRace race = service.get(Race.ENUTROF);

        assertEquals("DefaultCloseCombat{}", race.closeCombat().toString());

        assertCount(1, race.closeCombat().effects());
        assertCount(1, race.closeCombat().criticalEffects());

        assertEquals(100, race.closeCombat().effects().get(0).effect());
        assertEquals(2, race.closeCombat().effects().get(0).min());
        assertEquals(6, race.closeCombat().effects().get(0).max());
        assertEquals(SpellEffectTarget.DEFAULT, race.closeCombat().effects().get(0).target());
        assertInstanceOf(CellArea.class, race.closeCombat().effects().get(0).area());

        assertEquals(100, race.closeCombat().criticalEffects().get(0).effect());
        assertEquals(5, race.closeCombat().criticalEffects().get(0).min());
        assertEquals(9, race.closeCombat().criticalEffects().get(0).max());
        assertEquals(SpellEffectTarget.DEFAULT, race.closeCombat().criticalEffects().get(0).target());
        assertInstanceOf(CellArea.class, race.closeCombat().criticalEffects().get(0).area());

        assertEquals(4, race.closeCombat().apCost());
        assertEquals(20, race.closeCombat().criticalHit());
        assertEquals(50, race.closeCombat().criticalFailure());
        assertFalse(race.closeCombat().modifiableRange());
        assertFalse(race.closeCombat().constraints().freeCell());
        assertFalse(race.closeCombat().constraints().lineLaunch());
        assertTrue(race.closeCombat().constraints().lineOfSight());
        assertEquals(Interval.of(1), race.closeCombat().constraints().range());
        assertEquals(0, race.closeCombat().constraints().launchDelay());
        assertEquals(0, race.closeCombat().constraints().launchPerTarget());
        assertEquals(0, race.closeCombat().constraints().launchPerTurn());
        assertArrayEquals(new int[0], race.closeCombat().constraints().requiredStates());
        assertArrayEquals(new int[] {18, 19, 3, 1, 41}, race.closeCombat().constraints().forbiddenStates());
    }

    @Test
    void weaponAbility() throws SQLException {
        dataSet.pushItemTypes();
        ItemTypeRepository itemTypeRepository = container.get(ItemTypeRepository.class);
        GamePlayerRace race = service.get(Race.ENUTROF);

        assertEquals(90, race.weaponAbility(itemTypeRepository.get(3))); // Wand
        assertEquals(95, race.weaponAbility(itemTypeRepository.get(7))); // Hammer
        assertEquals(100, race.weaponAbility(itemTypeRepository.get(8))); // Shovel
    }
}
