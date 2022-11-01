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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsUtils;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.CellShown;

/**
 * Handle invisibility effect
 *
 * This effect only works has buff : direct effect is not supported
 * The hidden flag will be changed on buff start and termination
 *
 * When the fighter cast a spell or close combat attack, he will lose its invisibility if he performs direct damage.
 * For other spells (or close combat) effects, the cast cell (i.e. current cell of the caster) will be shown to all fighters.
 *
 * Note: If two (or more) invisibility buffs are effective on a fighter, the invisibility state will be terminated
 * with the last (i.e. longest) buff.
 *
 * @see FighterData#hidden() Check the hidden state
 * @see Fighter#setHidden(FighterData, boolean) Called by the handler for change hidden state
 */
public final class InvisibilityHandler implements EffectHandler, BuffHook {
    private final Fight fight;

    public InvisibilityHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Invisibility effect must be used as a buff");
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        final SpellEffect spellEffect = effect.effect();
        final Fighter caster = cast.caster();
        final Castable action = cast.action();

        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(spellEffect, action, caster, target, this));
        }
    }

    @Override
    public void onBuffStarted(Buff buff) {
        buff.target().setHidden(buff.caster(), true);
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        final Fighter target = buff.target();

        // Set visible only if not yet visible and there is no other active invisibility buff
        if (target.hidden() && !hasOtherInvisibilityBuff(target)) {
            target.setHidden(buff.caster(), false);
        }
    }

    @Override
    public void onCast(Buff buff, FightCastScope cast) {
        final Fighter target = buff.target();

        if (!target.hidden()) {
            return;
        }

        // The fighter cast a damage effect : he loses its invisibility
        if (hasDirectDamageEffect(cast)) {
            target.setHidden(target, false);
            return;
        }

        // Show cell only on the first buff
        if (isFirstInvisibilityBuff(target, buff)) {
            fight.send(new CellShown(target, target.cell().id()));
        }
    }

    /**
     * Check if another active invisibility buff is present
     */
    private boolean hasOtherInvisibilityBuff(FighterData fighter) {
        for (Buff activeBuff : fighter.buffs()) {
            // Another invisibility buff exists : do not set target visible
            if (activeBuff.valid() && activeBuff.hook() == this) {
                return true;
            }
        }

        return false;
    }

    /**
     * Does the given cast will perform direct damage or not
     */
    private boolean hasDirectDamageEffect(FightCastScope cast) {
        for (CastScope.EffectScope<Fighter> effect : cast.effects()) {
            final SpellEffect spellEffect = effect.effect();

            if (spellEffect.duration() == 0 && EffectsUtils.isDamageEffect(spellEffect.effect())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the given buff is the first of the buff list
     */
    private boolean isFirstInvisibilityBuff(FighterData fighter, Buff currentBuff) {
        for (Buff otherBuff : fighter.buffs()) {
            if (otherBuff.hook() == this) {
                // Current buff is not the first active invisibility buff
                return otherBuff == currentBuff;
            }
        }

        // Should not occur
        return true;
    }
}
