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
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.FightAction;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveActionFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry of all fight actions factories
 */
public final class FightActionsFactoryRegistry implements ActionsFactory {
    private final CastActionFactory castFactory;
    private final FightActionFactory closeCombatFactory;
    private final MoveActionFactory moveFactory;

    private final Map<ActionType, FightActionFactory> factories = new EnumMap<>(ActionType.class);

    public FightActionsFactoryRegistry(MoveActionFactory moveFactory, CastActionFactory castFactory, FightActionFactory closeCombatFactory) {
        this.moveFactory = moveFactory;
        this.castFactory = castFactory;
        this.closeCombatFactory = closeCombatFactory;

        register(moveFactory);
        register(castFactory);
        register(closeCombatFactory);
    }

    @Override
    public FightAction create(PlayableFighter fighter, ActionType action, String[] arguments) {
        if (!factories.containsKey(action)) {
            throw new FightException("Fight action " + action + " not found");
        }

        return factories.get(action).create(fighter, arguments);
    }

    @Override
    public CastActionFactory cast() {
        return castFactory;
    }

    @Override
    public FightActionFactory closeCombat() {
        return closeCombatFactory;
    }

    @Override
    public MoveActionFactory move() {
        return moveFactory;
    }

    private void register(FightActionFactory factory) {
        factories.put(factory.type(), factory);
    }
}
