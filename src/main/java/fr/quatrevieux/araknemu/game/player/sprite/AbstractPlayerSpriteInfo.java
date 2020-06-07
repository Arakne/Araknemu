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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.sprite;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;

/**
 * Sprite info for using Player entity
 */
abstract public class AbstractPlayerSpriteInfo implements SpriteInfo {
    final private Player entity;

    public AbstractPlayerSpriteInfo(Player entity) {
        this.entity = entity;
    }

    @Override
    public int id() {
        return entity.id();
    }

    @Override
    public String name() {
        return entity.name();
    }

    @Override
    public Colors colors() {
        return entity.colors();
    }

    @Override
    public int gfxId() {
        return 10 * entity.race().ordinal() + entity.gender().ordinal();
    }

    @Override
    public SpriteSize size() {
        return new SpriteSize(100, 100);
    }

    @Override
    public Gender gender() {
        return entity.gender();
    }

    @Override
    public Race race() {
        return entity.race();
    }
}
