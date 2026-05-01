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

package fr.quatrevieux.araknemu.game.listener;

import fr.quatrevieux.araknemu.data.constant.Emote;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.listener.player.emote.SendLearnedEmote;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.emote.event.EmoteLearned;
import fr.quatrevieux.araknemu.network.game.out.emote.EmoteLearnedMessage;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.game.out.emote.EmoteList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SendLearnedEmoteTest extends GameBaseCase {
    private SendLearnedEmote listener;
    private GamePlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        this.player = gamePlayer(true);
        this.listener = new SendLearnedEmote(player);

        requestStack.clear();
    }

    @Test
    void eventClass() {
        assertEquals(EmoteLearned.class, listener.event());
    }

    @Test
    void onEmoteLearned() {
        Emote emote = Emote.SIT;
        listener.on(new EmoteLearned(emote));
        requestStack.assertLast("eA"+emote.id());

        requestStack.assertAll(
            new EmoteList(player.getEmotes()),
            new EmoteLearnedMessage(emote.id())
        );
        requestStack.assertLast("eA"+emote.id());
    }
}
