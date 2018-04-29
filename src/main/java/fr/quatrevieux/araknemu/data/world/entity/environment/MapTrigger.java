package fr.quatrevieux.araknemu.data.world.entity.environment;

/**
 * Map cell triggers
 * Perform an action when player arrive on the cell
 */
final public class MapTrigger {
    final private int map;
    final private int cell;
    final private int action;
    final private String arguments;
    final private String conditions;

    public MapTrigger(int map, int cell, int action, String arguments, String conditions) {
        this.map = map;
        this.cell = cell;
        this.action = action;
        this.arguments = arguments;
        this.conditions = conditions;
    }

    public int map() {
        return map;
    }

    public int cell() {
        return cell;
    }

    public int action() {
        return action;
    }

    public String arguments() {
        return arguments;
    }

    public String conditions() {
        return conditions;
    }
}
