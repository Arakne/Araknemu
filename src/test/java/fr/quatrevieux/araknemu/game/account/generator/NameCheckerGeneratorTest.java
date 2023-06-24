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

package fr.quatrevieux.araknemu.game.account.generator;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NameCheckerGeneratorTest extends GameBaseCase {
    private NameCheckerGenerator nameCheckerGenerator;
    private NameGenerator inner;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushPlayer("Bob", 2, 2);
        dataSet.pushPlayer("Robert", 2, 1);
        dataSet.pushPlayer("Albert", 2, 2);

        inner = Mockito.mock(NameGenerator.class);
        nameCheckerGenerator = new NameCheckerGenerator(
            inner,
            container.get(PlayerRepository.class),
            container.get(GameConfiguration.class)
        );
    }

    @Test
    void cannotGenerateNameAlreadyUsed() throws NameGenerationException {
        Mockito.when(inner.generate()).thenReturn("Bob");

        assertThrows(NameGenerationException.class, () -> nameCheckerGenerator.generate(), "Reach the maximum try number");
    }

    @Test
    void generateFreeName() throws NameGenerationException {
        Mockito.when(inner.generate()).thenReturn("Jean");

        assertEquals("Jean", nameCheckerGenerator.generate());
    }

    @Test
    void generateWith2Tries() throws NameGenerationException {
        Mockito.when(inner.generate()).thenReturn("Bob", "Robert");

        assertEquals("Robert", nameCheckerGenerator.generate());
    }
}
