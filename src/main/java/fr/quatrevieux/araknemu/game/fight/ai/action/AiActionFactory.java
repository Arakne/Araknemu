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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.maps.path.Path;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Factory of actions performed by AI system
 */
public interface AiActionFactory<A extends Action> {
    /**
     * Cast a spell
     *
     * @param spell Spell to cast
     * @param target Target cell
     *
     * @return Action to perform
     *
     * @see AiActionFactory#castSpellValidator() For validate cast before perform action
     */
    public A cast(Spell spell, BattlefieldCell target);

    /**
     * Create a move action
     *
     * @param path Move path
     *
     * @return Action to perform
     */
    public A move(Path<BattlefieldCell> path);

    /**
     * Validator for cast spell action
     */
    public CastConstraintValidator<Spell> castSpellValidator();
}
