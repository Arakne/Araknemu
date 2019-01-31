package fr.quatrevieux.araknemu.network.game.out.game.action;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;

/**
 * Response for a game action
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L127
 */
final public class GameActionResponse {
    /** Nul / cancel game action */
    final static public GameActionResponse NOOP = new GameActionResponse("", ActionType.NONE);

    final private String id;
    final private ActionType type;
    final private Object[] arguments;

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
        StringBuilder packet = new StringBuilder("GA" + id + ";" + type.id());

        for (Object argument : arguments) {
            packet.append(';').append(argument.toString());
        }

        return packet.toString();
    }

    static private Object[] makeArguments(int spriteId, Object[] arguments) {
        switch (arguments.length) {
            case 0:
                return new Object[] {spriteId};

            case 1:
                return new Object[] {spriteId, arguments[0]};

            default:
                Object[] newArguments = new Object[arguments.length + 1];

                newArguments[0] = spriteId;
                System.arraycopy(arguments, 0, newArguments, 1, arguments.length);

                return newArguments;
        }
    }
}
