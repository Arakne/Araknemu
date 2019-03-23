package fr.quatrevieux.araknemu.data.world.entity.monster;

import fr.quatrevieux.araknemu.data.value.Interval;

/**
 * Store Pvm fight reward for each monsters
 *
 * Note: a monster can have no rewards
 */
final public class MonsterRewardData {
    final private int id;
    final private Interval kamas;
    final private long[] experiences;

    public MonsterRewardData(int id, Interval kamas, long[] experiences) {
        this.id = id;
        this.kamas = kamas;
        this.experiences = experiences;
    }

    /**
     * The monster template id (primary key)
     *
     * @see MonsterTemplate#id()
     */
    public int id() {
        return id;
    }

    /**
     * Interval of win kamas
     */
    public Interval kamas() {
        return kamas;
    }

    /**
     * Reward experience per grade
     */
    public long[] experiences() {
        return experiences;
    }
}
