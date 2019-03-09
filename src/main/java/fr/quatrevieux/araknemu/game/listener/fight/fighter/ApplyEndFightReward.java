package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FightFinished;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Apply reward when fight is terminated
 */
final public class ApplyEndFightReward implements Listener<FightFinished> {
    final private PlayerFighter fighter;

    public ApplyEndFightReward(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FightFinished event) {
        fighter.player().stop(fighter);
        event.reward().apply();
        fighter.player().save();
    }

    @Override
    public Class<FightFinished> event() {
        return FightFinished.class;
    }
}
