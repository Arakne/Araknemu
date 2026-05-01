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

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.constant.Emote;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.emote.event.EmoteError;
import fr.quatrevieux.araknemu.game.player.emote.event.EmoteLearned;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public class EmoteBook implements EmoteList, Dispatcher {
    private final Dispatcher dispatcher;
    private final Player player;
    private final Set<Emote> entries = EnumSet.noneOf(Emote.class);

    @SuppressWarnings("argument")
    public EmoteBook(Dispatcher dispatcher, Player player) {
        this.dispatcher = dispatcher;
        this.player = player;
        for (Emote emote : Emote.values()) {
            if (emote != Emote.NONE && (player.emotes() & emote.bitmask()) != 0) {
                this.entries.add(emote);
            }
        }
    }

    @Override
    public Emote get(int emoteId) {
        Emote emote = Emote.fromId(emoteId);
        return entries.contains(emote) ? emote : Emote.NONE;
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    @Override
    public Iterator<Emote> iterator() {
        return entries.iterator();
    }

    /**
     * Get all available emotes
     */
    public Collection<Emote> all() {
        return entries;
    }


    /**
     * Check if the user has the emote
     *
     * @param emoteId Emote to check
     */
    @Override
    public boolean has(int emoteId) {
        return entries.contains(Emote.fromId(emoteId));
    }

    /**
     * Check if the player can learn the emote
     *
     * @param emote Emote to learn
     */
    public boolean canLearn(Emote emote) {
        return !entries.contains(emote);
    }

    /**
     * Learn an emote
     */
    public void learn(Emote emoteToLearn) {
        if (!canLearn(emoteToLearn)) {
            throw new IllegalArgumentException("Cannot learn the emote " + emoteToLearn.name() + " (" + emoteToLearn.id() + ")");
        }
        player.setEmotes(Math.max(player.emotes() | emoteToLearn.bitmask(), 0));
        entries.add(emoteToLearn);
        dispatch(new EmoteLearned(emoteToLearn));
    }

    /**
     * Return the raw data of emotes known for a player
     */
    public long raw() {
        return player.emotes();
        }
}
