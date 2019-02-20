package fr.quatrevieux.araknemu.data.world.entity.monster;

import fr.quatrevieux.araknemu.data.value.Interval;

import java.util.List;

/**
 * Store information for generate monster groups
 */
final public class MonsterGroupData {
    final static public class Monster {
        final private int id;
        final private Interval level;

        public Monster(int id, Interval level) {
            this.id = id;
            this.level = level;
        }

        /**
         * Get the monster id
         *
         * @see MonsterTemplate#id()
         */
        public int id() {
            return id;
        }

        /**
         * Get the level interval
         */
        public Interval level() {
            return level;
        }
    }

    final private int id;
    final private long respawnTime;
    final private int maxSize;
    final private int maxCount;
    final private List<Monster> monsters;
    final private String comment;

    public MonsterGroupData(int id, long respawnTime, int maxSize, int maxCount, List<Monster> monsters, String comment) {
        this.id = id;
        this.respawnTime = respawnTime;
        this.maxSize = maxSize;
        this.maxCount = maxCount;
        this.monsters = monsters;
        this.comment = comment;
    }

    /**
     * The group id
     * This is the primary key of the group data
     */
    public int id() {
        return id;
    }

    /**
     * Get the group respawn time in milliseconds
     * The respawn timer will starts when group disappear (a fight is started)
     */
    public long respawnTime() {
        return respawnTime;
    }

    /**
     * Get the monsters data
     */
    public List<Monster> monsters() {
        return monsters;
    }

    /**
     * Human readable comment for the group data
     * Not used by the server
     */
    public String comment() {
        return comment;
    }

    /**
     * The maximal size of the group
     *
     * If the value is 0, all monsters on the data are present : a fixed group is generated
     * Else, a random group is generated, with size between [1, maxSize] include
     *
     * For dungeon groups, this value should be 0
     */
    public int maxSize() {
        return maxSize;
    }

    /**
     * Maximum number of occurrence of the group
     *
     * If the value is 1, only one group is spawn, and can respawn only when the previous group has start a fight
     *
     * For dungeon groups, this value should be 1
     */
    public int maxCount() {
        return maxCount;
    }
}
