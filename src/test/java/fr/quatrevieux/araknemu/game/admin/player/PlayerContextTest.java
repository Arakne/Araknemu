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

import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.NullContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.game.admin.player.teleport.Goto;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerContextTest extends GameBaseCase {
    private PlayerContext context;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        context = new PlayerContext(
            gamePlayer(),
            new AccountContext(
                new NullContext(),
                gamePlayer().account(),
                container.get(AccountRepository.class)
            ),
            container.get(ItemService.class),
            container.get(GeolocationService.class),
            container.get(ExplorationMapService.class),
            container.get(PlayerService.class)
        );
    }

    @Test
    void commands() throws CommandNotFoundException {
        assertInstanceOf(Info.class, context.command("info"));
        assertInstanceOf(GetItem.class, context.command("getitem"));
        assertInstanceOf(SetLife.class, context.command("setlife"));
        assertInstanceOf(AddStats.class, context.command("addstats"));
        assertInstanceOf(AddXp.class, context.command("addxp"));
        assertInstanceOf(Restriction.class, context.command("restriction"));
        assertInstanceOf(Goto.class, context.command("goto"));

        assertContainsType(Info.class, context.commands());
        assertContainsType(GetItem.class, context.commands());
        assertContainsType(SetLife.class, context.commands());
        assertContainsType(AddStats.class, context.commands());
        assertContainsType(AddXp.class, context.commands());
        assertContainsType(Restriction.class, context.commands());
        assertContainsType(Goto.class, context.commands());
    }

    @Test
    void children() throws ContextNotFoundException {
        assertInstanceOf(AccountContext.class, context.child("account"));
    }
}
