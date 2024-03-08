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

package fr.quatrevieux.araknemu.game.fight.fighter.invocation;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Characteristics for an invocation
 * Element characteristics (i.e. strength, intelligence, agility, luck) are altered by the invoker level, each level given 1% boost
 */
public final class InvocationFighterCharacteristics extends AbstractFighterCharacteristics {
    private @NonNegative int discernmentBoost = 0;

    public InvocationFighterCharacteristics(Monster monster, Fighter fighter, FighterData invoker) {
        super(fighter, new ModifiedCharacteristics(monster.characteristics(), modifier(invoker)));
    }

    @Override
    public int initiative() {
        return 0;
    }

    @Override
    public @NonNegative int discernment() {
        return discernmentBoost;
    }

    @Override
    public void alterDiscernment(int value) {
        discernmentBoost = Math.max(discernmentBoost + value, 0);
    }

    /**
     * Get the characteristics modifier for the given invoke
     * The value is always higher than 1
     */
    public static float modifier(FighterData invoker) {
        return 1 + invoker.level() / 100f;
    }

    private static final class ModifiedCharacteristics implements Characteristics {
        private final Characteristics base;
        private final float rate;

        public ModifiedCharacteristics(Characteristics base, float rate) {
            this.base = base;
            this.rate = rate;
        }

        @Override
        public int get(Characteristic characteristic) {
            int value = base.get(characteristic);

            if (characteristic == Characteristic.STRENGTH
                || characteristic == Characteristic.INTELLIGENCE
                || characteristic == Characteristic.LUCK
                || characteristic == Characteristic.AGILITY
            ) {
                value *= rate;
            }

            return value;
        }
    }
}
