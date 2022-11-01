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
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Effect for remove action point, which can be dodged by the target
 * When used as direct effect, it will be considered as buff with duration of 1 turn
 */
public final class ActionPointLostHandler implements EffectHandler {
    private final ActionPointLostApplier applier;

    public ActionPointLostHandler(Fight fight) {
        this.applier = new ActionPointLostApplier(fight);
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        buff(cast, effect);
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            applier.apply(cast, target, effect.effect());
        }
    }
}
