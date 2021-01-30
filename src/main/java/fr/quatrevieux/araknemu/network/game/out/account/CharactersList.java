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

package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.game.account.AccountCharacter;

import java.util.Collection;

/**
 * List all account characters
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L471
 */
final public class CharactersList {
    final private long remainingTime;
    final private Collection<AccountCharacter> characters;

    public CharactersList(long remainingTime, Collection<AccountCharacter> characters) {
        this.remainingTime = remainingTime;
        this.characters = characters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ALK");

        sb.append(remainingTime).append('|').append(characters.size());

        for (AccountCharacter character : characters) {
            sb
                .append('|')
                .append(character.id()).append(';')
                .append(character.spriteInfo().name()).append(';')
                .append(character.level()).append(';')
                .append(character.spriteInfo().gfxId()).append(';')
                .append(character.spriteInfo().colors().toHexString(";")).append(';')
                .append(character.spriteInfo().accessories()).append(';')
                .append(';') // @todo merchant
                .append(character.serverId()).append(';')
                .append(';') // @todo is dead
                .append(';') // @todo dead count
            // @todo level max
            ;
        }

        return sb.toString();
    }
}
