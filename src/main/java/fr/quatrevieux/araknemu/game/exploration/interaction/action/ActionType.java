package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for all game actions
 */
public enum ActionType {
    NONE(0),
    MOVE(1),
    CHANGE_MAP(2),
    FIREWORK(228),
    CHALLENGE(900),
    ACCEPT_CHALLENGE(901),
    REFUSE_CHALLENGE(902),
    JOIN_FIGHT_ERROR(903);

    final private int id;

    final static private Map<Integer, ActionType> actionsById = new HashMap<>();

    static {
        for (ActionType actionType : values()) {
            actionsById.put(actionType.id, actionType);
        }
    }

    ActionType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    /**
     * Get an action by id
     */
    static public ActionType byId(int id) {
        return actionsById.get(id);
    }
}
