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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;

/**
 * Hook for handle vitality alteration
 * Synchronize fighter life using {@link fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#alterMax(FighterData, int)}
 * in addition to characteristic change performed by the base {@link AlterCharacteristicHook}
 *
 * @see AddVitalityHandler
 * @see RemoveVitalityHandler
 */
public final class AlterVitalityHook extends AlterCharacteristicHook {
    /**
     * @param fight Active fight
     * @param multiplier 1 for add vitality, -1 for remove
     */
    private AlterVitalityHook(Fight fight, int multiplier) {
        super(fight, Characteristic.VITALITY, multiplier, false);
    }

    @Override
    protected void apply(Buff buff, Fighter target, int value) {
        super.apply(buff, target, value);
        target.life().alterMax(buff.caster(), value);
    }

    /**
     * Create the buff hook for add vitality to the fighter
     */
    public static AlterVitalityHook add(Fight fight) {
        return new AlterVitalityHook(fight, 1);
    }

    /**
     * Create the buff hook for remove vitality to the fighter
     */
    public static AlterVitalityHook remove(Fight fight) {
        return new AlterVitalityHook(fight, -1);
    }
}
