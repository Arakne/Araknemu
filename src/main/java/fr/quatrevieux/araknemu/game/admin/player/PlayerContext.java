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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.game.admin.context.AbstractContext;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.SimpleContext;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.List;

/**
 * Context for a player
 */
public final class PlayerContext extends AbstractContext<PlayerContext> {
    private final GamePlayer player;
    private final Context accountContext;

    public PlayerContext(GamePlayer player, Context accountContext, List<AbstractContextConfigurator<PlayerContext>> configurators) {
        super(configurators);

        this.player = player;
        this.accountContext = accountContext;
    }

    @Override
    protected SimpleContext createContext() {
        return new SimpleContext(accountContext)
            .add("account", accountContext)
            .add(new Info(player))
            .add(new SetLife(player))
            .add(new AddStats(player))
            .add(new AddXp(player))
            .add(new Restriction(player))
            .add(new Save(player))
            .add(new Message(player))
            .add(new Kick(player))
        ;
    }

    /**
     * The player related to the context
     */
    public GamePlayer player() {
        return player;
    }
}
