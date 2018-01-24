package fr.quatrevieux.araknemu.data.world.entity.environment;

import fr.quatrevieux.araknemu.game.exploration.map.trigger.CellAction;

/**
 * Map cell triggers
 * Perform an action when player arrive on the cell
 */
final public class MapTrigger {
    final private int id;
    final private int map;
    final private int cell;
    final private CellAction action;
    final private String arguments;
    final private String conditions;

    public MapTrigger(int id, int map, int cell, CellAction action, String arguments, String conditions) {
        this.id = id;
        this.map = map;
        this.cell = cell;
        this.action = action;
        this.arguments = arguments;
        this.conditions = conditions;
    }

    public int id() {
        return id;
    }

    public int map() {
        return map;
    }

    public int cell() {
        return cell;
    }

    public CellAction action() {
        return action;
    }

    public String arguments() {
        return arguments;
    }

    public String conditions() {
        return conditions;
    }
}
