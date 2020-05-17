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

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

class GenItemTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        command = new GenItem(container.get(ItemService.class));
    }

    @Test
    void executeSimple() throws ContainerException, SQLException, AdminException {
        execute("genitem", "284");

        assertOutput("Generate item Sel (284) : Resource");
    }

    @Test
    void executeMax() throws ContainerException, SQLException, AdminException {
        execute("genitem", "--max", "39");

        assertOutput(
            "Generate item Petite Amulette du Hibou (39) : Wearable",
            "Effects :",
            "===========================",
            "CharacteristicEffect{ADD_INTELLIGENCE:2}",
            "==========================="
        );
    }

    @Test
    void executeRealUser() throws ContainerException, SQLException, AdminException {
        command.execute(user(), new CommandParser.Arguments("genitem 39", "", command.name(), Arrays.asList("genitem", "39"), user().context().current()));

        requestStack.assertLast(
            new MessageSent(
                gamePlayer(),
                ChannelType.ADMIN,
                "Generated item : °0",
                "39!7e#2#0#0#0d0+2"
            )
        );
    }
}
