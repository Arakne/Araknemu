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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.spell.boost.SimpleSpellsBoosts;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 * Decorate base spell list for handle fighter spell boosts
 */
public final class BaseFighterSpellList implements FighterSpellList {
    private final SpellList list;
    private final SpellsBoosts boosts;

    /**
     * @param list Base spell list
     */
    public BaseFighterSpellList(SpellList list) {
        this(list, new SimpleSpellsBoosts());
    }

    /**
     * @param list Base spell list
     * @param boosts Boost instance to use
     */
    public BaseFighterSpellList(SpellList list, SpellsBoosts boosts) {
        this.list = list;
        this.boosts = boosts;
    }

    @Override
    public Spell get(int spellId) {
        return boosts.get(list.get(spellId));
    }

    @Override
    public boolean has(int spellId) {
        return list.has(spellId);
    }

    @Override
    public Iterator<Spell> iterator() {
        return StreamSupport.stream(list.spliterator(), false)
            .map(boosts::get)
            .iterator()
        ;
    }

    @Override
    public void boost(int spellId, SpellsBoosts.Modifier modifier, int value) {
        boosts.boost(spellId, modifier, value);
    }
}
