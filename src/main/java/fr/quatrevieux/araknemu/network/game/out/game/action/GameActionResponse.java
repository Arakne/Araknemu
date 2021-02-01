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

package fr.quatrevieux.araknemu.network.game.out.game.action;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;

/**
 * Response for a game action
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L127
 */
public final class GameActionResponse {
    /** Nul / cancel game action */
    public static final GameActionResponse NOOP = new GameActionResponse("", ActionType.NONE);

    private final String id;
    private final ActionType type;
    private final Object[] arguments;

    public GameActionResponse(String id, ActionType type, Object... arguments) {
        this.id = id;
        this.type = type;
        this.arguments = arguments;
    }

    public GameActionResponse(Action action) {
        this("", action.type(), makeArguments(action.performer().id(), action.arguments()));
    }

    public GameActionResponse(BlockingAction action) {
        this(Integer.toString(action.id()), action.type(), makeArguments(action.performer().id(), action.arguments()));
    }

    @Override
    public String toString() {
        final StringBuilder packet = new StringBuilder("GA" + id + ";" + type.id());

        for (Object argument : arguments) {
            packet.append(';').append(argument.toString());
        }

        return packet.toString();
    }

    private static Object[] makeArguments(int spriteId, Object[] arguments) {
        switch (arguments.length) {
            case 0:
                return new Object[] {spriteId};

            case 1:
                return new Object[] {spriteId, arguments[0]};
        }

        final Object[] newArguments = new Object[arguments.length + 1];

        newArguments[0] = spriteId;
        System.arraycopy(arguments, 0, newArguments, 1, arguments.length);

        return newArguments;
    }
}
