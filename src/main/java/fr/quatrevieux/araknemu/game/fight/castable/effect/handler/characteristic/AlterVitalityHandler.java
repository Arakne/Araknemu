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
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Alter vitality with buff effect
 * The effect will be added to current and max fighter life
 *
 * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#alterMax(fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter, int)
 */
public final class AlterVitalityHandler implements EffectHandler, BuffHook {
    private final Fight fight;
    private final int multiplier;
    private final boolean canBeDispelled;

    /**
     * @param fight Active fight
     * @param multiplier 1 for add vitality, -1 for remove
     * @param canBeDispelled Added buff effect can be dispelled. See: {@link Buff#canBeDispelled()}
     */
    public AlterVitalityHandler(Fight fight, int multiplier, boolean canBeDispelled) {
        this.fight = fight;
        this.multiplier = multiplier;
        this.canBeDispelled = canBeDispelled;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Alter characteristic effect must be used as a buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        final SpellEffect spellEffect = effect.effect();
        final ActiveFighter caster = cast.caster();

        EffectValue.forEachTargets(
            spellEffect,
            caster,
            effect.targets(),
            (target, effectValue) -> target.buffs().add(new Buff(
                BuffEffect.fixed(spellEffect, effectValue.value()),
                cast.action(),
                caster,
                target,
                this,
                canBeDispelled
            ))
        );
    }

    @Override
    public void onBuffStarted(Buff buff) {
        final int value = buff.effect().min() * multiplier;

        buff.target().characteristics().alter(Characteristic.VITALITY, value);
        fight.send(ActionEffect.buff(buff, buff.effect().min()));
        buff.target().life().alterMax(buff.caster(), value);
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        final int value = -buff.effect().min() * multiplier;

        buff.target().characteristics().alter(Characteristic.VITALITY, value);
        buff.target().life().alterMax(buff.caster(), value);
    }

    /**
     * Create a dispellable add vitality effect
     */
    public static AlterVitalityHandler add(Fight fight) {
        return new AlterVitalityHandler(fight, 1, true);
    }

    /**
     * Create a not dispellable add vitality effect
     */
    public static AlterVitalityHandler addNotDispellable(Fight fight) {
        return new AlterVitalityHandler(fight, 1, true);
    }

    /**
     * Create a dispellable remove vitality effect
     */
    public static AlterVitalityHandler remove(Fight fight) {
        return new AlterVitalityHandler(fight, -1, true);
    }
}
