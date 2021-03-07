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

package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.sprite.AbstractPlayerSpriteInfo;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

/**
 * Sprite info for account character
 */
public final class AccountCharacterSpriteInfo extends AbstractPlayerSpriteInfo {
    private final Accessories accessories;

    public AccountCharacterSpriteInfo(Player entity, Accessories accessories) {
        super(entity);
        this.accessories = accessories;
    }

    @Override
    public Accessories accessories() {
        return accessories;
    }
}
