package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.ending.reward.FightReward;

/**
 * The fight is terminated
 */
final public class FightFinished {
    final private FightReward reward;

    public FightFinished(FightReward reward) {
        this.reward = reward;
    }

    public FightReward reward() {
        return reward;
    }
}
