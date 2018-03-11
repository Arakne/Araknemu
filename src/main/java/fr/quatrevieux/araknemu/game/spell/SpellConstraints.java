package fr.quatrevieux.araknemu.game.spell;

import fr.quatrevieux.araknemu.data.value.Interval;

/**
 * Constraints for launch a spell
 */
public interface SpellConstraints {
    /**
     * Get the spell range
     */
    public Interval range();

    /**
     * Spell should be launch in line
     */
    public boolean lineLaunch();

    /**
     * Launch block by line of sight
     */
    public boolean lineOfSight();

    /**
     * Needs a free cell
     */
    public boolean freeCell();

    /**
     * Maximum number of launch per turn
     */
    public int launchPerTurn();

    /**
     * Maximum number of launch per target per turn
     */
    public int launchPerTarget();

    /**
     * Number of turns between two launch
     */
    public int launchDelay();

    /**
     * List of require states
     */
    public int[] requiredStates();

    /**
     * List of forbidden states
     */
    public int[] forbiddenStates();
}
