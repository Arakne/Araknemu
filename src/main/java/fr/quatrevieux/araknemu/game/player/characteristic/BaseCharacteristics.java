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

package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.race.GamePlayerRace;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Characteristics wrapper for player base stats
 */
public final class BaseCharacteristics implements MutableCharacteristics {
    private final Dispatcher dispatcher;
    private final GamePlayerRace race;
    private final Player player;

    public BaseCharacteristics(Dispatcher dispatcher, GamePlayerRace race, Player player) {
        this.dispatcher = dispatcher;
        this.race = race;
        this.player = player;
    }

    @Override
    public int get(Characteristic characteristic) {
        return race.baseStats(player.level()).get(characteristic) + player.stats().get(characteristic);
    }

    @Override
    public void set(Characteristic characteristic, int value) {
        player.stats().set(characteristic, value);

        dispatcher.dispatch(new CharacteristicsChanged());
    }

    @Override
    public void add(Characteristic characteristic, int value) {
        player.stats().add(characteristic, value);

        dispatcher.dispatch(new CharacteristicsChanged());
    }
}
