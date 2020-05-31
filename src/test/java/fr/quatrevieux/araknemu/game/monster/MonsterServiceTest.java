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

package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.monster.reward.MonsterRewardService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class MonsterServiceTest extends GameBaseCase {
    private MonsterService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
        ;

        service = new MonsterService(
            container.get(SpellService.class),
            container.get(MonsterRewardService.class),
            container.get(MonsterTemplateRepository.class)
        );
    }

    @Test
    void load() {
        GradeSet grades = service.load(31);

        assertCount(5, grades.all());
        assertArrayEquals(
            new int[] {2, 3, 4, 5, 6},
            grades.all().stream().mapToInt(Monster::level).toArray()
        );

        Monster first = grades.all().get(0);

        assertEquals(31, first.id());
        assertEquals(Colors.DEFAULT, first.colors());
        assertEquals(1563, first.gfxId());

        assertTrue(first.spells().has(213));
        assertFalse(first.spells().has(215));
        assertEquals(1, first.spells().get(213).level());
        assertEquals(1, first.spells().get(212).level());

        assertArrayEquals(
            new int[] {212, 213},
            StreamSupport.stream(first.spells().spliterator(), false).mapToInt(Spell::id).toArray()
        );

        assertEquals(new Interval(50, 70), first.reward().kamas());
        assertEquals(3, first.reward().experience());

        assertSame(service.load(31), service.load(31));
        assertSame(service.load(34), service.load(34));
        assertNotSame(service.load(31), service.load(34));
    }

    @Test
    void loadWithoutRewards() throws SQLException {
        dataSet.pushMonsterTemplateWithoutRewards();

        GradeSet grades = service.load(400);

        assertCount(5, grades.all());
        assertArrayEquals(
            new int[] {2, 3, 4, 5, 6},
            grades.all().stream().mapToInt(Monster::level).toArray()
        );

        Monster first = grades.all().get(0);

        assertEquals(400, first.id());
        assertEquals(new Interval(0, 0), first.reward().kamas());
        assertEquals(0, first.reward().experience());
    }

    @Test
    void loadNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.load(404));
    }

    @Test
    void preload() {
        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading monsters...");
        Mockito.verify(logger).info("{} monsters loaded", 3);
    }
}
