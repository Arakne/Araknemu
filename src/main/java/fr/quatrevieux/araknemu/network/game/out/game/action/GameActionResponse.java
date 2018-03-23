package fr.quatrevieux.araknemu.network.game.out.game.action;

import fr.quatrevieux.araknemu.game.exploration.action.Action;
import fr.quatrevieux.araknemu.game.exploration.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.action.BlockingAction;

/**
 * Response for a game action
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L127
 *
 * @todo Change constructor and arguments
 */
final public class GameActionResponse {
    final private String id;
    final private ActionType type;
    final private String spriteId;
    final private Object[] arguments;

    public GameActionResponse(String id, ActionType type, String spriteId, Object[] arguments) {
        this.id = id;
        this.type = type;
        this.spriteId = spriteId;
        this.arguments = arguments;
    }

    public GameActionResponse(int id, ActionType type, int spriteId, Object... arguments) {
        this.id = Integer.toString(id);
        this.type = type;
        this.spriteId = Integer.toString(spriteId);
        this.arguments = arguments;
    }

    public GameActionResponse(Action action) {
        this("", action.type(), Integer.toString(action.performer().id()), action.arguments());
    }

    public GameActionResponse(BlockingAction action) {
        this(action.id(), action.type(), action.performer().id(), action.arguments());
    }

    @Override
    public String toString() {
        StringBuilder packet = new StringBuilder("GA" + id + ";" + type.id() + ";" + spriteId);

        for (Object argument : arguments) {
            packet.append(';').append(argument.toString());
        }

        return packet.toString();
    }
}
