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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.monster.Monster;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Monster fighter characteristics
 */
public final class MonsterFighterCharacteristics extends AbstractFighterCharacteristics {
    private final Monster monster;

    public MonsterFighterCharacteristics(Monster monster, Fighter fighter) {
        super(fighter, monster.characteristics());

        this.monster = monster;
    }

    @Override
    public int initiative() {
        return monster.initiative();
    }

    @Override
    public @NonNegative int discernment() {
        return 0; // monster do not have discernment
    }

    @Override
    public void alterDiscernment(int value) {
        // No-op: monster can't have discernment
    }
}
