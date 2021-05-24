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

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.admin.AdminSessionService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class AreaTest extends CommandTestCase {
    private AdminUser user;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        explorationPlayer();

        command = new Area(container.get(SpellEffectService.class));
        user = container.get(AdminSessionService.class).user(gamePlayer());

        requestStack.clear();
    }

    @Test
    void show() throws SQLException, AdminException {
        user.player().setPosition(new Position(10340, 123));

        command.execute(user, "Cb");

        requestStack.assertContains(FightStartPositions.class);
    }
}
