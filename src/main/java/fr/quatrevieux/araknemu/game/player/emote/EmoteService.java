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

package fr.quatrevieux.araknemu.game.player.emote;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.listener.player.emote.AddDefaultEmote;
import fr.quatrevieux.araknemu.game.listener.player.emote.SendEmoteErrorMessage;
import fr.quatrevieux.araknemu.game.listener.player.emote.SendEmoteList;
import fr.quatrevieux.araknemu.game.listener.player.emote.SendLearnedEmote;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;

public final class EmoteService implements EventsSubscriber {

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    event.player().dispatcher().add(new SendEmoteList(event.player()));
                    event.player().dispatcher().add(new SendLearnedEmote(event.player()));
                    event.player().dispatcher().add(new SendEmoteErrorMessage(event.player()));
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            },
            new AddDefaultEmote()
        };
    }
}
