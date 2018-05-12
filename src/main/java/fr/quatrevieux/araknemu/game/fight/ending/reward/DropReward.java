package fr.quatrevieux.araknemu.game.fight.ending.reward;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Reward for drop
 */
final public class DropReward implements FightReward {
    final private RewardType type;
    final private Fighter fighter;

    private long xp;
    private long guildXp;
    private long mountXp;
    private int kamas;

    public DropReward(RewardType type, Fighter fighter) {
        this(type, fighter, 0, 0);
    }

    public DropReward(RewardType type, Fighter fighter, long xp, int kamas) {
        this.type = type;
        this.fighter = fighter;
        this.xp = xp;
        this.kamas = kamas;
    }

    @Override
    public Fighter fighter() {
        return fighter;
    }

    @Override
    public RewardType type() {
        return type;
    }

    @Override
    public void apply(FightRewardVisitor visitor) {
        visitor.onDropReward(this);
    }

    public long xp() {
        return xp;
    }

    public long guildXp() {
        return guildXp;
    }

    public long mountXp() {
        return mountXp;
    }

    public int kamas() {
        return kamas;
    }
}
