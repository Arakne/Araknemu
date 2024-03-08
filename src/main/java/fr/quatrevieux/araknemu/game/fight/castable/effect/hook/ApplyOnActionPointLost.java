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

package fr.quatrevieux.araknemu.game.fight.castable.effect.hook;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;

/**
 * Apply the effect when the target lose action points
 */
public final class ApplyOnActionPointLost extends AbstractEffectHookHandler {
    @Override
    protected BuffHook createHook(EffectHandler handler) {
        return new BuffHook() {
            @Override
            public void onCharacteristicAltered(Buff buff, Characteristic characteristic, int value) {
                if (value >= 0 || characteristic != Characteristic.ACTION_POINT) {
                    return;
                }

                while (value++ < 0) {
                    handler.applyFromHook(buff);
                }
            }
        };
    }
}
