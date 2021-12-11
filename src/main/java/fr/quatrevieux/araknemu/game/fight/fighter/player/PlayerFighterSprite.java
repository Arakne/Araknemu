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

package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.player.sprite.SpriteInfo;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

/**
 * Sprite for fighter
 *
 * The sprite type ID MUST be the class id
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L764
 */
public final class PlayerFighterSprite implements Sprite {
    private final PlayerFighter fighter;
    private final SpriteInfo spriteInfo;

    public PlayerFighterSprite(PlayerFighter fighter, SpriteInfo spriteInfo) {
        this.fighter = fighter;
        this.spriteInfo = spriteInfo;
    }

    @Override
    public int id() {
        return fighter.id();
    }

    @Override
    public int cell() {
        return fighter.dead() ? -1 : fighter.cell().id();
    }

    @Override
    public Direction orientation() {
        return fighter.orientation();
    }

    @Override
    public Type type() {
        return Type.PLAYER;
    }

    @Override
    public String name() {
        return spriteInfo.name();
    }

    @Override
    public String toString() {
        return
            cell() + ";" +
            fighter.orientation().ordinal() + ";" +
            "0;" + // Bonus value, not used on player
            id() + ";" +
            name() + ";" +
            spriteInfo.race().ordinal() + ";" +
            spriteInfo.gfxId() + "^" + spriteInfo.size() + ";" +
            spriteInfo.gender().ordinal() + ";" +
            fighter.properties().experience().level() + ";" +
            "0,0,0,0;" + // @todo alignment
            spriteInfo.colors().toHexString(";") + ";" +
            spriteInfo.accessories() + ";" +
            fighter.life().current() + ";" +
            fighter.characteristics().get(Characteristic.ACTION_POINT) + ";" +
            fighter.characteristics().get(Characteristic.MOVEMENT_POINT) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_NEUTRAL) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_EARTH) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_FIRE) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_WATER) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_AIR) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_ACTION_POINT) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_MOVEMENT_POINT) + ";" +
            fighter.team().number() + ";" +
            ";" // @todo mount
        ;
    }
}
