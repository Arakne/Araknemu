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

import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighterCharacteristics;
import fr.quatrevieux.araknemu.game.player.characteristic.CharacterCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Player fighter characteristics
 */
public final class PlayerFighterCharacteristics extends AbstractFighterCharacteristics implements CharacterCharacteristics {
    private final CharacterCharacteristics baseCharacteristics;
    private int discernmentBoost = 0;

    public PlayerFighterCharacteristics(CharacterCharacteristics baseCharacteristics, PlayerFighter fighter) {
        super(fighter, baseCharacteristics);

        this.baseCharacteristics = baseCharacteristics;
    }

    @Override
    public int initiative() {
        return baseCharacteristics.initiative();
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
        return buffs();
    }

    @Override
    public int boostPoints() {
        return baseCharacteristics.boostPoints();
    }

    @Override
    public @NonNegative int discernment() {
        return Math.max(baseCharacteristics.discernment() + discernmentBoost, 0);
    }

    @Override
    public void alterDiscernment(int value) {
        discernmentBoost += value;
    }

    @Override
    public int pods() {
        return baseCharacteristics.pods();
    }
}
