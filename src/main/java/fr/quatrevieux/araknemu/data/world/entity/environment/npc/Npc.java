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

    public Npc(int id, int templateId, Position position, Direction orientation) {
        this.id = id;
        this.templateId = templateId;
        this.position = position;
        this.orientation = orientation;
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
}
