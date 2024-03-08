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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter.invocation;

import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Characteristics for a double
 * This implements will simply retrieve the base characteristics from the invoker (i.e. {@link FighterCharacteristics#initial()})
 * and handle buffs.
 */
public final class DoubleFighterCharacteristics extends AbstractFighterCharacteristics {
    private @NonNegative int discernmentBoost = 0;

    /**
     * @param fighter The double fighter
     * @param invoker The invoker
     */
    public DoubleFighterCharacteristics(Fighter fighter, Fighter invoker) {
        super(fighter, invoker.characteristics().initial());
    }

    @Override
    public int initiative() {
        return 0; // initiative is not used for invocations
    }

    @Override
    public @NonNegative int discernment() {
        return discernmentBoost;
    }

    @Override
    public void alterDiscernment(int value) {
        discernmentBoost = Math.max(discernmentBoost + value, 0);
    }
}
