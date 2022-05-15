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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.modifier;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

/**
 * Add a spell boost to a fighter
 * The boost is applied on buff start, and removed on buff termination
 *
 * Parameters:
 * - The first effect parameter (i.e. min) is the spell id
 * - The third effect parameter (i.e. special) is the boost value
 *
 * Note: There is no game action packet related to this effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterSpellList#boost(int, SpellsBoosts.Modifier, int)
 * @see BuffHook#onBuffStarted(Buff) Hook used for apply the boost
 * @see BuffHook#onBuffTerminated(Buff) Hook used for removed the boost
 */
public final class BoostSpellHandler implements EffectHandler, BuffHook {
    private final SpellsBoosts.Modifier modifier;

    /**
     * @param modifier Spell modifier to apply
     */
    public BoostSpellHandler(SpellsBoosts.Modifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Boost spell can only be used as buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (PassiveFighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onBuffStarted(Buff buff) {
        final PassiveFighter target = buff.target();

        if (!(target instanceof Fighter)) {
            return;
        }

        final Fighter fighter = (Fighter) target;
        fighter.spells().boost(buff.effect().min(), modifier, buff.effect().special());
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        final PassiveFighter target = buff.target();

        if (!(target instanceof Fighter)) {
            return;
        }

        final Fighter fighter = (Fighter) target;
        fighter.spells().boost(buff.effect().min(), modifier, -buff.effect().special());
    }
}
