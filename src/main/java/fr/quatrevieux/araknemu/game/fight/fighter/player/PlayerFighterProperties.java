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

package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.game.player.CharacterProperties;
import fr.quatrevieux.araknemu.game.player.experience.GamePlayerExperience;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;

/**
 * Wrap player fighter properties
 */
public final class PlayerFighterProperties implements CharacterProperties {
    private final PlayerFighter fighter;
    private final CharacterProperties baseProperties;

    private final PlayerFighterCharacteristics characteristics;
    private final PlayerFighterLife life;

    public PlayerFighterProperties(PlayerFighter fighter, CharacterProperties baseProperties) {
        this.fighter = fighter;
        this.baseProperties = baseProperties;

        this.characteristics = new PlayerFighterCharacteristics(baseProperties.characteristics(), fighter);
        this.life = new PlayerFighterLife(baseProperties.life(), fighter);
    }

    @Override
    public PlayerFighterCharacteristics characteristics() {
        return characteristics;
    }

    @Override
    public PlayerFighterLife life() {
        return life;
    }

    @Override
    public SpellBook spells() {
        return baseProperties.spells();
    }

    @Override
    public GamePlayerExperience experience() {
        return baseProperties.experience();
    }

    @Override
    public long kamas() {
        return baseProperties.kamas();
    }
}
