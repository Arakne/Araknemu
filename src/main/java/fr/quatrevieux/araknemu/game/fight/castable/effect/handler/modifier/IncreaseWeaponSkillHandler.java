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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.modifier;

import fr.quatrevieux.araknemu.game.fight.castable.BaseCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.closeCombat.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

import java.util.Optional;

/**
 * Increase the ability of a weapon on a fighter
 * The boost value is a percentage, which is added to the weapon effect, before the weapon damage calculation
 *
 * Effect parameters:
 * - min: The item type {@link CastableWeapon#weaponType()}. If the weapon type is different, the effect is ignored.
 * - max: The boost value
 *
 * @see CastableWeapon#ability()
 * @see CastableWeapon#increaseAbility(int) Called when the buff is applied
 * @see CastableWeapon#decreaseAbility(int) Called when the buff is removed
 */
public final class IncreaseWeaponSkillHandler implements EffectHandler, BuffHook {
    @Override
    public void handle(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        throw new UnsupportedOperationException("Increase weapon skill can only be used as buff");
    }

    @Override
    public void buff(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onBuffStarted(Buff buff) {
        weapon(buff).ifPresent(weapon -> weapon.increaseAbility(buff.effect().max()));
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        weapon(buff).ifPresent(weapon -> weapon.decreaseAbility(buff.effect().max()));
    }

    private Optional<CastableWeapon> weapon(Buff buff) {
        return buff.target().closeCombat()
            .filter(CastableWeapon.class::isInstance)
            .map(CastableWeapon.class::cast)
            .filter(weapon -> weapon.weaponType().id() == buff.effect().min())
        ;
    }
}
