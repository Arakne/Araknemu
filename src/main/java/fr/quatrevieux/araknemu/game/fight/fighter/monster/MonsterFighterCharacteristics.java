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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Monster fighter characteristics
 */
public final class MonsterFighterCharacteristics implements FighterCharacteristics {
    private final Monster monster;
    private final Fighter fighter;

    private final MutableCharacteristics buffs = new DefaultCharacteristics();

    public MonsterFighterCharacteristics(Monster monster, Fighter fighter) {
        this.monster = monster;
        this.fighter = fighter;
    }

    @Override
    public int initiative() {
        return monster.initiative();
    }

    @Override
    public int discernment() {
        return 0; // monster do not have discernment
    }

    @Override
    public int get(Characteristic characteristic) {
        return monster.characteristics().get(characteristic) + buffs.get(characteristic);
    }

    @Override
    public void alter(Characteristic characteristic, int value) {
        buffs.add(characteristic, value);
        fighter.dispatch(new FighterCharacteristicChanged(characteristic, value));
    }

    @Override
    public Characteristics initial() {
        return monster.characteristics();
    }
}
