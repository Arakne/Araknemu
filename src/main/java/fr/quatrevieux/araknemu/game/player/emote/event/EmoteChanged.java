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

package fr.quatrevieux.araknemu.game.player.emote.event;

import fr.quatrevieux.araknemu.data.constant.Emote;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Event for emotes
 */
public final class EmoteChanged {
    private final ExplorationPlayer player;
    private final boolean emoteActivated;
    private final Emote emote;

    public int getEmoteId() {
        return emote.id();
    }

    public boolean isEmoteActivated() {
        return emoteActivated;
    }

    public Emote getEmote() {
        return emote;
    }

    public EmoteChanged(ExplorationPlayer player, Emote emote, boolean emoteActivated) {
        this.player = player;
        this.emoteActivated = emoteActivated;
        this.emote = emote;
    }

    public ExplorationPlayer player() {
        return player;
    }
}
