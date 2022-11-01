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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;

/**
 * Handle effect of punishment spell
 *
 * When the life of the caster is at 50%, damage corresponds to total percent life
 * When the life is higher or lower than 50%, damage decrease
 *
 * Formula :
 *
 * (cos(2 × PI × ([%life] - 0.5)) + 1)²
 * ———————————————————————————————————— × [max life] × [effect value]
 *                 4
 *
 * The damage are not boosted, but resistance are applied, because this effect is related to neutral element
 *
 * See: https://dofuswiki.fandom.com/wiki/Punishment
 */
public final class PunishmentHandler implements EffectHandler {
    private final DamageApplier applier;

    public PunishmentHandler(Fight fight) {
        this.applier = new DamageApplier(Element.NEUTRAL, fight);
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter caster = cast.caster();
        final FighterLife casterLife = caster.life();

        final double percentLife = (double) casterLife.current() / casterLife.max();
        final double base = Math.cos(2 * Math.PI * (percentLife - 0.5)) + 1;
        final double factor = base * base / 4 * casterLife.max();

        for (Fighter target : effect.targets()) {
            final double percent = EffectValue.create(effect.effect(), caster, target).value() / 100d;
            final int value = (int) (factor * percent);

            applier.applyFixed(caster, value, target);
        }
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use punishment as buff");
    }
}
