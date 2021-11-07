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

package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerLife;
import fr.quatrevieux.araknemu.game.player.experience.GamePlayerExperience;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;

/**
 * Store the current player data
 */
public final class PlayerData implements CharacterProperties {
    private final PlayerCharacteristics characteristics;
    private final PlayerLife life;
    private final SpellBook spells;
    private final GamePlayerExperience experience;
    private final PlayerInventory inventory;

    public PlayerData(Dispatcher dispatcher, GamePlayer player, Player entity, SpellBook spells, GamePlayerExperience experience) {
        this.spells = spells;
        this.experience = experience;
        this.characteristics = new PlayerCharacteristics(dispatcher, player, entity);
        this.life = new PlayerLife(player, entity);
        this.inventory = player.inventory();
    }

    @Override
    public PlayerCharacteristics characteristics() {
        return characteristics;
    }

    @Override
    public PlayerLife life() {
        return life;
    }

    @Override
    public SpellBook spells() {
        return spells;
    }

    @Override
    public GamePlayerExperience experience() {
        return experience;
    }

    @Override
    public long kamas() {
        return inventory.kamas();
    }

    void build() {
        characteristics.rebuildSpecialEffects();
        life.init();
    }
}
