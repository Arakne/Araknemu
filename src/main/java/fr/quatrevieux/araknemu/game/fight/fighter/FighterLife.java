package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.world.creature.Life;

/**
 * Handle the fighter life
 */
public interface FighterLife extends Life {
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
