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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LearnSpellTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new LearnSpell(gamePlayer(true), container.get(SpellService.class));
        dataSet.pushFunctionalSpells();
    }

    @Test
    void executeSuccess() throws ContainerException, SQLException, AdminException {
        execute("learnspell", "109");

        assertOutput("The spell Bluff (109) has been learned");
        assertTrue(gamePlayer().properties().spells().has(109));
        assertEquals(1, gamePlayer().properties().spells().get(109).level());
    }

    @Test
    void executeNotFound() throws ContainerException, SQLException, AdminException {
        execute("learnspell", "404");

        assertOutput("Spell 404 not found");
        assertFalse(gamePlayer().properties().spells().has(404));
    }

    @Test
    void executeCannotLearn() throws ContainerException, SQLException, AdminException {
        execute("learnspell", "157");

        assertOutput("Cannot learn spell Epée Céleste (157)");
        assertFalse(gamePlayer().properties().spells().has(157));
    }

    @Test
    void executeMissingArgument() throws ContainerException, SQLException, AdminException {
        assertThrowsWithMessage(
            CommandException.class, "Argument \"SPELLID\" is required",
            () -> execute("learnspell")
        );
    }

    @Test
    void help() {
        assertHelp(
            "learnspell - Add the given spell to a player",
            "========================================",
            "SYNOPSIS",
                "\tlearnspell SPELLID",
            "OPTIONS",
                "\tSPELLID : The spell ID to learn.",
            "EXAMPLES",
                "\t@John learnspell 366 - John will learn the spell Moon Hammer",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }
}
