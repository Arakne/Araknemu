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
import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.sprite.SpriteSize;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Sprite for exploration player
 *
 * The sprite type ID MUST be the class id
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L764
 */
public final class PlayerSprite implements Sprite {
    private final ExplorationPlayer exploration;

    /**
     * Cached constant part of the sprite
     * The integrity of this field is ensured by the constantPartHash field
     */
    private @Nullable String constantPart = null;

    /**
     * Store a hash code of the last computed constant part
     */
    private int constPartHash = 0;

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
            constantPart()
        ;
    }

    /**
     * Get the part of the sprite which change rarely
     * This value is cached to avoid recomputation and allocation
     */
    private String constantPart() {
        final int id = id();
        final String name = name();
        final Race race = exploration.player().spriteInfo().race();
        final int gfxId = gfxId();
        final SpriteSize size = exploration.player().spriteInfo().size();
        final Gender gender = exploration.player().spriteInfo().gender();
        final Colors colors = exploration.player().spriteInfo().colors();
        final String accessories = exploration.player().spriteInfo().accessories().toString();
        final int restrictions = exploration.restrictions().toInt();
        final String lastResult = constantPart;

        int newHash = 17 * id;

        newHash = 31 * newHash + name.hashCode();
        newHash = 31 * newHash + race.hashCode();
        newHash = 31 * newHash + gfxId;
        newHash = 31 * newHash + size.hashCode();
        newHash = 31 * newHash + gender.hashCode();
        newHash = 31 * newHash + colors.hashCode();
        newHash = 31 * newHash + accessories.hashCode();
        newHash = 31 * newHash + restrictions;

        if (lastResult != null && newHash == constPartHash) {
            return lastResult;
        }

        constPartHash = newHash;

        return constantPart = "0;" + // bonus
            id + ";" +
            name + ";" +
            race.ordinal() + ";" + // @todo title
            gfxId + "^" + size + ";" +
            gender.ordinal() + ";" +
            ";" + // @todo alignment
            colors.toHexString(";") + ";" +
            accessories + ";" +
            ";" + // @todo aura
            ";;" + // @todo emote; emote timer
            ";;" + // @todo guild; guild emblem
            Integer.toString(restrictions, 36) + ";"
        // @todo mount
        ;
    }
}
