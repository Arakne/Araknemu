package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.game.fight.ending.reward.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardVisitor;

/**
 * Apply fight rewards to player fighter
 */
final public class PlayerFighterRewardApplier implements FightRewardVisitor {
    final private PlayerFighter fighter;

    public PlayerFighterRewardApplier(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void onDropReward(DropReward reward) {
        if (reward.xp() != 0) {
            fighter.player().properties().experience().add(reward.xp());
        }

        // @todo kamas,items...
    }
}
