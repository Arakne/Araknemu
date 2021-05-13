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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddStatsTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new AddStats(gamePlayer(true));
    }

    @Test
    void executeSuccess() throws ContainerException, SQLException, AdminException {
        execute("addstats", "vitality", "100");

        assertEquals(395, gamePlayer().properties().life().max());
        assertOutput("Characteristic changed for Bob : VITALITY = 100");
    }

    @Test
    void executeMissingValue() throws ContainerException, SQLException, AdminException {
        assertThrowsWithMessage(
            CommandException.class, "Argument \"VALUE\" is required",
            () -> execute("addstats", "vitality")
        );
    }

    @Test
    void executeMissingCharacteristic() throws ContainerException, SQLException, AdminException {
        assertThrowsWithMessage(
            CommandException.class, "Argument \"CHARACTERISTIC\" is required",
            () -> execute("addstats")
        );
    }

    @Test
    void executeInvalidCharacteristic() throws ContainerException, SQLException, AdminException {
        assertThrowsWithMessage(
            CommandException.class, "Invalid value invalid for argument \"CHARACTERISTIC\". Available values : [action-point, movement-point, strength, vitality, wisdom, luck, agility, intelligence, sight-boost, max-summoned-creatures, fixed-damage, physical-damage, weapon-master, percent-damage, health-boost, trap-boost, percent-trap-boost, counter-damage, critical-bonus, fail-malus, resistance-action-point, resistance-movement-point, resistance-neutral, resistance-percent-neutral, resistance-pvp-neutral, resistance-percent-pvp-neutral, resistance-earth, resistance-percent-earth, resistance-pvp-earth, resistance-percent-pvp-earth, resistance-water, resistance-percent-water, resistance-pvp-water, resistance-percent-pvp-water, resistance-air, resistance-percent-air, resistance-pvp-air, resistance-percent-pvp-air, resistance-fire, resistance-percent-fire, resistance-pvp-fire, resistance-percent-pvp-fire]",
            () -> execute("addstats", "invalid", "100")
        );
    }

    @Test
    void help() {
        assertHelp(
            "addstats - Add stats to a player",
            "========================================",
            "SYNOPSIS",
                "\taddstats CHARACTERISTIC VALUE",
            "OPTIONS",
                "\tCHARACTERISTIC : The characteristic to add.",
                    "\t\tThis parameter is case insensitive.",
                    "\t\tIt's value must be one of those : [ACTION_POINT, MOVEMENT_POINT, STRENGTH, VITALITY, WISDOM, LUCK, AGILITY, INTELLIGENCE, SIGHT_BOOST, MAX_SUMMONED_CREATURES, FIXED_DAMAGE, PHYSICAL_DAMAGE, WEAPON_MASTER, PERCENT_DAMAGE, HEALTH_BOOST, TRAP_BOOST, PERCENT_TRAP_BOOST, COUNTER_DAMAGE, CRITICAL_BONUS, FAIL_MALUS, RESISTANCE_ACTION_POINT, RESISTANCE_MOVEMENT_POINT, RESISTANCE_NEUTRAL, RESISTANCE_PERCENT_NEUTRAL, RESISTANCE_PVP_NEUTRAL, RESISTANCE_PERCENT_PVP_NEUTRAL, RESISTANCE_EARTH, RESISTANCE_PERCENT_EARTH, RESISTANCE_PVP_EARTH, RESISTANCE_PERCENT_PVP_EARTH, RESISTANCE_WATER, RESISTANCE_PERCENT_WATER, RESISTANCE_PVP_WATER, RESISTANCE_PERCENT_PVP_WATER, RESISTANCE_AIR, RESISTANCE_PERCENT_AIR, RESISTANCE_PVP_AIR, RESISTANCE_PERCENT_PVP_AIR, RESISTANCE_FIRE, RESISTANCE_PERCENT_FIRE, RESISTANCE_PVP_FIRE, RESISTANCE_PERCENT_PVP_FIRE]",
                "\tVALUE : The value to add, must be an integer. Negative values are allowed, but be careful with negative vitality !!!",
            "EXAMPLES",
                "\taddstats vitality 150 - Add 150 vitality to current player",
                "\t${player:John} addstats strength 50 - Add 50 strength to current John",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }
}
