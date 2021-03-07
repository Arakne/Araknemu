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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.spell.boost;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellBoostChanged;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.Collection;

/**
 * SpellsBoosts with dispatcher
 */
public final class DispatcherSpellsBoosts implements SpellsBoosts {
    private final SpellsBoosts boosts;
    private final Dispatcher dispatcher;

    public DispatcherSpellsBoosts(SpellsBoosts boosts, Dispatcher dispatcher) {
        this.boosts = boosts;
        this.dispatcher = dispatcher;
    }

    @Override
    public int boost(int spellId, Modifier modifier, int value) {
        final int newValue = boosts.boost(spellId, modifier, value);

        dispatcher.dispatch(new SpellBoostChanged(spellId, modifier, newValue));

        return newValue;
    }

    @Override
    public int set(int spellId, Modifier modifier, int value) {
        boosts.set(spellId, modifier, value);
        dispatcher.dispatch(new SpellBoostChanged(spellId, modifier, value));

        return value;
    }

    @Override
    public void unset(int spellId, Modifier modifier) {
        boosts.unset(spellId, modifier);
        dispatcher.dispatch(new SpellBoostChanged(spellId, modifier, -1));
    }

    @Override
    public SpellModifiers modifiers(int spellId) {
        return boosts.modifiers(spellId);
    }

    @Override
    public Spell get(Spell spell) {
        return boosts.get(spell);
    }

    @Override
    public Collection<SpellModifiers> all() {
        return boosts.all();
    }
}
