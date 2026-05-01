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
 * Copyright (c) 2017-2026 Leor Finacre
 */

package fr.quatrevieux.araknemu.game.listener.player.emote;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.emote.event.EmoteError;
import fr.quatrevieux.araknemu.network.game.out.emote.EmoteErrorMessage;

public class SendEmoteErrorMessage implements Listener<EmoteError> {
    private final GamePlayer player;

    public SendEmoteErrorMessage(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(EmoteError event) {
        player.send(new EmoteErrorMessage());
    }

    @Override
    public Class<EmoteError> event() {
        return EmoteError.class;
    }
}
