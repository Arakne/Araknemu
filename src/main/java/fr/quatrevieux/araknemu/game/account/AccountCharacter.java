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

package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.sprite.SpriteInfo;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;
import fr.quatrevieux.araknemu.game.world.creature.accessory.EmptyAccessories;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Character for game account
 */
public final class AccountCharacter {
    private final GameAccount account;
    private final Player entity;
    private final Accessories accessories;

    public AccountCharacter(GameAccount account, Player entity, Accessories accessories) {
        this.account = account;
        this.entity = entity;
        this.accessories = accessories;
    }

    public AccountCharacter(GameAccount account, Player character) {
        this(account, character, new EmptyAccessories());
    }

    /**
     * Get the layer account
     */
    @Pure
    public GameAccount account() {
        return account;
    }

    /**
     * Get the player id
     */
    @Pure
    public int id() {
        return entity.id();
    }

    /**
     * Get the character entity
     */
    @Pure
    public Player character() {
        return entity;
    }

    /**
     * Get the character level
     */
    @Pure
    public int level() {
        return entity.level();
    }

    /**
     * Get sprite info for character
     */
    public SpriteInfo spriteInfo() {
        return new AccountCharacterSpriteInfo(entity, accessories);
    }

    /**
     * Get the player server ID
     */
    @Pure
    public int serverId() {
        return entity.serverId();
    }

    /**
     * Create the character from creation request
     *
     * @param account The creator account
     * @param request The request
     */
    public static AccountCharacter fromRequest(GameAccount account, AddCharacterRequest request) {
        return new AccountCharacter(
            account,
            Player.forCreation(
                account.id(),
                account.serverId(),
                request.name(),
                request.race(),
                request.gender(),
                request.colors()
            )
        );
    }
}
