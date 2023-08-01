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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Buff hook for handle a characteristic change effect
 * The characteristic modification can be positive (add) or negative (remove)
 *
 * Use factory methods for create the hook instance
 *
 * @see AlterCharacteristicHook#add(Fight, Characteristic) For add a characteristic
 * @see AlterCharacteristicHook#remove(Fight, Characteristic) For remove a characteristic
 */
public class AlterCharacteristicHook implements BuffHook {
    private final Fight fight;
    private final Characteristic characteristic;
    private final int multiplier;
    private final boolean applyMultiplierOnPacketValue;

    /**
     * @param fight Fight where the hook will take effect
     * @param characteristic Characteristic to alter
     * @param multiplier Value multiplier. -1 for removing characteristic, 1 for adding
     * @param applyMultiplierOnPacketValue Does the actual altered value should be sent, or the raw effect value. Set to true if the client to not handle negative value by it-self (like AP removal)
     */
    protected AlterCharacteristicHook(Fight fight, Characteristic characteristic, int multiplier, boolean applyMultiplierOnPacketValue) {
        this.fight = fight;
        this.characteristic = characteristic;
        this.multiplier = multiplier;
        this.applyMultiplierOnPacketValue = applyMultiplierOnPacketValue;
    }

    @Override
    public final void onBuffStarted(Buff buff) {
        final int effectValue = buff.effect().min();
        final int appliedValue = effectValue * multiplier;

        apply(buff, buff.target(), appliedValue);
        fight.send(ActionEffect.buff(buff, applyMultiplierOnPacketValue ? appliedValue : effectValue));
    }

    @Override
    public final void onBuffTerminated(Buff buff) {
        apply(buff, buff.target(), -buff.effect().min() * multiplier);
    }

    /**
     * Apply the buff effect to the buff target
     * Can be overridden for apply custom effects
     *
     * @param buff Buff to apply
     * @param target Buff target
     * @param value Value to apply. Negative for removing, positive for adding
     */
    protected void apply(Buff buff, Fighter target, int value) {
        target.characteristics().alter(characteristic, value);
    }

    /**
     * Create the buff hook for add the given characteristic to the fighter
     */
    public static AlterCharacteristicHook add(Fight fight, Characteristic characteristic) {
        return new AlterCharacteristicHook(fight, characteristic, 1, false);
    }

    /**
     * Create the buff hook for remove the given characteristic to the fighter
     */
    public static AlterCharacteristicHook remove(Fight fight, Characteristic characteristic) {
        return new AlterCharacteristicHook(fight, characteristic, -1, false);
    }
}
