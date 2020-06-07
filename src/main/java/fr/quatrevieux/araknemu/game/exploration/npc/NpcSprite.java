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

package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

/**
 * Sprite for the NPC
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L630
 */
final public class NpcSprite implements Sprite {
    final private GameNpc npc;

    public NpcSprite(GameNpc npc) {
        this.npc = npc;
    }

    @Override
    public int id() {
        return npc.id();
    }

    @Override
    public int cell() {
        return npc.position().cell();
    }

    @Override
    public Direction orientation() {
        return npc.orientation();
    }

    @Override
    public Type type() {
        return Type.NPC;
    }

    @Override
    public String name() {
        return Integer.toString(npc.template().id());
    }

    @Override
    public String toString() {
        return
            cell() + ";" +
            orientation().ordinal() + ";" +
            "0;" + // Bonus
            id() + ";" +
            name() + ";" +
            type().id() + ";" +
            npc.template().gfxId() + "^" + npc.template().scaleX() + "x" + npc.template().scaleY() + ";" +
            npc.template().gender().ordinal() + ";" +
            npc.template().colors().toHexString(";") + ";" +
            npc.template().accessories() + ";" +
            (npc.template().extraClip() != -1 ? npc.template().extraClip() : "") + ";" +
            npc.template().customArtwork()
        ;
    }
}
