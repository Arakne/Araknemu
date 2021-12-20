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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Apply dodgeable action point removal effect
 */
public final class ActionPointLostApplier extends AbstractPointLostApplier {
    public ActionPointLostApplier(Fight fight) {
        super(fight, AlterPointHook.removeActionPoint(fight), Characteristic.ACTION_POINT, Characteristic.RESISTANCE_ACTION_POINT);
    }

    @Override
    protected ActionEffect dodgeMessage(PassiveFighter caster, PassiveFighter target, int value) {
        return ActionEffect.dodgeActionPointLost(caster, target, value);
    }
}
