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

package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.player.characteristic.CharacterCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Player fighter characteristics
 */
public final class PlayerFighterCharacteristics implements FighterCharacteristics, CharacterCharacteristics {
    private final CharacterCharacteristics baseCharacteristics;
    private final PlayerFighter fighter;

    private final MutableCharacteristics buffs = new DefaultCharacteristics();

    public PlayerFighterCharacteristics(CharacterCharacteristics baseCharacteristics, PlayerFighter fighter) {
        this.baseCharacteristics = baseCharacteristics;
        this.fighter = fighter;
    }

    @Override
    public int initiative() {
        return baseCharacteristics.initiative();
    }

    @Override
    public int get(Characteristic characteristic) {
        return baseCharacteristics.get(characteristic) + buffs.get(characteristic);
    }

    @Override
    public MutableCharacteristics base() {
        return baseCharacteristics.base();
    }

    @Override
    public Characteristics stuff() {
        return baseCharacteristics.stuff();
    }

    @Override
    public Characteristics feats() {
        return baseCharacteristics.feats();
    }

    @Override
    public Characteristics boost() {
        return buffs;
    }

    @Override
    public int boostPoints() {
        return baseCharacteristics.boostPoints();
    }

    @Override
    public int discernment() {
        // @todo Add buff discernment
        return baseCharacteristics.discernment();
    }

    @Override
    public int pods() {
        return baseCharacteristics.pods();
    }

    @Override
    public void alter(Characteristic characteristic, int value) {
        buffs.add(characteristic, value);
        fighter.dispatch(new FighterCharacteristicChanged(characteristic, value));
    }

    @Override
    public Characteristics initial() {
        return baseCharacteristics;
    }
}
