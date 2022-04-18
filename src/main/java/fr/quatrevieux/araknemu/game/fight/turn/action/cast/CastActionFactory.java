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

package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionFactory;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Factory for create a cast action
 */
public interface CastActionFactory<F extends ActiveFighter> extends FightActionFactory<F> {
    /**
     * Create the cast action
     *
     * @param performer Fighter which perform the action
     * @param spell  The spell to cast
     * @param target The cell target
     */
    public Action create(F performer, Spell spell, FightCell target);

    /**
     * Get the spell cast validator
     */
    public CastConstraintValidator<Spell> validator();
}
