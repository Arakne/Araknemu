package fr.quatrevieux.araknemu.game.fight.ending.reward.generator;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;

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
