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

import fr.quatrevieux.araknemu.data.constant.Emote;
import fr.quatrevieux.araknemu.network.game.in.emote.SetEmoteRequest;
import org.checkerframework.dataflow.qual.Pure;

/**
 * List of emotes
 */
public interface EmoteList extends Iterable<Emote> {
    /**
     * Get one emote by its id
     * {@link EmoteList#has(int)} must be called before, and returns true. If not undefined behavior can occur
     *
     * @param emoteId The emote id
     */
    public Emote get(int emoteId);

    /**
     * Check if the creature have the emote
     *
     * @param emoteId ID of the emote to check
     */
    @Pure
    public boolean has(int emoteId);
}