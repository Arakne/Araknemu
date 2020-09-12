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

package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastActionFactory;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Utility class for cast spells
 */
final public class SpellCaster {
    final private Turn turn;
    final private CastConstraintValidator<Spell> validator;
    final private CastActionFactory factory;

    public SpellCaster(AI ai) {
        turn = ai.turn();
        factory = turn.actions().cast();
        validator = factory.validator();
    }

    /**
     * Validate the cast action
     */
    public boolean validate(Spell spell, FightCell target) {
        return validator.validate(turn, spell, target) == null;
    }

    /**
     * Create the action
     */
    public Action create(Spell spell, FightCell target) {
        return factory.create(spell, target);
    }
}
