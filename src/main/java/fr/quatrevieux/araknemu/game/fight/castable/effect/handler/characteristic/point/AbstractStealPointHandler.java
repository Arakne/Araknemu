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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.FightBuff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Base effect for steal turn points
 *
 * @see StealActionPointHandler
 * @see StealMovementPointHandler
 */
public abstract class AbstractStealPointHandler implements EffectHandler {
    private final Fight fight;
    private final AbstractPointLostApplier removePointApplier;
    private final AlterPointHook addPointHook;
    private final int addPointEffect;

    /**
     * @param fight Fight where the effect will be applied
     * @param removePointApplier Applied use to remove points from targets
     * @param addPointHook Applied use to add points to caster
     * @param addPointEffect Effect id used by the "add point" buff
     */
    public AbstractStealPointHandler(Fight fight, AbstractPointLostApplier removePointApplier, AlterPointHook addPointHook, int addPointEffect) {
        this.fight = fight;
        this.removePointApplier = removePointApplier;
        this.addPointHook = addPointHook;
        this.addPointEffect = addPointEffect;
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter caster = cast.caster();
        final int stolen = apply(cast, effect);

        if (stolen > 0) {
            fight.turnList().current()
                .filter(turn -> turn.fighter().equals(caster))
                .ifPresent(turn -> applyOnCurrentTurn(fight, turn, caster, stolen))
            ;
        }
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter caster = cast.caster();
        final int stolen = apply(cast, effect);

        if (stolen > 0) {
            caster.buffs().add(
                new FightBuff(
                    BuffEffect.withCustomEffect(effect.effect(), addPointEffect, stolen),
                    cast.action(),
                    caster,
                    caster,
                    addPointHook
                )
            );
        }
    }

    /**
     * Apply the add point effect to the current turn
     * This method is called only if the effect is used as direct effect (i.e. without duration)
     * This method should update turn points and send to client the current turn point modification
     *
     * @param fight The active fight
     * @param turn The active turn
     * @param toAdd Number of points to add. This value is always >= 1
     */
    protected abstract void applyOnCurrentTurn(Fight fight, Turn turn, FighterData caster, @Positive int toAdd);

    /**
     * Apply to all targets and compute the stolen points
     *
     * @return Stolen action points. 0 if target has dodged
     */
    private int apply(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter caster = cast.caster();
        final SpellEffect baseEffect = effect.effect();

        int stolen = 0;

        for (Fighter target : effect.targets()) {
            if (!target.equals(caster)) {
                stolen += removePointApplier.apply(cast, target, baseEffect);
            }
        }

        return stolen;
    }
}
