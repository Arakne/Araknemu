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

import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.SimpleContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.Collection;

/**
 * Context for a player
 */
final public class PlayerContext implements Context {
    final private GamePlayer player;
    final private Context accountContext;
    final private ItemService itemService;

    final private Context context;

    public PlayerContext(GamePlayer player, Context accountContext, ItemService itemService) throws ContextException {
        this.player = player;
        this.accountContext = accountContext;
        this.itemService = itemService;

        context = configure();
    }

    @Override
    public Command command(String name) throws CommandNotFoundException {
        return context.command(name);
    }

    @Override
    public Collection<Command> commands() {
        return context.commands();
    }

    @Override
    public Context child(String name) throws ContextNotFoundException {
        return context.child(name);
    }

    private Context configure() throws ContextException {
        return new SimpleContext(accountContext)
            .add(new Info(player))
            .add(new GetItem(player, itemService))
            .add(new SetLife(player))
            .add(new AddStats(player))
            .add(new AddXp(player))
            .add(new Restriction(player))
            .add("account", accountContext)
        ;
    }
}
