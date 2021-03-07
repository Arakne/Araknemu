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

import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Factory for cast action
 */
public final class CastFactory implements CastActionFactory {
    private final FightTurn turn;
    private final Fighter fighter;
    private final CastConstraintValidator<Spell> validator;
    private final CriticalityStrategy criticalityStrategy;

    public CastFactory(FightTurn turn) {
        this(turn, new SpellConstraintsValidator(), new BaseCriticalityStrategy(turn.fighter()));
    }

    public CastFactory(FightTurn turn, CastConstraintValidator<Spell> validator, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.fighter = turn.fighter();
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public Action create(String[] arguments) {
        final int spellId = Integer.parseInt(arguments[0]);

        return create(
            fighter.spells().has(spellId) ? fighter.spells().get(spellId) : null,
            turn.fight().map().get(Integer.parseInt(arguments[1]))
        );
    }

    @Override
    public ActionType type() {
        return ActionType.CAST;
    }

    @Override
    public Cast create(Spell spell, FightCell target) {
        return new Cast(turn, fighter, spell, target, validator, criticalityStrategy);
    }

    @Override
    public CastConstraintValidator<Spell> validator() {
        return validator;
    }
}
