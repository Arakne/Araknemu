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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.NullContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.global.GlobalContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebugContextTest extends GameBaseCase {
    private DebugContext context;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        context = new DebugContext(container, new NullContext());
    }

    @Test
    void commands() throws CommandNotFoundException {
        assertInstanceOf(GenItem.class, context.command("genitem"));
        assertInstanceOf(FightPos.class, context.command("fightpos"));
        assertInstanceOf(MapStats.class, context.command("mapstats"));
        assertInstanceOf(Movement.class, context.command("movement"));
        assertInstanceOf(Area.class, context.command("area"));

        assertContainsType(GenItem.class, context.commands());
        assertContainsType(FightPos.class, context.commands());
        assertContainsType(MapStats.class, context.commands());
        assertContainsType(Movement.class, context.commands());
        assertContainsType(Area.class, context.commands());
    }
}
