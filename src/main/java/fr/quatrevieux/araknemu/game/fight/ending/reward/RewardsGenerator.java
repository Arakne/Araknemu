package fr.quatrevieux.araknemu.game.fight.ending.reward;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;

/**
 * Generate rewards for end fight
 */
public interface RewardsGenerator {
    /**
     * Generate the rewards from the end fight results
     *
     * @param results The end fight results
     */
    public FightRewardsSheet generate(EndFightResults results);
}
