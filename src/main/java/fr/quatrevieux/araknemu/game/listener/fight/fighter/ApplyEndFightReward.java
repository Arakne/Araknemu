package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.ending.reward.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardVisitor;
import fr.quatrevieux.araknemu.game.fight.event.FightFinished;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Apply reward when fight is terminated
 */
final public class ApplyEndFightReward implements Listener<FightFinished>, FightRewardVisitor {
    final private PlayerFighter fighter;

    public ApplyEndFightReward(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FightFinished event) {
        fighter.player().stopFighting();
        event.reward().apply(this);
        fighter.player().save();
    }

    @Override
    public Class<FightFinished> event() {
        return FightFinished.class;
    }

    @Override
    public void onDropReward(DropReward reward) {
        if (reward.xp() != 0) {
            fighter.player().experience().add(reward.xp());
        }

        // @todo kamas,items...
    }
}
