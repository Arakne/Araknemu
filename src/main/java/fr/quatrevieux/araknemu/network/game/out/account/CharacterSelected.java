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

import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.object.ItemSerializer;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Confirm character select and start game
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L764
 */
public final class CharacterSelected {
    private final GamePlayer character;

    public CharacterSelected(GamePlayer character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return
            "ASK|" +
            character.id() + "|" +
            character.name() + "|" +
            character.properties().experience().level() + "|" +
            character.race().race().ordinal() + "|" +
            character.spriteInfo().gender().ordinal() + "|" +
            character.spriteInfo().gfxId() + "|" +
            character.spriteInfo().colors().toHexString("|") + "|" +
            StreamSupport.stream(character.inventory().spliterator(), false)
                .map(ItemSerializer::new)
                .map(Object::toString)
                .collect(Collectors.joining(";"))
        ;
    }
}
