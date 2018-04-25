package fr.quatrevieux.araknemu.game.fight.turn.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for all fight actions
 */
public enum ActionType {
    NONE(0, 0),
    MOVE(1, 2),
    CAST(300, 0);

    final private int id;
    final private int end;

    final static private Map<Integer, ActionType> actionsById = new HashMap<>();

    static {
        for (ActionType actionType : values()) {
            actionsById.put(actionType.id, actionType);
        }
    }

    ActionType(int id, int end) {
        this.id = id;
        this.end = end;
    }

    /**
     * The game action id
     */
    public int id() {
        return id;
    }

    /**
     * The end action id
     */
    public int end() {
        return end;
    }

    /**
     * Get an action by id
     */
    static public ActionType byId(int id) {
        return actionsById.get(id);
    }
}
