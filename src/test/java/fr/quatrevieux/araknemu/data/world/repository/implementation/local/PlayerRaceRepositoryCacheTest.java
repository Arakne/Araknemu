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

package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
class PlayerRaceRepositoryCacheTest extends GameBaseCase {
    private PlayerRaceRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushRaces();

        repository = new PlayerRaceRepositoryCache(
            container.get(PlayerRaceRepository.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(Race.NO_CLASS));
    }

    @Test
    void get() {
        PlayerRace race = repository.get(Race.CRA);

        assertEquals(Race.CRA, race.race());
        assertEquals("Cra", race.name());
        assertEquals(10300, race.startPosition().map());
        assertEquals(6, race.baseStats().get(1).get(Characteristic.ACTION_POINT));
        assertEquals(7, race.baseStats().get(100).get(Characteristic.ACTION_POINT));
    }

    @Test
    void getWillReturnSameInstance() {
        assertSame(
            repository.get(Race.CRA),
            repository.get(Race.CRA)
        );
    }

    @Test
    void getWithEntity() {
        assertSame(
            repository.get(new PlayerRace(Race.CRA)),
            repository.get(Race.CRA)
        );
    }

    @Test
    void hasLoaded() {
        repository.get(Race.CRA);

        assertTrue(repository.has(new PlayerRace(Race.CRA)));
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new PlayerRace(Race.CRA)));
        assertFalse(repository.has(new PlayerRace(Race.NO_CLASS)));
    }

    @Test
    void load() {
        Collection<PlayerRace> races = repository.load();

        assertCount(12, races);

        for (PlayerRace race : races) {
            assertSame(
                race,
                repository.get(race)
            );
        }
    }
}
