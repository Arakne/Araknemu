package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighterRewardApplier;

/**
 * Apply reward when fighter leave a fight
 */
final public class ApplyLeaveReward implements Listener<FightLeaved> {
    final private PlayerFighter fighter;

    public ApplyLeaveReward(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FightLeaved event) {
        event.reward().ifPresent(reward -> {
            reward.apply(new PlayerFighterRewardApplier(fighter));
            fighter.player().save();
        });
    }

    @Override
    public Class<FightLeaved> event() {
        return FightLeaved.class;
    }
}
