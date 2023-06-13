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

package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.util.ParseUtils;

/**
 * Factory for cast action
 */
public final class CastFactory implements CastActionFactory<PlayableFighter> {
    private final CastConstraintValidator<Spell> validator;
    private final CriticalityStrategy criticalityStrategy;

    public CastFactory(CastConstraintValidator<Spell> validator, CriticalityStrategy criticalityStrategy) {
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public Action create(PlayableFighter fighter, String[] arguments) {
        if (arguments.length < 2) {
            throw new IllegalArgumentException("Invalid cast arguments");
        }

        final FightMap map = fighter.fight().map();
        final SpellList spells = fighter.spells();

        final int spellId = Integer.parseInt(arguments[0]);
        final int cellId = ParseUtils.parseNonNegativeInt(arguments[1]);

        if (!spells.has(spellId)) {
            return new SpellNotFound(fighter);
        }

        if (cellId >= map.size()) {
            throw new IllegalArgumentException("Invalid target cell");
        }

        return create(fighter, spells.get(spellId), map.get(cellId));
    }

    @Override
    public ActionType type() {
        return ActionType.CAST;
    }

    @Override
    public Cast create(PlayableFighter performer, Spell spell, FightCell target) {
        return new Cast(performer, spell, target, validator, criticalityStrategy);
    }

    @Override
    public CastConstraintValidator<Spell> validator() {
        return validator;
    }
}
