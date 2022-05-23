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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellBoostChanged;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellBoost;

/**
 * Send spell boosts values when changed
 *
 * Note: this listener will take in account the base player spell boosts (i.e. boosts given by items),
 *       so it will send fighter boost + items boost values
 *
 * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterSpellList#boost(int, SpellsBoosts.Modifier, int)
 */
public final class SendSpellBoosted implements Listener<SpellBoostChanged> {
    private final PlayerFighter fighter;

    public SendSpellBoosted(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(SpellBoostChanged event) {
        fighter.send(new SpellBoost(
            event.spellId(),
            event.modifier(),
            event.value() + fighter.player().properties().spells().boosts()
                .modifiers(event.spellId())
                .value(event.modifier())
        ));
    }

    @Override
    public Class<SpellBoostChanged> event() {
        return SpellBoostChanged.class;
    }
}
