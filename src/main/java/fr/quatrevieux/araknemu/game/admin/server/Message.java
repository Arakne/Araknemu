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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.game.admin.AbstractMessageCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.help.CommandHelp;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.out.chat.ServerMessage;

/**
 * Command for send a server message to all players
 */
public final class Message extends AbstractMessageCommand {
    private final PlayerService service;

    public Message(PlayerService service) {
        this.service = service;
    }

    @Override
    protected void configureHelp(CommandHelp.Builder builder) {
        builder
            .description("Send a message to all connected players")
            .synopsis("msg [options] MESSAGE")

            .example("msg Hello World !", "Send message \"Hello World\" to all players")
            .example("msg --color red My import message", "Send a message in red")
            .example("msg --color CD5C5C My message", "Send a message in custom color")
            .example("msg --color blue **Note:** go to link : [My link](https://my-site.com/my-page)", "Send a message using markdown")
        ;
    }

    @Override
    protected void send(AdminPerformer performer, ServerMessage packet) {
        service.send(packet);
    }
}
