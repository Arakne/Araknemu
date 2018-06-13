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

    /**
     * Check if the fighter is dead
     */
    public boolean dead();

    /**
     * Change fighter life
     *
     * @param caster The caster
     * @param value The modified value. Positive for heal, negative for damage
     */
    public int alter(Fighter caster, int value);

    /**
     * Kill the fighter
     */
    public void kill(Fighter caster);
}
