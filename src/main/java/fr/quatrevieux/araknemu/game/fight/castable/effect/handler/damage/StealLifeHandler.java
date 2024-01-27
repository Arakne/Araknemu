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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.AbstractAttenuableAreaEffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Handle steal life
 */
public final class StealLifeHandler extends AbstractAttenuableAreaEffectHandler implements EffectHandler, BuffHook {
    private final DamageApplier applier;

    public StealLifeHandler(Element element, Fight fight) {
        super(fight);

        this.applier = new DamageApplier(element, fight);
    }

    @Override
    protected boolean applyOnTarget(FightCastScope cast, SpellEffect effect, Fighter target, EffectValue value, @NonNegative int distance) {
        final Fighter caster = cast.caster();

        applyCasterHeal(applier.apply(caster, effect, target, value, distance), caster);

        return true;
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public boolean onStartTurn(Buff buff) {
        applyCasterHeal(applier.apply(buff), buff.caster());

        return !buff.target().dead();
    }

    private void applyCasterHeal(int damage, Fighter caster) {
        // #56 : do not heal if dead
        if (damage >= 0 || caster.dead()) { // Heal or no effect
            return;
        }

        // Minimal heal is 1
        caster.life().alter(caster, Math.max(-damage / 2, 1));
    }
}
