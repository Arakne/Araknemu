package fr.quatrevieux.araknemu.data.world.entity.character;

/**
 * Player level and experience
 */
final public class PlayerExperience {
    final private int level;
    final private long experience;

    public PlayerExperience(int level, long experience) {
        this.level = level;
        this.experience = experience;
    }

    public int level() {
        return level;
    }

    public long experience() {
        return experience;
    }
}
