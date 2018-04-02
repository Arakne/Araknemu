package fr.quatrevieux.araknemu.game.player.experience.event;

/**
 * New player level reached
 */
final public class PlayerLevelUp {
    final private int level;

    public PlayerLevelUp(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }
}
