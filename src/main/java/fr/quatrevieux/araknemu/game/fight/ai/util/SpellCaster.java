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
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Utility class for cast spells
 */
public final class SpellCaster {
    private final AI ai;

    public SpellCaster(AI ai) {
        this.ai = ai;
    }

    /**
     * Validate the cast action
     */
    public boolean validate(Spell spell, FightCell target) {
        // A non-walkable cell can't be a valid target
        if (!target.walkableIgnoreFighter()) {
            return false;
        }

        final Turn turn = ai.turn();
        final CastConstraintValidator<Spell> validator = turn.actions().cast().validator();

        return validator.check(turn, spell, target);
    }

    /**
     * Create the action
     */
    public Action create(Spell spell, FightCell target) {
        return ai.turn().actions().cast().create(spell, target);
    }
}
