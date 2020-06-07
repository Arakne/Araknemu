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

package fr.quatrevieux.araknemu.data.living.constraint.player;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerConstraintsTest extends GameBaseCase {
    private PlayerConstraints constraints;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        constraints = new PlayerConstraints(
            container.get(PlayerRepository.class),
            configuration.player()
        );

        dataSet.use(Player.class);
    }

    @Test
    void emptyName() {
        assertFalse(constraints.check(Player.forCreation(1, 1, "", Race.FECA, Gender.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME, constraints.error());
    }

    @Test
    void nameTooShort() {
        assertFalse(constraints.check(Player.forCreation(1, 1, "a", Race.FECA, Gender.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME, constraints.error());
    }

    @Test
    void nameTooLong() {
        assertFalse(constraints.check(Player.forCreation(1, 1, "Myverylongnamewithtoomuchcharacters", Race.FECA, Gender.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME, constraints.error());
    }

    @Test
    void nameBadFormat() {
        assertFalse(constraints.check(Player.forCreation(1, 1, "-My-invalid-name-", Race.FECA, Gender.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME, constraints.error());
    }

    @Test
    void nameAlreadyUsed() throws ContainerException {
        dataSet.push(Player.forCreation(1, 1, "My-name", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));

        assertFalse(constraints.check(Player.forCreation(1, 1, "My-name", Race.FECA, Gender.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.NAME_ALEREADY_EXISTS, constraints.error());
    }

    @Test
    void tooMuchCharacters() throws ContainerException {
        dataSet.push(Player.forCreation(1, 1, "One", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "Two", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "Three", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "Four", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "Five", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));

        assertFalse(constraints.check(Player.forCreation(1, 1, "My-name", Race.FECA, Gender.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_FULL, constraints.error());
    }
}
