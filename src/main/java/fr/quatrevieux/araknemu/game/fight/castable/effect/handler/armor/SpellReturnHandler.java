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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsUtils;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.util.Asserter;

/**
 * Handle spell return buff effect
 */
public final class SpellReturnHandler implements EffectHandler, BuffHook {
    private final Fight fight;

    private final RandomUtil random = new RandomUtil();

    public SpellReturnHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Spell return effect can be only used as buff");
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public boolean onCastTarget(Buff buff, FightCastScope cast) {
        if (buff.target().equals(cast.caster()) || !isReturnableCast(cast)) {
            return true;
        }

        if (!cast.spell().filter(spell -> checkSpellReturned(spell, buff.effect())).isPresent()) {
            fight.send(ActionEffect.returnSpell(buff.target(), false));
            return true;
        }

        cast.replaceTarget(buff.target(), cast.caster());
        fight.send(ActionEffect.returnSpell(buff.target(), true));

        return false;
    }

    /**
     * Check if the action is returnable (Contains damage or AP loose effects)
     *
     * @param cast The action to check
     *
     * @return true if the cast can is returnable
     */
    private boolean isReturnableCast(FightCastScope cast) {
        return cast.effects().stream()
            .map(CastScope.EffectScope::effect)
            .anyMatch(effect -> {
                // Should return only direct damage effects
                if (EffectsUtils.isDamageEffect(effect.effect()) && effect.duration() == 0) {
                    return true;
                }

                return EffectsUtils.isLooseApEffect(effect.effect());
            })
        ;
    }

    /**
     * Check if the spell should be returned
     *
     * Check the spell level and the return probability
     *
     * @param spell Spell to check
     * @param returnEffect The buff effect
     *
     * @return true if the spell should be returned
     */
    private boolean checkSpellReturned(Spell spell, SpellEffect returnEffect) {
        if (spell.level() > Math.max(returnEffect.min(), returnEffect.max())) {
            return false;
        }

        final int probability = Asserter.assertPercent(returnEffect.special());

        if (probability >= 100) {
            return true;
        }

        return random.bool(probability);
    }
}
