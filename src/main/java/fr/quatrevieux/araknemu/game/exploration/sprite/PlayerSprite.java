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

package fr.quatrevieux.araknemu.game.exploration.sprite;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

/**
 * Sprite for exploration player
 *
 * The sprite type ID MUST be the class id
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L764
 */
public final class PlayerSprite implements Sprite {
    private final ExplorationPlayer exploration;

    public PlayerSprite(ExplorationPlayer exploration) {
        this.exploration = exploration;
    }

    @Override
    public int id() {
        return exploration.player().spriteInfo().id();
    }

    @Override
    public int cell() {
        return exploration.position().cell();
    }

    @Override
    public Direction orientation() {
        return exploration.orientation();
    }

    @Override
    public Type type() {
        return Type.PLAYER;
    }

    @Override
    public int gfxId() {
        return exploration.player().spriteInfo().gfxId();
    }

    @Override
    public String name() {
        return exploration.player().name();
    }

    /**
     * cell;direction;bonus;id;name;type,title;gfxID^"scaleX"x"scaleY";sex;alignment;color1;color2;color3;accessories;aura;emote;emoteTimer;guildName;guildEmblem;restrictions;mount,mountColor1,mountColor2,mountColor3
     */
    @Override
    public String toString() {
        return
            cell() + ";" +
            orientation().ordinal() + ";" +
            "0;" + // bonus
            id() + ";" +
            name() + ";" +
            exploration.player().spriteInfo().race().ordinal() + ";" + // @todo title
            gfxId() + "^" + exploration.player().spriteInfo().size() + ";" +
            exploration.player().spriteInfo().gender().ordinal() + ";" +
            ";" + // @todo alignment
            exploration.player().spriteInfo().colors().toHexString(";") + ";" +
            exploration.player().spriteInfo().accessories() + ";" +
            ";" + // @todo aura
            ";;" + // @todo emote; emote timer
            ";;" + // @todo guild; guild emblem
            Integer.toString(exploration.restrictions().toInt(), 36) + ";"
        // @todo mount
        ;
    }
}
