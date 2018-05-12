package fr.quatrevieux.araknemu.game.fight.ending.reward;

/**
 * Visitor for apply reward
 */
public interface FightRewardVisitor {
    /**
     * Apply a drop reward
     */
    public void onDropReward(DropReward reward);
}
