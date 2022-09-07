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

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * Effect handler for steal vitality
 * This effect is equivalent to chain {@link AddVitalityHandler} and {@link RemoveVitalityHandler}
 *
 * @see AlterVitalityHook
 * @see AddVitalityHandler
 * @see RemoveVitalityHandler
 */
public final class StealVitalityHandler extends StealCharacteristicHandler {
    /**
     * @param fight Fight where the handler take effect
     * @param addEffectId Spell effect id used as "add vitality" effect
     * @param removeEffectId Spell effect id used as "remove vitality" effect
     */
    public StealVitalityHandler(Fight fight, int addEffectId, int removeEffectId) {
        super(
            AlterVitalityHook.add(fight), addEffectId,
            AlterVitalityHook.remove(fight), removeEffectId
        );
    }
}
