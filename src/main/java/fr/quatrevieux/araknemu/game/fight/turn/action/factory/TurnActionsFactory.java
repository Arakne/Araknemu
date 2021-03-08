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

package fr.quatrevieux.araknemu.game.fight.turn.action.factory;

import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombatFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * Action factory for all turn actions
 */
public final class TurnActionsFactory implements ActionsFactory {
    private final CastFactory castFactory;
    private final CloseCombatFactory closeCombatFactory;
    private final MoveFactory moveFactory;

    private final Map<ActionType, FightActionFactory> factories = new EnumMap<>(ActionType.class);

    public TurnActionsFactory(FightTurn turn) {
        this.moveFactory = new MoveFactory(turn);
        this.castFactory = new CastFactory(turn);
        this.closeCombatFactory = new CloseCombatFactory(turn);

        register(moveFactory);
        register(castFactory);
        register(closeCombatFactory);
    }

    @Override
    public Action create(ActionType action, String[] arguments) {
        if (!factories.containsKey(action)) {
            throw new FightException("Fight action " + action + " not found");
        }

        return factories.get(action).create(arguments);
    }

    @Override
    public CastFactory cast() {
        return castFactory;
    }

    @Override
    public CloseCombatFactory closeCombat() {
        return closeCombatFactory;
    }

    @Override
    public MoveFactory move() {
        return moveFactory;
    }

    private void register(FightActionFactory factory) {
        factories.put(factory.type(), factory);
    }
}
