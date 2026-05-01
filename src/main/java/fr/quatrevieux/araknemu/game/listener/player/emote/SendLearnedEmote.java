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
import fr.quatrevieux.araknemu.game.player.emote.event.EmoteLearned;
import fr.quatrevieux.araknemu.network.game.out.emote.EmoteLearnedMessage;
import fr.quatrevieux.araknemu.network.game.out.emote.EmoteList;

public class SendLearnedEmote implements Listener<EmoteLearned> {
    private final GamePlayer player;

    public SendLearnedEmote(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(EmoteLearned event) {
        player.send(new EmoteList(player.getEmotes()));
        player.send(new EmoteLearnedMessage(event.getEmoteLearned().id()));
    }

    @Override
    public Class<EmoteLearned> event() {
        return EmoteLearned.class;
    }
}
