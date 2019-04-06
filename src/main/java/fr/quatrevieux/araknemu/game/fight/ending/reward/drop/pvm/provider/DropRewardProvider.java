package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;

/**
 * Base type for provide drop rewards data
 */
public interface DropRewardProvider {
    /**
     * Scoped provider for end fight results
     */
    public interface Scope {
        /**
         * Provide the reward data
         */
        public void provide(DropReward reward);
    }

    /**
     * Initialize the provider for the given fight results
     */
    public Scope initialize(EndFightResults results);
}
