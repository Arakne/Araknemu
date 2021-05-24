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
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GetItemTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        command = new GetItem(
            gamePlayer(),
            container.get(ItemService.class)
        );
    }

    @Test
    void executeSimple() throws ContainerException, SQLException, AdminException, ItemNotFoundException {
        execute("getitem", "2425");

        assertOutputContains("Generate 1 'Amulette du Bouftou'");

        InventoryEntry entry = gamePlayer().inventory().get(1);

        assertEquals(2425, entry.templateId());
        assertEquals(1, entry.quantity());
    }

    @Test
    void executeWithQuantity() throws ContainerException, SQLException, AdminException, ItemNotFoundException {
        execute("getitem", "2425", "5");

        assertOutputContains("Generate 5 'Amulette du Bouftou'");

        InventoryEntry entry = gamePlayer().inventory().get(1);

        assertEquals(2425, entry.templateId());
        assertEquals(5, entry.quantity());
    }

    @Test
    void executeWithMaxStats() throws ContainerException, SQLException, AdminException, ItemNotFoundException {
        execute("getitem", "--max", "2425");

        assertOutputContains("Generate 1 'Amulette du Bouftou'");

        InventoryEntry entry = gamePlayer().inventory().get(1);

        assertEquals(2425, entry.templateId());

        Wearable wearable = (Wearable) entry.item();

        assertEquals(10, wearable.characteristics().get(0).value());
        assertEquals(10, wearable.characteristics().get(1).value());
    }

    @Test
    void executeEach() throws ContainerException, SQLException, AdminException, ItemNotFoundException {
        execute("getitem", "--each", "2425", "5");

        assertOutputContains("Generate 1 'Amulette du Bouftou'");

        for (int i = 1; i <= 5; ++i) {
            InventoryEntry entry = gamePlayer().inventory().get(i);

            assertEquals(2425, entry.templateId());
            assertEquals(1, entry.quantity());
        }
    }

    @Test
    void executeWithItemTemplateEffects() throws ContainerException, SQLException, AdminException, ItemNotFoundException {
        execute("getitem", "--effects", "5b#1#32#0#,5c#1#32#0#,5d#1#32#0#,5e#1#32#0#,5f#1#32#0#", "40");

        assertOutputContains("Generate 1 'Petite Epée de Boisaille'");

        InventoryEntry entry = gamePlayer().inventory().get(1);

        assertEquals(40, entry.templateId());

        Weapon weapon = (Weapon) entry.item();

        assertEquals(50, weapon.weaponEffects().get(0).max());
        assertEquals(50, weapon.weaponEffects().get(1).max());
        assertEquals(50, weapon.weaponEffects().get(2).max());
        assertEquals(50, weapon.weaponEffects().get(3).max());
        assertEquals(50, weapon.weaponEffects().get(4).max());

        assertEquals(Effect.STOLEN_WATER, weapon.weaponEffects().get(0).effect());
        assertEquals(Effect.STOLEN_EARTH, weapon.weaponEffects().get(1).effect());
        assertEquals(Effect.STOLEN_WIND, weapon.weaponEffects().get(2).effect());
        assertEquals(Effect.STOLEN_FIRE, weapon.weaponEffects().get(3).effect());
        assertEquals(Effect.STOLEN_NEUTRAL, weapon.weaponEffects().get(4).effect());
    }

    @Test
    void executeWithSimplifiedEffects() throws ContainerException, SQLException, AdminException, ItemNotFoundException {
        execute("getitem", "--effects", "STOLEN_WATER:1:50,STOLEN_EARTH:1:50,STOLEN_WIND:1:50,STOLEN_FIRE:1:50,STOLEN_NEUTRAL:1:50", "40");

        assertOutputContains("Generate 1 'Petite Epée de Boisaille'");

        InventoryEntry entry = gamePlayer().inventory().get(1);

        assertEquals(40, entry.templateId());

        Weapon weapon = (Weapon) entry.item();

        assertEquals(50, weapon.weaponEffects().get(0).max());
        assertEquals(50, weapon.weaponEffects().get(1).max());
        assertEquals(50, weapon.weaponEffects().get(2).max());
        assertEquals(50, weapon.weaponEffects().get(3).max());
        assertEquals(50, weapon.weaponEffects().get(4).max());

        assertEquals(Effect.STOLEN_WATER, weapon.weaponEffects().get(0).effect());
        assertEquals(Effect.STOLEN_EARTH, weapon.weaponEffects().get(1).effect());
        assertEquals(Effect.STOLEN_WIND, weapon.weaponEffects().get(2).effect());
        assertEquals(Effect.STOLEN_FIRE, weapon.weaponEffects().get(3).effect());
        assertEquals(Effect.STOLEN_NEUTRAL, weapon.weaponEffects().get(4).effect());
    }

    @Test
    void errorMissingItemId() {
        assertThrows(CommandException.class, () -> execute("getitem", "--max"), "Missing argument item_id");
    }

    @Test
    void errorBadOption() {
        assertThrows(CommandException.class, () -> execute("getitem", "--bad_option"), "Undefined option bad_option");
    }

    @Test
    void errorBadEffect() {
        assertThrows(CommandException.class, () -> execute("getitem", "--effects", "BAD_EFFECT:5", "2425"), "Undefined effect BAD_EFFECT");
    }

    @Test
    void help() {
        assertHelp(
            "getitem - Add an item to the player",
            "========================================",
            "SYNOPSIS",
                "\tgetitem ITEM_ID [QUANTITY=1] [--each] [--effects effects,...] [--max]",
            "OPTIONS",
                "\tITEM_ID : The id of the item to generate. It can be found using /ui itemsummoner command",
                "\tQUANTITY : The quantity of item to generate. By default all generated items will gets the same characteristics unless --each option is used.",
                "\t--each : Regenerate item stats for each QUANTITY items instead of generate the same item with QUANTITY",
                "\t--effects : Set the item effects",
                    "\t\tThe effects should be a list of effects separated with comma ','. Available formats :",
                    "\t\tItem template format : 64#b#f#0#1d5+10,7d#b#0#0#0d0+11,9a#f#0#0#0d0+15",
                    "\t\tSimplified format    : INFLICT_DAMAGE_NEUTRAL:11:15,ADD_VITALITY:11,SUB_AGILITY:15",
                    "\t\tThis option is not compatible with --max option.",
                    "\t\tIf a range value is set for a characteristic effect, a random value will be generated",
                "\t--max : Generate item with maximized characteristics",
            "EXAMPLES",
                "\tgetitem 2425 - Generate a random 'Amulette du Bouftou'",
                "\t!getitem 2425 3 - Generate 3 random 'Amulette du Bouftou', and ensure that the admin user is the target",
                "\t${player:Robert} getitem 39 - Add to Robert the 'Petite Amulette du Hibou'",
                "\tgetitem --max 2425 - Generate an 'Amulette du Bouftou' with max stats",
                "\tgetitem --effects 5b#1#32#0#,5c#1#32#0#,5d#1#32#0#,5e#1#32#0#,5f#1#32#0# 40 - Cheated 'Petite Epée de Boisaille'",
                "\tgetitem --effects STOLEN_WATER:1:50,STOLEN_EARTH:1:50,STOLEN_WIND:1:50,STOLEN_FIRE:1:50,STOLEN_NEUTRAL:1:50 40 - Same as above",
            "SEE ALSO",
                "\t/ui itemsummoner - Show the item picker",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }
}
