package fr.quatrevieux.araknemu.game.fight.fighter;

/**
 * Handle the fighter life
 */
public interface FighterLife {
    /**
     * Get the fighter current life
     */
    public int current();

    /**
     * Get the maximum life of the fighter
     */
    public int max();
}
