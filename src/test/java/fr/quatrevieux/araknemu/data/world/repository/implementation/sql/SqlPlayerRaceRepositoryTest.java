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

package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.transformer.BoostStatsDataTransformer;
import fr.quatrevieux.araknemu.data.world.transformer.RaceBaseStatsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SqlPlayerRaceRepositoryTest extends GameBaseCase {
    private SqlPlayerRaceRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushRaces();

        repository = new SqlPlayerRaceRepository(
            new ConnectionPoolExecutor(app.database().get("game")),
            new RaceBaseStatsTransformer(new ImmutableCharacteristicsTransformer()),
            new BoostStatsDataTransformer()
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(Race.NO_CLASS));
    }

    @Test
    void getFound() {
        PlayerRace race = repository.get(Race.FECA);

        assertSame(Race.FECA, race.race());
        assertEquals("Feca", race.name());
        assertContainsOnly(DefaultCharacteristics.class, race.baseStats().values());
        assertEquals(6, race.baseStats().get(1).get(Characteristic.ACTION_POINT));
        assertEquals(new Position(10300, 320), race.startPosition());
        assertEquals(new Position(10340, 250), race.astrubPosition());
        assertEquals(2, race.boostStats().get(Characteristic.STRENGTH, 15).cost());
        assertEquals(100, race.startDiscernment());
        assertEquals(1000, race.startPods());
        assertEquals(50, race.startLife());
        assertEquals(5, race.perLevelLife());
        assertArrayEquals(new int[] {3, 6, 17}, race.spells());
    }

    @Test
    void getWithPlayerRaceEntity() {
        assertEquals(
            Race.FECA,
            repository.get(
                new PlayerRace(Race.FECA)
            ).race()
        );
    }

    @Test
    void has() {
        assertTrue(repository.has(new PlayerRace(Race.FECA)));
        assertFalse(repository.has(new PlayerRace(Race.NO_CLASS)));
    }

    @Test
    void load() {
        Collection<PlayerRace> loaded = repository.load();

        assertCount(12, loaded);
    }
}
