package fr.quatrevieux.araknemu.data.world.entity.environment.npc;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.world.map.Direction;

/**
 * Store the NPC data
 */
final public class Npc {
    final private int id;
    final private int templateId;
    final private Position position;
    final private Direction orientation;
    final private int[] questions;

    public Npc(int id, int templateId, Position position, Direction orientation, int[] questions) {
        this.id = id;
        this.templateId = templateId;
        this.position = position;
        this.orientation = orientation;
        this.questions = questions;
    }

    /**
     * Get the NPC unique id
     */
    public int id() {
        return id;
    }

    /**
     * Get the template id
     *
     * @see NpcTemplate#id()
     */
    public int templateId() {
        return templateId;
    }

    /**
     * Get the NPC position on map
     */
    public Position position() {
        return position;
    }

    /**
     * Get the NPC sprite orientation
     */
    public Direction orientation() {
        return orientation;
    }

    /**
     * Get list of available questions ids on the NPC
     *
     * @see Question#id()
     */
    public int[] questions() {
        return questions;
    }
}
