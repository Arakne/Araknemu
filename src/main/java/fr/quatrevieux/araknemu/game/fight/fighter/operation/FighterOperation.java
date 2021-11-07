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

package fr.quatrevieux.araknemu.game.fight.fighter.operation;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Operation to apply on a fighter creature
 * Implements the visitor pattern
 *
 * @see Fighter#apply(FighterOperation)
 */
public interface FighterOperation {
    /**
     * Apply the operation to a PlayerFighter
     */
    public default void onPlayer(PlayerFighter fighter) {
        onGenericFighter(fighter);
    }

    /**
     * Apply the operation to a MonsterFighter
     */
    public default void onMonster(MonsterFighter fighter) {
        onGenericFighter(fighter);
    }

    /**
     * Apply the operation to an InvocationFighter
     */
    default public void onInvocation(InvocationFighter fighter) {
        onGenericFighter(fighter);
    }

    /**
     * Apply the operation to a generic fighter type
     */
    public default void onGenericFighter(Fighter fighter) {}
}
