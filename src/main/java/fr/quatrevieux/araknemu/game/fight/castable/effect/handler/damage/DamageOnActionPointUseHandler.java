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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;

/**
 * Apply poison when action points are used
 *
 * This effect is applied on end turn
 * The first effect parameter (i.e. min) is the amount of AP for apply damage
 * The second effect parameter (i.e. max) is the damage to apply per amount of used AP
 * Damage are modified by {@link Element#FIRE} and other boosts
 *
 * @see BuffHook#onEndTurn(Buff, Turn) The called hook
 */
public final class DamageOnActionPointUseHandler implements EffectHandler, BuffHook {
    private final DamageApplier applier;

    public DamageOnActionPointUseHandler(Fight fight) {
        this.applier = new DamageApplier(Element.FIRE, fight);
    }

    @Override
    public void handle(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        throw new UnsupportedOperationException("Damage on action point use can only be used as buff");
    }

    @Override
    public void buff(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onEndTurn(Buff buff, Turn turn) {
        final int usedAP = turn.points().usedActionPoints();
        final int minAP = buff.effect().min();
        final Fighter caster = buff.caster();

        // Not enough AP used for apply the effect
        if (usedAP < minAP) {
            return;
        }

        // Create a fake Buff object with computed damage to apply
        applier.apply(new Buff(
            BuffEffect.fixed(buff.effect(), (usedAP / minAP) * buff.effect().max()),
            buff.action(),
            caster,
            buff.target(),
            this
        ));
    }
}
