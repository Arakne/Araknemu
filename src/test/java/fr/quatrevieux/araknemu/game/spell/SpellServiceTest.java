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

package fr.quatrevieux.araknemu.game.spell;

import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class SpellServiceTest extends GameBaseCase {
    private SpellService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        service = new SpellService(
            container.get(SpellTemplateRepository.class),
            container.get(SpellEffectService.class)
        );
    }

    @Test
    void get() {
        SpellLevels levels = service.get(3);

        assertEquals(3, levels.id());
        assertEquals("Attaque Naturelle", levels.name());
        assertEquals(6, levels.max());

        assertEquals(99, levels.level(2).effects().get(0).effect());
        assertEquals(3, levels.level(2).effects().get(0).min());
        assertEquals(7, levels.level(2).effects().get(0).max());
        assertEquals(5, levels.level(2).apCost());
    }

    @Test
    void preload() {
        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading spells...");
        Mockito.verify(logger).info("{} spells loaded", 5);
    }

    @Test
    void name() {
        assertEquals("spell", service.name());
    }
}
