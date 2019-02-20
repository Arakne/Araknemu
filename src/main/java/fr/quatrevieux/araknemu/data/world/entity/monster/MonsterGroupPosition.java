package fr.quatrevieux.araknemu.data.world.entity.monster;

import fr.quatrevieux.araknemu.data.value.Position;

/**
 * Spawn position for the monster group on map
 */
final public class MonsterGroupPosition {
    final private Position position;
    final private int groupId;

    public MonsterGroupPosition(Position position, int groupId) {
        this.position = position;
        this.groupId = groupId;
    }

    /**
     * The group spawn position
     * The cell is not required : If the value is -1, the group may spawn in any free free
     *
     * The position is the primary key : this value is unique
     */
    public Position position() {
        return position;
    }

    /**
     * Foreign key for monster group id
     *
     * @see MonsterGroupData#id()
     */
    public int groupId() {
        return groupId;
    }
}
