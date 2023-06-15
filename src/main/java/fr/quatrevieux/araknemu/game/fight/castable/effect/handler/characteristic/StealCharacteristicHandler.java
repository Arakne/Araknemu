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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.function.BiConsumer;

/**
 * Steal a characteristic from a fighter to give to the caster
 * A configured effect id will be used for both add and remove characteristic buff effects
 *
 * This effect is equivalent of chaining {@link RemoveCharacteristicHandler} and {@link AddCharacteristicHandler} effects
 *
 * @see AlterCharacteristicHook
 */
public class StealCharacteristicHandler implements EffectHandler {
    private final AlterCharacteristicHook addHook;
    private final AlterCharacteristicHook removeHook;
    private final int addEffectId;
    private final int removeEffectId;

    /**
     * @param fight Fight where the effect takes effect
     * @param characteristic Characteristic to steal
     * @param addEffectId Spell effect id used as "add characteristic" effect
     * @param removeEffectId Spell effect id used as "remove characteristic" effect
     */
    public StealCharacteristicHandler(Fight fight, Characteristic characteristic, int addEffectId, int removeEffectId) {
        this(
            AlterCharacteristicHook.add(fight, characteristic), addEffectId,
            AlterCharacteristicHook.remove(fight, characteristic), removeEffectId
        );
    }

    /**
     * @param addHook Buff used for add characteristic to caster
     * @param addEffectId Spell effect id used as "add characteristic" effect
     * @param removeHook Buff used for remove characteristic from targets
     * @param removeEffectId Spell effect id used as "remove characteristic" effect
     */
    protected StealCharacteristicHandler(AlterCharacteristicHook addHook, int addEffectId, AlterCharacteristicHook removeHook, int removeEffectId) {
        this.addHook = addHook;
        this.removeHook = removeHook;
        this.addEffectId = addEffectId;
        this.removeEffectId = removeEffectId;
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Alter characteristic effect must be used as a buff");
    }

    @Override
    public final void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        final SpellEffect spellEffect = effect.effect();
        final Fighter caster = cast.caster();
        final Castable action = cast.action();

        final Applier applier = new Applier(caster, spellEffect, action);

        EffectValue.forEachTargets(spellEffect, caster, effect.targets(), applier);

        if (applier.total() > 0) {
            caster.buffs().add(new Buff(
                BuffEffect.withCustomEffect(spellEffect, addEffectId, applier.total()),
                action,
                caster,
                caster,
                addHook
            ));
        }
    }

    /**
     * Apply removal effect to target
     */
    private class Applier implements BiConsumer<Fighter, EffectValue> {
        private final Fighter caster;
        private final SpellEffect spellEffect;
        private final Castable action;
        private @NonNegative int total = 0;

        public Applier(Fighter caster, SpellEffect spellEffect, Castable action) {
            this.caster = caster;
            this.spellEffect = spellEffect;
            this.action = action;
        }

        @Override
        public void accept(Fighter target, EffectValue effectValue) {
            final int value = effectValue.value();
            total += value;

            target.buffs().add(new Buff(
                BuffEffect.withCustomEffect(spellEffect, removeEffectId, value),
                action,
                caster,
                target,
                removeHook
            ));
        }

        /**
         * Total stolen characteristic value
         */
        public @NonNegative int total() {
            return total;
        }
    }
}
