package fr.quatrevieux.araknemu.game.fight.ending.reward;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;

import java.util.List;

/**
 * Contains all end fight rewards
 */
final public class FightRewardsSheet {
    public enum Type {
        /** For drop, pvm, challenge...) */
        NORMAL,
        /** For honour fight (conquest, PvP) */
        HONOUR
    }

    final private EndFightResults results;
    final private Type type;
    final private List<DropReward> rewards;

    public FightRewardsSheet(EndFightResults results, Type type, List<DropReward> rewards) {
        this.results = results;
        this.type = type;
        this.rewards = rewards;
    }

    /**
     * Get the end fight results
     */
    public EndFightResults results() {
        return results;
    }

    /**
     * Get the reward type
     */
    public Type type() {
        return type;
    }

    /**
     * Get all rewards
     */
    public List<DropReward> rewards() {
        return rewards;
    }
}
