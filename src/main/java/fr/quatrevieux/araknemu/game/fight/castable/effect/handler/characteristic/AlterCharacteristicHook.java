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

    protected AlterCharacteristicHook(Fight fight, Characteristic characteristic, int multiplier) {
        this.fight = fight;
        this.characteristic = characteristic;
        this.multiplier = multiplier;
    }

    @Override
    public void onBuffStarted(Buff buff) {
        final int value = buff.effect().min() * multiplier;

        buff.target().characteristics().alter(characteristic, value);
        fight.send(ActionEffect.buff(buff, value));
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        buff.target().characteristics().alter(characteristic, -buff.effect().min() * multiplier);
    }

    /**
     * Create the buff hook for add the given characteristic to the fighter
     */
    public static AlterCharacteristicHook add(Fight fight, Characteristic characteristic) {
        return new AlterCharacteristicHook(fight, characteristic, 1);
    }

    /**
     * Create the buff hook for remove the given characteristic to the fighter
     */
    public static AlterCharacteristicHook remove(Fight fight, Characteristic characteristic) {
        return new AlterCharacteristicHook(fight, characteristic, -1);
    }
}
