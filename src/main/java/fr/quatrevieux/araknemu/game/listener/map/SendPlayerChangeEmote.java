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
package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.emote.event.EmoteChanged;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.out.emote.PlayerEmote;

public class SendPlayerChangeEmote implements Listener<EmoteChanged> {
    private final ExplorationMap map;

    public SendPlayerChangeEmote(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(EmoteChanged event) {
        map.send(new PlayerEmote(event.player(), event.getEmote(), event.isEmoteActivated()));
    }

    @Override
    public Class<EmoteChanged> event() {
        return EmoteChanged.class;
    }
}
