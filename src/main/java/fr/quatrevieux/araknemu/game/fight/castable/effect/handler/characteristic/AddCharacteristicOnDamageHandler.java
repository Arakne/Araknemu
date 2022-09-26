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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

import java.util.HashMap;
import java.util.Map;

/**
 * Effect handler for punishment
 *
 * Add a characteristic boost when the fight suffer from direct damage
 * Characteristics mapping must be added using {@link AddCharacteristicOnDamageHandler#register(int, Characteristic)} method
 *
 * Parameters:
 * - first (i.e. min) is the effect used as characteristic boost
 * - second (i.e. max) is the maximal boost value per turn
 * - third (i.e. special) is the boost duration
 *
 * Note: this buff cannot be dispelled, but the boosted characteristics can
 */
public final class AddCharacteristicOnDamageHandler implements EffectHandler, BuffHook {
    private final Fight fight;

    /**
     * Map first effect parameter to related characteristic hook
     */
    private final Map<Integer, AlterCharacteristicHook> hooksMapping = new HashMap<>();

    public AddCharacteristicOnDamageHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        throw new UnsupportedOperationException("Alter characteristic effect must be used as a buff");
    }

    @Override
    public void buff(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        final SpellEffect spellEffect = effect.effect();
        final Fighter caster = cast.caster();

        for (Fighter target : cast.targets()) {
            target.buffs().add(new Buff(
                spellEffect,
                cast.action(),
                caster,
                target,
                this,
                false
            ));
        }
    }

    /**
     * Register an handled characteristic
     *
     * @param effectId Handled effect id. This is the first parameter of the spell effect
     * @param hook Alter characteristic hook to use
     *
     * @return This instance
     */
    public AddCharacteristicOnDamageHandler register(int effectId, AlterCharacteristicHook hook) {
        hooksMapping.put(effectId, hook);

        return this;
    }

    /**
     * Register an handled characteristic
     *
     * @param effectId Handled effect id. This is the first parameter of the spell effect
     * @param characteristic The mapped characteristic
     *
     * @return This instance
     */
    public AddCharacteristicOnDamageHandler register(int effectId, Characteristic characteristic) {
        return register(effectId, AlterCharacteristicHook.add(fight, characteristic));
    }

    @Override
    public void onDirectDamageApplied(Buff buff, Fighter caster, @Positive int damage) {
        final Fighter target = buff.target();
        final int boostEffectId = buff.effect().min();
        final AlterCharacteristicHook hook = hooksMapping.get(boostEffectId);

        if (hook == null) {
            throw new IllegalArgumentException("Unsupported effect " + boostEffectId + " for punishment effect");
        }

        final int maximalValue = buff.effect().max() - currentBoostValue(buff, target);

        if (maximalValue <= 0) {
            return;
        }

        target.buffs().add(new Buff(
            BuffEffect.withCustomEffectAndDuration(buff.effect(), boostEffectId, Math.min(maximalValue, damage), Asserter.assertGTENegativeOne(buff.effect().special())),
            buff.action(),
            buff.caster(),
            target,
            hook
        ));
    }

    private @NonNegative int currentBoostValue(Buff buff, FighterData target) {
        // Add 1 to duration in case of self damage
        final int expectedEffectDuration = buff.effect().special() + (target.equals(fight.turnList().currentFighter()) ? 1 : 0);

        int boost = 0;

        for (Buff activeBuff : target.buffs()) {
            if (activeBuff.effect().effect() == buff.effect().min()
                && activeBuff.remainingTurns() == expectedEffectDuration
                && activeBuff.action().equals(buff.action())
            ) {
                boost += activeBuff.effect().min();
            }
        }

        return boost;
    }
}
